package com.hbm.item;

import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

/**
 * Items implementing this interface can inject multiple representative ItemStacks
 * into their creative tab instead of relying on the default single stack.
 */
public interface CreativeTabVariantItem {

    void fillCreativeTab(BuildCreativeModeTabContentsEvent event);
}
