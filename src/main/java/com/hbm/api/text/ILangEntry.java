package com.hbm.api.text;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
//ref:mek
/**
 * Helper interface for creating formatted translations in our lang enums
 */
@MethodsReturnNonnullByDefault
public interface ILangEntry extends IHasTranslationKey {

    /**
     * Translates this {@link ILangEntry} using a "smart" replacement scheme to allow for automatic replacements, and coloring to take place.
     */
    default MutableComponent translate(Object... args) {
        return TextComponentUtil.smartTranslate(key(), args);
    }

    /**
     * Translates this {@link ILangEntry} and applies the {@link TextColor} of the given {@link EnumColor} to the {@link Component}.
     */
    default MutableComponent translateColored(EnumColor color, Object... args) {
        return translateColored(color.getColor(), args);
    }

    /**
     * Translates this {@link ILangEntry} and applies the {@link TextColor} to the {@link Component}.
     *
     * @since 10.4.0
     */
    default MutableComponent translateColored(TextColor color, Object... args) {
        return TextComponentUtil.build(color, translate(args));
    }
}