package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileEntitySolarMirror;
import com.hbm.core.client.model.CustomPartsModel;
import com.hbm.core.client.render.RendererBlockNaked;
import com.hbm.render.RenderUtils;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RendererSolarMirror implements BlockEntityRenderer<TileEntitySolarMirror> {
    BakedModel model;
    public RendererSolarMirror(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(TileEntitySolarMirror pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (model == null) model = blockRenderer.getBlockModel(blockState);
        pPoseStack.pushPose();
        if (model instanceof CustomPartsModel.Baked customModel){
            RenderUtils.renderModel(customModel.getPart("Base"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            if (pBlockEntity.isOn){

                int dx = pBlockEntity.tX - pBlockEntity.getBlockPos().getX();
                int dy = pBlockEntity.tY - pBlockEntity.getBlockPos().getY();
                int dz = pBlockEntity.tZ - pBlockEntity.getBlockPos().getZ();
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double pitch = -Math.asin(dy / dist) + Mth.HALF_PI;
                double yaw = -Math.atan2(dz, dx) - Mth.HALF_PI;
                pPoseStack.pushPose();
                pPoseStack.mulPose(Axis.XP.rotation((float) pitch));
                pPoseStack.mulPose(Axis.YP.rotation((float) yaw));
                RenderUtils.renderModel(customModel.getPart("Mirror"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
                pPoseStack.popPose();
            }else {
                RenderUtils.renderModel(customModel.getPart("Mirror"), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
            }
        }else RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();
    }
}
