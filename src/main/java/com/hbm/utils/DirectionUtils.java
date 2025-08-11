package com.hbm.utils;

import net.minecraft.core.Direction;

import java.util.List;

public class DirectionUtils {
    public static List<Direction> horizDir = List.of(Direction.EAST,Direction.SOUTH,Direction.WEST,Direction.NORTH);
    /** 沿水平面旋转一个方向 */
    public static Direction horizRot(Direction refDir, Direction newRefDir, Direction dir){
        int deltaRot = horizDir.indexOf(newRefDir) - horizDir.indexOf(refDir);
        return horizDir.get((horizDir.indexOf(dir) + deltaRot + horizDir.size())%horizDir.size());
    }

}
