package com.hbm.utils;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class DirectionUtils {
    //==============旋转的内容，1710版本的hbm主要参考早期forge direction的方式确定旋转，似乎可以照搬
    public static final int[] OPPOSITES = {1, 0, 3, 2, 5, 4, 6};
    /** 沿特定舟单次旋转（左手系） */
    public static final int[][] ROTATION_MATRIX = {
            {0, 1, 4, 5, 3, 2, 6},
            {0, 1, 5, 4, 2, 3, 6},
            {5, 4, 2, 3, 0, 1, 6},
            {4, 5, 2, 3, 1, 0, 6},
            {2, 3, 1, 0, 4, 5, 6},
            {3, 2, 0, 1, 4, 5, 6},
            {0, 1, 2, 3, 4, 5, 6},
    };
    public static Direction leftRot(Direction axis, Direction original){
        return EnumUtils.DIRECTIONS[ROTATION_MATRIX[axis.ordinal()][original.ordinal()]];
    }

    /**
     * 沿水平面旋转一个方向
     * refDir: 结构的默认方向
     * newRefDir: 放置下来的结构方向
     * */
//    public static List<Direction> horizDir = List.of(Direction.EAST,Direction.SOUTH,Direction.WEST,Direction.NORTH);
//    public static Direction horizRot(Direction refDir, Direction newRefDir, Direction dir){
//        if (refDir.equals(newRefDir)) return dir;
//        int deltaRot = horizDir.indexOf(newRefDir) - horizDir.indexOf(refDir);
//        return horizDir.get((horizDir.indexOf(dir) + deltaRot + horizDir.size())%horizDir.size());
//    }
    public static Direction horizRot(Direction refDir, Direction newRefDir, Direction dir){
        // 只处理水平旋转（NORTH=2, EAST=5, SOUTH=3, WEST=4），上下方向不考虑
        int times = (newRefDir.get2DDataValue() - refDir.get2DDataValue() + 4) % 4;
        return Direction.from2DDataValue((dir.get2DDataValue() + times) % 4);
    }

    /** 偏移量offset的旋转 */
    public static Vec3i offsetRot(Vec3i offset, Direction refDir, Direction newRefdir){
        return offsetRot(List.of(offset), refDir, newRefdir).get(0);
    }
    public static List<Vec3i> offsetRot(List<Vec3i> offsets, Direction refDir, Direction newRefDir){
        // 只处理水平旋转（NORTH=2, EAST=5, SOUTH=3, WEST=4），上下方向不考虑
        int times = (newRefDir.get2DDataValue() - refDir.get2DDataValue() + 4) % 4;

        List<Vec3i> result = new ArrayList<>(offsets.size());
        for (Vec3i v : offsets) {
            int x = v.getX();
            int y = v.getY();
            int z = v.getZ();
            switch (times) {
                case 0: // 不旋转
                    result.add(new Vec3i(x, y, z));
                    break;
                case 1: // 旋转 90° 顺时针
                    result.add(new Vec3i(-z, y, x));
                    break;
                case 2: // 旋转 180°
                    result.add(new Vec3i(-x, y, -z));
                    break;
                case 3: // 旋转 270° 顺时针（或逆时针 90°）
                    result.add(new Vec3i(z, y, -x));
                    break;
            }
        }
        return result;
    }
}
