package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockAllocator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.HashSet;
import java.util.Optional;

public class BlockAllocatorStandard implements IBlockAllocator {

	protected int resolution;

	public BlockAllocatorStandard() {
		this(16);
	}

	public BlockAllocatorStandard(int resolution) {
		this.resolution = resolution;
	}

	@Override
	public HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
		HashSet<BlockPos> affectedBlocks = new HashSet();

		for(int i = 0; i < this.resolution; ++i) {
			for(int j = 0; j < this.resolution; ++j) {
				for(int k = 0; k < this.resolution; ++k) {

					if(i == 0 || i == this.resolution - 1 || j == 0 || j == this.resolution - 1 || k == 0 || k == this.resolution - 1) {

						double d0 = (double) ((float) i / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						double d1 = (double) ((float) j / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						double d2 = (double) ((float) k / ((float) this.resolution - 1.0F) * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

						d0 /= d3;
						d1 /= d3;
						d2 /= d3;

						float powerRemaining = size * (0.7F + world.random.nextFloat() * 0.6F);
						double currentX = x;
						double currentY = y;
						double currentZ = z;

						for(float stepSize = 0.3F; powerRemaining > 0.0F; powerRemaining -= stepSize * 0.75F) {

							int blockX = Mth.floor(currentX);
							int blockY = Mth.floor(currentY);
							int blockZ = Mth.floor(currentZ);
							BlockPos blockPos = new BlockPos(blockX, blockY, blockZ);
							BlockState blockState = world.getBlockState(blockPos);
							FluidState fluidState = world.getFluidState(blockPos);

							if (!world.isInWorldBounds(blockPos)) {
								break;
							}

							if(!blockState.isAir()) {
								Optional<Float> optional = blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(blockState.getExplosionResistance(world, blockPos, explosion.innerInstance), fluidState.getExplosionResistance(world, blockPos, explosion.innerInstance)));
								if (optional.isPresent()) {
									powerRemaining -= (optional.get() + 0.3F) * 0.3F;
								}
							}

							if(powerRemaining > 0.0F && (explosion.exploder == null || explosion.exploder.shouldBlockExplode(explosion.innerInstance, world, blockPos, blockState, powerRemaining))) {
								affectedBlocks.add(blockPos);
							}

							currentX += d0 * (double) stepSize;
							currentY += d1 * (double) stepSize;
							currentZ += d2 * (double) stepSize;
						}
					}
				}
			}
		}

		return affectedBlocks;
	}

}
