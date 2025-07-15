package com.hbm.api.interferences;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IDefaultFacing {
    // 由于方块被放置的方向是不定的，因此我们规定处理物品的方向的时候会设置一个默认方向
    // 默认方向应该和方块的默认方向相同，所以设成NORTH
    @Nullable
    default Direction getDefaultFacing() {
        return Direction.NORTH;
    }
}
