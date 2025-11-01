package com.hbm.utils;

import net.minecraft.core.Direction;

public class EnumUtils {
    public static final Direction[] DIRECTIONS = Direction.values();

    public static final int[][] offsets = new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
}
