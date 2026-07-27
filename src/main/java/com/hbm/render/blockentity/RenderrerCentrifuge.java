package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileMachineCentrifuge;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class RenderrerCentrifuge implements BlockEntityRenderer<TileMachineCentrifuge> {
    private BakedModel model;
    public RenderrerCentrifuge(BlockEntityRendererProvider.Context pContext){
//        model = Minecraft.getInstance().getModelManager().getModel(Models.CENTRIFUGE);
    }
    @Override
    public void render(TileMachineCentrifuge centrifuge, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = centrifuge.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        poseStack.pushPose();
        DirectionUtils.generalMachineRotate(poseStack, blockState);

        if (model == null) model = blockRenderer.getBlockModel(blockState);
//        if (model != null)
        RenderUtils.renderModel(model, poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        poseStack.popPose();
    }
}
