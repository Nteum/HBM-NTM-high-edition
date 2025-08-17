package com.hbm.api.text;

import net.minecraft.MethodsReturnNonnullByDefault;
/**来自mekanism
 * */
@MethodsReturnNonnullByDefault
public interface IHasTranslationKey {

    /**
     * Gets the translation key for this object.
     */
    String key();
}