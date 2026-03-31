package com.hbm.render.blockentity;

import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.logistic.TileConveyor;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RendererConveyor implements BlockEntityRenderer<TileConveyor> {
    public RendererConveyor(BlockEntityRendererProvider.Context pContext){}
    @Override
    public void render(TileConveyor conveyor, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (conveyor.isEmpty()) return;
        ItemStack carriedItem = conveyor.getItems().getStackInSlot(0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Direction inDir = conveyor.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction outDir = DirectionUtils.leftAndRightDir(inDir, conveyor.getBlockState().getValue(HBMBlockProperties.VARIANT3));
        float transProgress = (float) conveyor.getTransPortProgress() / TileConveyor.MAX_TRANSPORT_PROGRESS;
        Direction dir = transProgress > 0.5 ? outDir : inDir;
        boolean isBlock = carriedItem.getItem() instanceof BlockItem;

        float xrot = 0;
        float yrot = 0;
        poseStack.pushPose();
        switch (dir){
            case SOUTH -> {
                poseStack.translate(0.5, 0.375, transProgress);
                if (!isBlock) poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }case EAST -> {
                poseStack.translate(transProgress, 0.375, 0.5);
                poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
                if (!isBlock) poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }case NORTH -> {
                poseStack.translate(0.5, 0.375, 1 - transProgress);
                if (!isBlock) poseStack.mulPose(Axis.XN.rotation(-Mth.HALF_PI));
            }case WEST -> {
                poseStack.translate(1 - transProgress, 0.375, 0.5);
                poseStack.mulPose(Axis.YN.rotation(-Mth.HALF_PI));
                if (!isBlock) poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
            }
        }
//        poseStack.translate(0.5, 0.375, 0.5);
//        poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
        poseStack.scale(0.5f, 0.5f, 0.5f);
        itemRenderer.render(carriedItem, ItemDisplayContext.GUI,true,poseStack,bufferSource,light,overlay,itemRenderer.getModel(carriedItem, conveyor.getLevel(), null, 0));
        poseStack.popPose();
    }
}
