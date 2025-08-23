package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
/**
 * 对应Explosion实例中explode方法的第二部分，计算受爆炸影响的实体。
 * */
public interface IBlockProcessor {

	public void process(ExplosionVNT explosion, Level world, double x, double y, double z, HashSet<BlockPos> affectedBlocks);
}
