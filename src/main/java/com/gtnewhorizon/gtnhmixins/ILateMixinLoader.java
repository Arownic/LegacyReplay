package com.gtnewhorizon.gtnhmixins;

import java.util.List;
import java.util.Set;

/**
 * NOT our code - this is a compile-time-only copy of UniMixins/GTNHMixins' public API
 * (com.gtnewhorizon.gtnhmixins.ILateMixinLoader), copied verbatim from
 * https://github.com/GTNH-Museum/GTNHMixins/blob/master/src/main/java/com/gtnewhorizon/gtnhmixins/ILateMixinLoader.java
 * so this project can compile without needing UniMixins' jar in libs/.
 *
 * At runtime, the actual UniMixins-provided class with this exact name is what gets used -
 * Forge's LaunchClassLoader resolves a single canonical class per fully-qualified name across
 * all mod jars, so having our own copy here does not create a duplicate/conflicting definition
 * the way bundling an actual Mixin implementation would (that was the cause of the earlier
 * LinkageError - this interface is just a plain compile-time contract, not a running subsystem).
 */
public interface ILateMixinLoader {
    String getMixinConfig();
    List<String> getMixins(Set<String> loadedMods);
}
