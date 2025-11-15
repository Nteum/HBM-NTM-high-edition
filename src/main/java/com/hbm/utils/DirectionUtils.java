package com.hbm.utils;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.block.base.BlockDummyable;
import com.hbm.block.base.MultiPartBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    /** 模型旋转的逻辑 */
//    public static void generalMachineRotate(PoseStack poseStack, BlockState blockState){
//        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
//        int[] offset;
//        Block block1 = blockState.getBlock();
//        if (block1 instanceof BlockDummyable dummyable)
//            offset = dummyable.getDimensions();
//        else return;
//
//        float xSize = (float) (offset[5] - offset[4]) / 2;
//        float zSize = (float) (offset[3] - offset[2]) / 2;
//        // YP是顺时针，mc是左手定则
//        switch (facing){
//            case SOUTH -> {
//                poseStack.mulPose(Axis.YP.rotationDegrees(0));
//            }
//            case EAST -> {
//                poseStack.translate(zSize-xSize,0,-xSize-zSize);
//                poseStack.mulPose(Axis.YP.rotationDegrees(90));
//            }
//            case NORTH -> {
//                poseStack.translate(-2*xSize,0,-2*zSize);
//                poseStack.mulPose(Axis.YP.rotationDegrees(180));
//            }
//            case WEST -> {
//                poseStack.translate(-zSize-xSize,0,xSize-zSize);
//                poseStack.mulPose(Axis.YP.rotationDegrees(270));
//            }
//        }
//    }
    public static void generalMachineRotate(PoseStack poseStack, BlockState blockState){
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
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
    public static VoxelShape voxelShapeRot(VoxelShape shape, Direction facing) {
        return voxelShapeRot(shape, Direction.SOUTH, facing);
    }
    /**
     * 根据方向对voxelshape进行旋转
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

    public static float deltaYRot(Direction ... facings){
        if (facings.length == 0) return 0;
        Direction defaultFacing = facings.length < 2 ? Direction.NORTH : facings[1];
        return facings[0].toYRot() - defaultFacing.toYRot();
    }

    public static boolean searchAround(Level level, BlockPos pos, Block targetBlock){
        for (Direction direction : EnumUtils.DIRECTIONS) {
            if (level.getBlockState(pos.relative(direction)).is(targetBlock)) return true;
        }
        return false;
    }
}
