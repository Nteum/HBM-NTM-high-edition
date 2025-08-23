package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.IBlockMutator;
import com.hbm.explosion.vanillant.interfaces.IBlockProcessor;
import com.hbm.explosion.vanillant.interfaces.IDropChanceMutator;
import com.hbm.explosion.vanillant.interfaces.IFortuneMutator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Iterator;

public class BlockProcessorStandard implements IBlockProcessor {
	
	protected IDropChanceMutator chance;
	protected IFortuneMutator fortune;
	protected IBlockMutator convert;
	
	public BlockProcessorStandard() { }
	
	public BlockProcessorStandard withChance(IDropChanceMutator chance) {
		this.chance = chance;
		return this;
	}
	
	public BlockProcessorStandard withFortune(IFortuneMutator fortune) {
		this.fortune = fortune;
		return this;
	}
	
	public BlockProcessorStandard withBlockEffect(IBlockMutator convert) {
		this.convert = convert;
		return this;
	}

	@Override
	public void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashSet<BlockPos> affectedBlocks) {

		Iterator<BlockPos> iterator = affectedBlocks.iterator();
		float dropChance = 1.0F / explosion.size;
		
		while(iterator.hasNext()) {
			BlockPos blockPos = iterator.next();
			BlockState blockState = world.getBlockState(blockPos);

			if(!blockState.isAir()) {
				if(blockState.canDropFromExplosion(world, blockPos, explosion.innerInstance)) {
					if(chance != null) {
						dropChance = chance.mutateDropChance(explosion, blockState.getBlock(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), dropChance);
					}
					
					int dropFortune = fortune == null ? 0 : fortune.mutateFortune(explosion, blockState.getBlock(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
					
//					blockState.dropBlockAsItemWithChance(world, blockX, blockY, blockZ, world.getBlockMetadata(blockX, blockY, blockZ), dropChance, dropFortune);

				}
				
				blockState.onBlockExploded(world, blockPos, explosion.innerInstance);
				if(this.convert != null) this.convert.mutatePre(explosion, blockState.getBlock(), 0, blockPos.getX(), blockPos.getY(), blockPos.getZ());
			} else {
				iterator.remove();
			}
		}
		
		
		if(this.convert != null) {
			iterator = affectedBlocks.iterator();
			
			while(iterator.hasNext()) {
				BlockPos blockPos = iterator.next();
				BlockState blockState = world.getBlockState(blockPos);
				
				if(!blockState.isAir()) {
					this.convert.mutatePost(explosion, blockPos.getX(), blockPos.getY(), blockPos.getZ());
				}
			}
		}
	}

	public BlockProcessorStandard setNoDrop() {
		this.chance = new DropChanceMutatorStandard(0F);
		return this;
	}
	public BlockProcessorStandard setAllDrop() {
		this.chance = new DropChanceMutatorStandard(1F);
		return this;
	}
	public BlockProcessorStandard setFortune(int fortune) {
		this.fortune = new IFortuneMutator() { //no standard class because we only have one case thus far
			@Override
			public int mutateFortune(ExplosionVNT explosion, Block block, int x, int y, int z) {
				return fortune;
			}
		};
		return this;
	}
}
