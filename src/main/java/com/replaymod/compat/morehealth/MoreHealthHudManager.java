package com.replaymod.compat.morehealth;

import com.nohero.morehealth.mod_moreHealthEnhanced;
import de.johni0702.minecraft.gui.utils.EventRegistrations;
import com.replaymod.replay.ReplayHandler;
import com.replaymod.replay.events.ReplayClosedCallback;
import com.replaymod.replay.events.ReplayOpenedCallback;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class MoreHealthHudManager extends EventRegistrations {

    private boolean prevRenderCustomGUI;
    private boolean prevMinimalisticGUI;
    private boolean inReplay;

    { on(ReplayOpenedCallback.EVENT, this::disableGUI); }
    public void disableGUI(ReplayHandler handler) {
        prevRenderCustomGUI = mod_moreHealthEnhanced.renderCustomGUI;
        prevMinimalisticGUI = mod_moreHealthEnhanced.minimalisticGUI;

        mod_moreHealthEnhanced.renderCustomGUI = false;
        mod_moreHealthEnhanced.minimalisticGUI = false;
        inReplay = true;
    }

    { on(ReplayClosedCallback.EVENT, this::reenableGUI); }
    public void reenableGUI(ReplayHandler handler) {
        mod_moreHealthEnhanced.renderCustomGUI = prevRenderCustomGUI;
        mod_moreHealthEnhanced.minimalisticGUI = prevMinimalisticGUI;
        inReplay = false;
    }

    // Runs before MoreHealthHUD's own (default-priority) listeners, so when
    // we cancel here, MoreHealthHUD.modifyHealthHUD/modifyArmorHUD/modifyAirHUD
    // never even get invoked (Forge skips non-receiveCanceled listeners once
    // an event is already canceled). This also cancels vanilla's own render,
    // since vanilla's overlay code checks event.isCanceled() too.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void hideVanillaHud(RenderGameOverlayEvent.Pre event) {
        if (!inReplay) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.HEALTH
                || event.type == RenderGameOverlayEvent.ElementType.ARMOR
                || event.type == RenderGameOverlayEvent.ElementType.AIR) {
            event.setCanceled(true);
        }
    }
}