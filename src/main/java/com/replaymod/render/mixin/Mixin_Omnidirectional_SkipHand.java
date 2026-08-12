package com.replaymod.render.mixin;

import com.replaymod.render.hooks.EntityRendererHandler;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import de.johni0702.minecraft.gui.versions.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class Mixin_Omnidirectional_SkipHand implements EntityRendererHandler.IEntityRenderer {
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void replayModRender_renderSpectatorHand(
            //#if MC>=11500
            //$$ MatrixStack matrixStack,
            //#endif
            //#if MC>=11400
            //$$ ActiveRenderInfo camera,
            //#endif
            float partialTicks,
            //#if MC<11400
            int renderPass,
            //#endif
            CallbackInfo ci
    ) {
        EntityRendererHandler handler = replayModRender_getHandler();
        if (handler != null && handler.omnidirectional) {
            // No spectator hands during 360° view, we wouldn't even know where to put it
            ci.cancel();
        }
    }
}
