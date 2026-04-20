package com.hbm.render.blockentity;

import com.hbm.block.HBMBlockProperties;
import com.hbm.block.logistic.Conveyor;
import com.hbm.blockentity.logistic.TileConveyor;
import com.hbm.render.RenderUtils;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RendererConveyor implements BlockEntityRenderer<TileConveyor> {
    public RendererConveyor(BlockEntityRendererProvider.Context pContext){}
    @Override
    public void render(TileConveyor conveyor, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        if (conveyor.isEmpty()) return;
        ItemStack carriedItem = conveyor.getItems().getStackInSlot(0);
        if (carriedItem.isEmpty()) return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        Direction inDir = conveyor.inDir;
        BlockState state = conveyor.getBlockState();
        Direction facing = state.getValue(Conveyor.FACING);
        int variant = state.getValue(Conveyor.VARIANT);
        float transProgress = (float) conveyor.getTransPortProgress() / conveyor.getMaxTransportProgress();
        Direction dir;  // 仅用于表示水平方向
        // 方块单独渲染，需要物品是方块
        boolean isBlock = carriedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock().getRenderShape(blockItem.getBlock().defaultBlockState()) == RenderShape.MODEL;
        float hoOffset = isBlock ? 0.375f : 0.5f;
        float scale = isBlock ? 0.25f : 0.5f;
        poseStack.pushPose();
        if (variant < 3 || variant == 7){       // 如果处在水平方向上
            dir = transProgress > 0.5 ? DirectionUtils.leftAndRightDir(facing, variant) : (inDir == null ? facing : inDir.getOpposite());   // 输入方向和运动方向是相反的
            switch (dir){
                case SOUTH -> {
                    poseStack.translate(hoOffset, 0.375, transProgress);
                    if (!isBlock) poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
                }case EAST -> {
                    poseStack.translate(transProgress, 0.375, hoOffset);
                    if (!isBlock) {
                        poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
                        poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
                    }
                }case NORTH -> {
                    poseStack.translate(hoOffset, 0.375, 1 - transProgress);
                    if (!isBlock) poseStack.mulPose(Axis.XN.rotation(-Mth.HALF_PI));
                }case WEST -> {
                    poseStack.translate(1 - transProgress, 0.375, hoOffset);
                    if (!isBlock) {
                        poseStack.mulPose(Axis.YN.rotation(-Mth.HALF_PI));
                        poseStack.mulPose(Axis.XN.rotation(Mth.HALF_PI));
                    }
                }
            }
            poseStack.scale(scale, scale, scale);
            if (isBlock){
                blockRenderer.renderSingleBlock(((BlockItem) carriedItem.getItem()).getBlock().defaultBlockState(), poseStack, bufferSource, light, overlay);
            }else {
                itemRenderer.renderStatic(carriedItem, ItemDisplayContext.FIXED,light,overlay, poseStack, bufferSource,conveyor.getLevel(), 0);
            }
        }else if (variant < 6){
            switch (facing){
                case SOUTH -> {
                    poseStack.translate(hoOffset, transProgress, 0.625);
                }case EAST -> {
                    poseStack.translate(0.625, transProgress, hoOffset);
                    poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
                }case NORTH -> {
                    poseStack.translate(hoOffset, transProgress, 0.375);
                }case WEST -> {
                    poseStack.translate(0.375, transProgress, hoOffset);
                    poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
                }
            }
            poseStack.scale(scale, scale, scale);
            if (isBlock){
                blockRenderer.renderSingleBlock(((BlockItem) carriedItem.getItem()).getBlock().defaultBlockState(), poseStack, bufferSource, light, overlay);
            }else {
                itemRenderer.renderStatic(carriedItem, ItemDisplayContext.FIXED,light,overlay, poseStack, bufferSource,conveyor.getLevel(), 0);
            }
        }
        poseStack.popPose();
    }
}
