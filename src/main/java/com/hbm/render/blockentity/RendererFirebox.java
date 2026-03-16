package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileFireboxBase;
import com.hbm.registries.ModBlocks;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RendererFirebox implements BlockEntityRenderer<TileFireboxBase> {
    private final BakedModel model;
    public RendererFirebox(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.FIREBOX);
    }
    @Override
    public void render(TileFireboxBase pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
//        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0, 0);
        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState());
        RenderUtils.renderModel(model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();
    }
}
