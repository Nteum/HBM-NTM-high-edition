package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileSpaceStation;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.engine.CustomPartsModel;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;

public class SpaceStationRender implements BlockEntityRenderer<TileSpaceStation> {
    private final BakedModel model;
    public SpaceStationRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.SPACE_STATION_BASE);
    }
    @Override
    public void render(TileSpaceStation pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        long degree = pBlockEntity.getLevel().getGameTime() % 180;
        degree = degree <= 90 ? degree : 180 - degree;
        pPoseStack.pushPose();
        pPoseStack.translate(0, 1, 0);
        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState());
        if (model instanceof CustomPartsModel.Baked customBakedModel){
            RenderUtils.renderModel(customBakedModel.getPart("Port"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());

            pPoseStack.pushPose();
            pPoseStack.translate(0, -1.65, 2);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(degree));
            pPoseStack.translate(0, 1.65, -2);
            RenderUtils.renderModel(customBakedModel.getPart("ArmZN"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();
            pPoseStack.pushPose();
            pPoseStack.translate(0, -1.65, -2);
            pPoseStack.mulPose(Axis.XN.rotationDegrees(degree));
            pPoseStack.translate(0, 1.65, 2);
            RenderUtils.renderModel(customBakedModel.getPart("ArmZP"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();
            pPoseStack.pushPose();
            pPoseStack.translate(2, -1.65, 0);
            pPoseStack.mulPose(Axis.ZN.rotationDegrees(degree));
            pPoseStack.translate(-2, 1.65, 0);
            RenderUtils.renderModel(customBakedModel.getPart("ArmXP"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();
            pPoseStack.pushPose();
            pPoseStack.translate(-2, -1.65, 0);
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(degree));
            pPoseStack.translate(2, 1.65, 0);
            RenderUtils.renderModel(customBakedModel.getPart("ArmXN"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            pPoseStack.popPose();
        }else {
            RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        }
        pPoseStack.popPose();
    }
}
