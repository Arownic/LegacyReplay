package com.replaymod.compat.battlegear2.mixin;

import mods.battlegear2.packet.AbstractMBPacket;
import mods.battlegear2.packet.BattlegearAnimationPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BattlegearAnimationPacket.class)
public abstract class MixinCaptureAnimationForRecording extends AbstractMBPacket {
    /**
     * Runs at the end of process(), after the existing tracker broadcast
     * and local processAnimation() call.
     */
    @Inject(method = "process", at = @At("TAIL"), remap = false)
    private void replaymodcompat$sendToSelfForRecording(ByteBuf in, EntityPlayer player, CallbackInfo ci) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayerMP self = (EntityPlayerMP) player;
        if (!(self.worldObj instanceof WorldServer)) {
            return;
        }
        self.playerNetServerHandler.sendPacket(this.generatePacket());
    }
}