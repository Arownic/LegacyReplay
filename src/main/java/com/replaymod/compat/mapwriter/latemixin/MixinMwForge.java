package com.replaymod.compat.mapwriter.latemixin;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.network.NetworkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(targets = "mapwriter.forge.MwForge")
public abstract class MixinMwForge {

    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(
            method = "onConnected(Lcpw/mods/fml/common/network/FMLNetworkEvent$ClientConnectedToServerEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void replaymodcompat_guardNonInetAddress(
            FMLNetworkEvent.ClientConnectedToServerEvent event, CallbackInfo ci) {
        if (event.isLocal) {
            return; // MapWriter already skips its own risky code path for this case.
        }

        NetworkManager manager = event.manager;
        if (manager == null) {
            return;
        }

        SocketAddress remote;
        try {
            remote = manager.getRemoteAddress();
        } catch (Throwable t) {
            // If we can't even ask, don't let that itself crash the game - just skip.
            LOGGER.warn("[ReplayModCompat] Failed to inspect NetworkManager remote address; " +
                    "skipping MapWriter's onConnected for this connection.", t);
            ci.cancel();
            return;
        }

        if (!(remote instanceof InetSocketAddress)) {
            LOGGER.info("[ReplayModCompat] Non-IP connection detected (e.g. a Replay Mod " +
                    "internal connection) - skipping MapWriter's onConnected to avoid its " +
                    "ClassCastException.");
            ci.cancel();
        }
    }
}
