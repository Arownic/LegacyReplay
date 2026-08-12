package com.gtnewhorizon.gtnhmixins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NOT our code - compile-time-only copy, see ILateMixinLoader.java in this same package for why.
 * Copied verbatim from
 * https://github.com/GTNH-Museum/GTNHMixins/blob/master/src/main/java/com/gtnewhorizon/gtnhmixins/LateMixin.java
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE})
public @interface LateMixin {}
