package com.hbm.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public interface IDesignatorItem {

	boolean isReady(Level world, ItemStack stack, BlockPos pos);

	Vec3 getCoords(Level world, ItemStack stack, BlockPos pos);
}
