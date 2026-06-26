package com.hbm.api.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface IDepthRockTool {
	public boolean canBreakRock(Level world, Player player, ItemStack tool, Block block, BlockPos pos);
}
