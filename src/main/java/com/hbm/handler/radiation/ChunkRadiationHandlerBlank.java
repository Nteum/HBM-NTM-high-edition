package com.hbm.handler.radiation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ChunkRadiationHandlerBlank extends ChunkRadiationHandler {
	@Override
	public void updateSystem() {

	}

	@Override
	public float getRadiation(Level level, BlockPos pos) {
		return 0;
	}

	@Override
	public void setRadiation(Level level, BlockPos pos, float rad) {

	}

	@Override
	public void incrementRad(Level level, BlockPos pos, float rad) {

	}

	@Override
	public void decrementRad(Level level, BlockPos pos, float rad) {

	}

	@Override
	public void clearSystem(Level level) {

	}
}
