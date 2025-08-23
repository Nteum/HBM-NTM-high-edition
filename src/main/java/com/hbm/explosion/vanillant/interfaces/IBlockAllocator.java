package com.hbm.explosion.vanillant.interfaces;

import com.hbm.explosion.vanillant.ExplosionVNT;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;

/**
 * 对应原版Explosion中explode函数计算受影响方块的部分。
 * 返回破坏的方块列表
 * */
public interface IBlockAllocator {

	HashSet<BlockPos> allocate(ExplosionVNT explosion, Level world, double x, double y, double z, float size);
}
