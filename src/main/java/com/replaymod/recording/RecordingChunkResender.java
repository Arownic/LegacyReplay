package com.replaymod.recording;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.List;

import com.replaymod.recording.packet.PacketListener;

/**
 * Builds fresh, self-contained chunk packets from the chunks currently loaded on the
 * client and feeds them into an in-progress recording.
 *
 * This exists because, for the singleplayer integrated server, the packet objects that
 * reach PacketListener for the INITIAL chunk burst on world join were never actually run
 * through Packet#readPacketData (see MixinS26PacketMapChunkBulk) -- they're the same
 * object the integrated server built for sending, handed straight to the client without a
 * real serialize/deserialize round trip. That means there's no raw wire data to capture
 * for them, and any attempt to re-serialize the original object on save produces corrupt
 * bytes.
 *
 * Rather than depend on capturing bytes at all, this rebuilds equivalent packets directly
 * from the client's own loaded Chunk objects, using the same "sending" constructor real
 * servers use. Compression happens lazily inside writePacketData itself (gated by each
 * packet's own deflateGate semaphore), so these packets are guaranteed self-consistent
 * regardless of how the original ones were delivered.
 */
public class RecordingChunkResender {

    // Batch size for each S26PacketMapChunkBulk, mirroring vanilla's own chunk-watch
    // batching so no single packet gets unreasonably large.
    private static final int BATCH_SIZE = 64;

    public static void resendLoadedChunks(PacketListener packetListener) {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
        if (world == null || player == null) {
            return;
        }

        IChunkProvider chunkProviderRaw = world.getChunkProvider();
        if (!(chunkProviderRaw instanceof ChunkProviderClient)) {
            return;
        }
        ChunkProviderClient chunkProvider = (ChunkProviderClient) chunkProviderRaw;

        int renderDistance = mc.gameSettings.renderDistanceChunks;
        int playerChunkX = (int) player.posX >> 4;
        int playerChunkZ = (int) player.posZ >> 4;

        List<Chunk> batch = new ArrayList<Chunk>();

        for (int dx = -renderDistance; dx <= renderDistance; dx++) {
            for (int dz = -renderDistance; dz <= renderDistance; dz++) {
                int chunkX = playerChunkX + dx;
                int chunkZ = playerChunkZ + dz;

                // chunkExists is the public, safe way to check load state without
                // triggering ChunkProviderClient to hand back its placeholder "empty"
                // chunk for coordinates that were never actually sent by the server.
                if (!chunkProvider.chunkExists(chunkX, chunkZ)) {
                    continue;
                }

                Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }

                batch.add(chunk);
                if (batch.size() >= BATCH_SIZE) {
                    flushBatch(packetListener, batch);
                }
            }
        }

        if (!batch.isEmpty()) {
            flushBatch(packetListener, batch);
        }
    }

    private static void flushBatch(PacketListener packetListener, List<Chunk> batch) {
        // S26PacketMapChunkBulk(List<Chunk>) is the real vanilla "sending" constructor --
        // it extracts full block/light/biome data from each chunk right here, and leaves
        // compression to run lazily the first time writePacketData is called on it, so
        // packetListener.save(...) below will compress it correctly on its own thread.
        S26PacketMapChunkBulk packet = new S26PacketMapChunkBulk(new ArrayList<Chunk>(batch));
        packetListener.save(packet);
        batch.clear();
    }
}