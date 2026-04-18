package com.hbm.utils;

import com.hbm.block.logistic.BlockConnector;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collection;
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
    public static Direction horizRot(Direction refDir, Direction newRefDir, Direction dir){
        // 只处理水平旋转（NORTH=2, EAST=5, SOUTH=3, WEST=4），上下方向不考虑
        return dir.getAxis() == Direction.Axis.Y ? dir : Direction.from2DDataValue((dir.get2DDataValue() + (newRefDir.get2DDataValue() - refDir.get2DDataValue() + 4) % 4) % 4);
    }
    public static List<Direction> horizRot(Direction refDir, Direction newRefDir, Collection<Direction> dirs){
        return dirs.stream().map(dir -> horizRot(refDir, newRefDir, dir)).toList();
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
    /** 模型旋转的逻辑 */
    public static void generalMachineRotate(PoseStack poseStack, BlockState blockState){
        Direction facing = blockState.hasProperty(BlockStateProperties.FACING) ? blockState.getValue(BlockStateProperties.FACING) :
                blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) :
                Direction.NORTH;
        generalMachineRotate(poseStack, facing, 0.5f, 0.5f);
    }
    /**
     * 这里默认模型中心位于(0,0)
     * @param centerX 相对于模型中心，放置方块的旋转中心的x位置（一般是0.5）;
     * @param centerZ 相对模型中心，放置方块的旋转中心z的位置（一般是0.5）
     * */
    public static void generalMachineRotate(PoseStack poseStack, Direction facing, float centerX, float centerZ){
        // YP是顺时针，mc是左手定则
        switch (facing){
            case SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
            }
            case EAST -> {
                poseStack.translate(centerX - centerZ,0,centerX + centerZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            case NORTH -> {
                poseStack.translate(2 * centerX,0,2 * centerZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            case WEST -> {
                poseStack.translate(centerX + centerZ,0,-centerX + centerZ);
                poseStack.mulPose(Axis.YP.rotationDegrees(270));
            }
        }
    }
    // 默认模型中心点在(0,0)，且UP为正方向
    public static void generalMachineRotate(PoseStack poseStack, Direction facing, float centerX, float centerY, float centerZ){
        // 先转y轴，后转x轴
        poseStack.translate(centerX, centerY, centerZ);
        switch (facing){
            case UP -> {}
            case DOWN -> poseStack.mulPose(Axis.XN.rotation(Mth.PI));
            case SOUTH -> {
                poseStack.mulPose(Axis.YN.rotation(Mth.PI));
                poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }
            case EAST -> {
                poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
                poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }
            case NORTH -> poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            case WEST -> {
                poseStack.mulPose(Axis.YN.rotation(-Mth.HALF_PI));
                poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }
        }
        poseStack.translate(-centerX, -centerY, -centerZ);
    }
    public static VoxelShape voxelShapeRot(VoxelShape shape, Direction facing) {
        return voxelShapeRot(shape, Direction.SOUTH, facing);
    }
    /**
     * 根据方向对voxelshape进行旋转
     * AI生成的，还没验证正确性
     * */
    public static VoxelShape voxelShapeRot(VoxelShape shape, Direction defaultFace, Direction facing) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        int times = (facing.get2DDataValue() - defaultFace.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                // 以 (0.5, y, 0.5) 为中心旋转90°，实际上只对 XZ 平面做变换
                buffer[1] = Shapes.or(buffer[1], Shapes.box(1-maxZ, minY, minX, 1-minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
    /** 将offset根据方向进行旋转。
     * 默认方向是南方，其他方向按照南方进行旋转（因为南方两个坐标都是正的）
     * （本以为会有现成方法的，但好像确实没有）
     * */
    public static List<Vec3i> transOffsets(List<Vec3i> offsets, Direction dir){
        List<Vec3i> result = new ArrayList<>(offsets);
        int[] trans;
        switch (dir){
            case NORTH -> trans = new int[]{-1,0,0,-1};
            case EAST -> trans = new int[]{0,-1,1,0};
            case SOUTH -> trans = new int[]{1,0,0,1};
            case WEST -> trans = new int[]{0,1,-1,0};
            default -> trans = new int[]{1,0,0,1};
        }
        for (int i = 0; i < result.size(); i++) {
            Vec3i v1 = result.get(i);
            Vec3i v2 = new Vec3i(v1.getX() * trans[0] + v1.getZ() * trans[2], v1.getY(), v1.getX() * trans[1] + v1.getZ() * trans[3]);
            result.set(i, v2);
        }
        return result;
    }
    public static boolean searchAround(Level level, BlockPos pos, Block targetBlock){
        for (Direction direction : EnumUtils.DIRECTIONS) {
            if (level.getBlockState(pos.relative(direction)).is(targetBlock)) return true;
        }
        return false;
    }
    // 从某个方向左右转后获得的方向 0 - 向前；1 - 左弯；2 - 右弯
    public static Direction leftAndRightDir(Direction inDir, int bend){
        return switch (bend){
            case 1 -> Direction.from2DDataValue((inDir.get2DDataValue() - 1 + 4) % 4);
            case 2 -> Direction.from2DDataValue((inDir.get2DDataValue() + 1 + 4) % 4);
            default -> inDir;
        };
    }
    // 0 - 对面，1 - 左面 2 - 右面 3 - 上面 4 - 下面
//    public static Direction relativeDir2Dir(Direction refDir, int relative){
//        Direction.Axis axis = refDir.getAxis();
//        Direction.AxisDirection axisDirection = refDir.getAxisDirection();
//        Direction.Axis sideAxis = axis == Direction.Axis.X || axis == Direction.Axis.Y ? Direction.Axis.Z :  Direction.Axis.X;
//        Direction.AxisDirection rightAxisDirection = axis == Direction.Axis.X || axis == Direction.Axis.Y ? axisDirection : axisDirection.opposite();
//        Direction.Axis verticalAxis = axis == Direction.Axis.X || axis == Direction.Axis.Z ? Direction.Axis.Y : Direction.Axis.X;
//        Direction.AxisDirection upAxisDirection = axis == Direction.Axis.X || axis == Direction.Axis.Z ? axisDirection : axisDirection.opposite();
//        return switch (relative){
//            case 0 -> refDir.getOpposite();
//            case 1 -> Direction.fromAxisAndDirection(sideAxis, rightAxisDirection.opposite());
//            case 2 -> Direction.fromAxisAndDirection(sideAxis, rightAxisDirection);
//            case 3 -> Direction.fromAxisAndDirection(verticalAxis, upAxisDirection);
//            case 4 -> Direction.fromAxisAndDirection(verticalAxis, upAxisDirection.opposite());
//            default -> refDir;
//        };
//    }
    // 预定义的相对查找表（针对每种 refDir，对应的 0-4 分别是什么）
    // 这虽然笨，但绝对不会在计算坐标轴正负号时出错
    private static final Direction[][] RELATIVE_MAP = {
            // 假设索引顺序：0:OPPOSITE, 1:LEFT, 2:RIGHT, 3:UP, 4:DOWN
            { /* DOWN  */ Direction.UP,    Direction.WEST,  Direction.EAST,  Direction.NORTH, Direction.SOUTH },
            { /* UP    */ Direction.DOWN,  Direction.WEST,  Direction.EAST,  Direction.SOUTH, Direction.NORTH },
            { /* NORTH */ Direction.SOUTH, Direction.WEST,  Direction.EAST,  Direction.UP,    Direction.DOWN },
            { /* SOUTH */ Direction.NORTH, Direction.EAST,  Direction.WEST,  Direction.UP,    Direction.DOWN },
            { /* WEST  */ Direction.EAST,  Direction.SOUTH, Direction.NORTH, Direction.UP,    Direction.DOWN },
            { /* EAST  */ Direction.WEST,  Direction.NORTH, Direction.SOUTH, Direction.UP,    Direction.DOWN }
    };

    public static Direction relativeDir2Dir(Direction ref, int relative) {
        if (relative < 0 || relative > 4) return ref;
        return RELATIVE_MAP[ref.get3DDataValue()][relative];
    }
    public static int dir2RelativeDir(Direction ref, Direction secondary){
        if (ref == secondary) return 0;
        for (int i = 0; i < RELATIVE_MAP[ref.get3DDataValue()].length; i++) {
            if (RELATIVE_MAP[ref.get3DDataValue()][i] == secondary) return i;
        }
        return 0;
    }

    // 辅助函数：将方向向上“翻” 90 度
    private static Direction rotateUp(Direction ref) {
        // 如果参考方向是水平的，向上转 90 度就是指向天（有些特殊情况需要处理）
        // 这里推荐一个万能公式：
        return switch (ref) {
            case UP -> Direction.NORTH;   // 已经在顶上，向上翻转到北
            case DOWN -> Direction.SOUTH; // 已经在底下，向上翻转到南
            default -> Direction.UP;      // 水平方向向上翻转统一指向天
        };
    }
    // 一个点相对于所在方块内特点方向边的距离
    public static double locToSideDist(Vec3 loc, Direction side){
        return switch (side){
            case EAST -> Math.ceil(loc.x) - loc.x;
            case WEST -> loc.x - Math.floor(loc.x);
            case SOUTH -> Math.ceil(loc.z) - loc.z;
            case NORTH -> loc.z - Math.floor(loc.z);
            case UP -> Math.ceil(loc.y) - loc.y;
            case DOWN -> loc.y - Math.floor(loc.y);
        };
    }
    // 一个点相对于两条边的角度
    public static double locToCornerAngle(Vec3 loc, Direction inSide, Direction outSide){
        return Math.atan(locToSideDist(loc, inSide) / locToSideDist(loc, outSide));
    }
}
