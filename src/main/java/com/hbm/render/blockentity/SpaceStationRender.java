package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileSpaceStaion;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;

public class SpaceStationRender implements BlockEntityRenderer<TileSpaceStaion> {
    private final BakedModel model;
    public SpaceStationRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.SPACE_STATION_BASE);
    }
    @Override
    public void render(TileSpaceStaion pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState());
        RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();
    }
}
