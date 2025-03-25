package com.hbm.api.annotations;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface to declare that all fields, methods, and parameters in a class are {@link @NotNull}
 */
@NotNull
@Nonnull//Note: Must use the javax nonnull for intellij to recognize it properly in override warnings
@TypeQualifierDefault({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.CLASS)
public @interface NothingNullByDefault {
}