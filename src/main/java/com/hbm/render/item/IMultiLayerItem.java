package com.hbm.render.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMultiLayerItem {
    List<ResourceLocation> getLayers(ItemStack stack);
}
