package com.hbm.render.utils;

import com.hbm.block.base.BedLikeBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

//用于调整模型位置
public class ModelAdjustUtils {
    public static void generalMachineRotate(PoseStack poseStack, BlockState blockState){
        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BedLikeBlock block = (BedLikeBlock)blockState.getBlock();
        int[] offset = block.getOffset();

        float xSize = (float) (offset[5] - offset[4]) / 2;
        float zSize = (float) (offset[3] - offset[2]) / 2;
        poseStack.translate(-xSize, 0, -zSize);
        switch (facing){
            case SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
                poseStack.translate(-xSize-1,0,zSize);
            }
            case EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(-zSize-1,0,xSize);
            }
            case NORTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(-xSize-1,0,zSize);
            }
            case WEST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(270));
                poseStack.translate(-zSize-1,0,xSize);
            }
        }
    }
}
