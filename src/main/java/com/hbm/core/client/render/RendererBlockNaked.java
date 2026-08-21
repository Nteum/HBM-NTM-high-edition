package com.hbm.core.client.render;

import com.hbm.render.RenderUtils;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
/**
 * 方块的默认渲染器，多方快如果没有特殊动画，就用这个。
 */
public class RendererBlockNaked implements BlockEntityRenderer<BlockEntity> {
    BakedModel model;
    public RendererBlockNaked(BlockEntityRendererProvider.Context pContext){
    }
    @Override
    public void render(BlockEntity pBlockEntity, float v, PoseStack poseStack, MultiBufferSource pBuffer, int i, int i1) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (model == null) model = blockRenderer.getBlockModel(blockState);
        poseStack.pushPose();
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) DirectionUtils.generalMachineRotate(poseStack, blockState);
        RenderUtils.renderModel(model, poseStack, pBuffer, i, i1, RenderType.cutout());
        poseStack.popPose();
    }
}
