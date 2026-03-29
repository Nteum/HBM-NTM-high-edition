package com.hbm.block.env;


import com.hbm.block.HBMBlockComponent;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class WasteLeaves extends LeavesBlock {

	public WasteLeaves(Properties pProperties) {
		super(pProperties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pRandom.nextInt(30) == 0){
			pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
			//生成掉落物
			if (pLevel.getBlockState(pPos).isAir()){
				ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, new ItemStack(HBMBlockComponent.WAST_LEAVES.get()));
				pLevel.addFreshEntity(itemEntity);
			}
		}
		super.tick(pState,pLevel,pPos,pRandom);
	}
}