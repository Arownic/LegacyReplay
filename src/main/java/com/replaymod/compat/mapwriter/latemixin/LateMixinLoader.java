package com.replaymod.compat.mapwriter.latemixin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Registers MixinMwForge with UniMixins' GTNHMixins-compatible "late mixin" phase - the correct
 * mechanism for a mixin that targets another mod's class (mapwriter.forge.MwForge) rather than
 * vanilla/Forge.
 *
 * Deliberately NOT an IFMLLoadingPlugin/coremod: UniMixins instantiates any @LateMixin-annotated
 * class itself, at the point mixins are being queued, through the normal mod classloader. Coremods
 * are constructed earlier, through a different classloader path, which is what caused the
 * LinkageError before (our old coremod tried to call MixinBootstrap.init() itself, colliding with
 * the Mixin instance UniMixins already bootstraps). This class touches no Mixin API at all beyond
 * this plain interface/annotation contract - UniMixins does all the actual work.
 */
@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.compat.mapwriter.late.replaymod.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        // Only bother queuing the mixin if MapWriter is actually present - harmless either way
        // since Mixin itself no-ops against a class it never encounters, but this keeps things
        // explicit and avoids any log noise about MwForge not being found when it's not installed.
        if (loadedMods.contains("MapWriter")) {
            return Collections.singletonList("MixinMwForge");
        }
        return Collections.emptyList();
    }
}
