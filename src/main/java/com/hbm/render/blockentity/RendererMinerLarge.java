package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.render.model.engine.CustomPartsModel;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

public class RendererMinerLarge implements BlockEntityRenderer<TileMinerLarge> {
    private BakedModel miner;
    public RendererMinerLarge(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        miner = modelManager.getModel(Models.MINER_LARGE);
    }
    @Override
    public void render(TileMinerLarge pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        pPoseStack.translate(0.5f, -4f, 0.5f);
        if (miner instanceof CustomPartsModel.Baked customBakedModel){
            RenderUtils.renderModel(customBakedModel, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        }else {
            RenderUtils.renderModel(miner, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        }
        pPoseStack.popPose();
    }
}
