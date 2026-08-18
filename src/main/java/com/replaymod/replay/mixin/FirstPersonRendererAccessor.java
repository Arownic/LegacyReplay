package com.replaymod.replay.mixin;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public interface FirstPersonRendererAccessor {
    @Accessor("itemToRender")
    void replaymodCompat$setItemToRender(ItemStack paramItemStack);

    @Accessor("equippedItemSlot")
    void replaymodCompat$setEquippedItemSlot(int paramInt);

    @Accessor("equippedProgress")
    void replaymodCompat$setEquippedProgress(float paramFloat);

    @Accessor("prevEquippedProgress")
    void replaymodCompat$setPrevEquippedProgress(float paramFloat);
}
