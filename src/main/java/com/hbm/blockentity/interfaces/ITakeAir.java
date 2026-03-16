package com.hbm.blockentity.interfaces;

import com.hbm.dim.orbit.Space;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 检测机器周围的空气是否足够
 * */
public interface ITakeAir {
    public static boolean breatheAir(Level world, BlockPos blockPos, int amount) {
        return world.dimension() != Space.LEVEL_KEY;
    }
}
