package com.hbm.block.env;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
/** 废土 */
public class WasteEarth extends Block {
	public WasteEarth(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		super.stepOn(pLevel, pPos, pState, pEntity);
		if (pEntity instanceof LivingEntity livingEntity){

		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
		super.animateTick(pState, pLevel, pPos, pRandom);

	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		super.tick(pState, pLevel, pPos, pRandom);

	}

	@Override
	public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(state, level, pos, neighbor);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		return plantable.getPlantType(world,pos) == PlantType.CAVE;
	}
}
