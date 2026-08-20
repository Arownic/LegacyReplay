package com.replaymod.core;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import com.replaymod.core.asm.GLErrorTransformer;
import com.replaymod.core.asm.GLStateTrackerTransformer;

import java.util.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

@IFMLLoadingPlugin.TransformerExclusions("com.replaymod.core.asm.")
public class LoadingPlugin implements IFMLLoadingPlugin {

    public LoadingPlugin() {
        if (Launch.blackboard.get("fml.deobfuscatedEnvironment") != Boolean.FALSE) {
            // Outside of the dev env, this is the job of the tweaker
            MixinBootstrap.init();
        }

        Mixins.addConfiguration("mixins.core.replaymod.json");
        Mixins.addConfiguration("mixins.recording.replaymod.json");
        Mixins.addConfiguration("mixins.render.replaymod.json");
        Mixins.addConfiguration("mixins.render.blend.replaymod.json");
        Mixins.addConfiguration("mixins.replay.replaymod.json");
        Mixins.addConfiguration("mixins.compat.mapwriter.replaymod.json");
        Mixins.addConfiguration("mixins.compat.battlegear.replaymod.json");
        Mixins.addConfiguration("mixins.compat.shaders.replaymod.json");
        Mixins.addConfiguration("mixins.extras.playeroverview.replaymod.json");

        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL location = codeSource.getLocation();
            try {
                File file = new File(location.toURI());
                if (file.isFile()) {
                    // This forces forge to reexamine the jar file for FML mods
                    // Should eventually be handled by Mixin itself, maybe?
                    CoreModManager.getLoadedCoremods().remove(file.getName());
                    CoreModManager.getReparseableCoremods().add(file.getName());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            LogManager.getLogger().warn("No CodeSource, if this is not a development environment we might run into problems!");
            LogManager.getLogger().warn(getClass().getProtectionDomain());
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        List<String> transformers = new ArrayList<>();
        if ("true".equals(System.getProperty("replaymod.glerrors", "false"))) {
            transformers.add(GLErrorTransformer.class.getName());
        }
        transformers.add(GLStateTrackerTransformer.class.getName());
        return transformers.stream().toArray(String[]::new);
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @LateMixin
    public static class MapwriterLateMixins implements ILateMixinLoader {

        @Override
        public String getMixinConfig() {
            return "mixins.compat.mapwriter.late.replaymod.json";
        }

        @Override
        public List<String> getMixins(Set<String> loadedMods) {
            if (loadedMods.contains("MapWriter")) {
                return Collections.singletonList("MixinMwForge");
            }
            return Collections.emptyList();
        }
    }
}
