package com.hbm.render.blockentity;

import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class NukeBoyRender extends MultiPartRenderer<NukeBombBoyEntity> {
    public static BakedModel bomb_model;
    public NukeBoyRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        bomb_model = modelManager.getModel(Models.BOY);
    }

    @Override
    public void renderMultiPart(NukeBombBoyEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
        renderBlockModel(bomb_model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
    }

}
