package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.research.BreederReactorBlockEntity;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class BreederReactorRenderer extends MultiPartRenderer<BreederReactorBlockEntity> {

    public BreederReactorRenderer() {
    }

    @Override
    public void renderMultiPart(BreederReactorBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel breederModel = modelManager.getModel(Models.BREEDER_REACTOR);
        if (breederModel == modelManager.getMissingModel()) {
            breederModel = modelManager.getModel(Models.RESEARCH_REACTOR_BASE);
        }
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer renderer = dispatcher.getModelRenderer();
        BlockState state = entity.getBlockState();
        renderBlockModel(breederModel, state, renderer, poseStack, buffer, packedLight, packedOverlay, null);
    }
}
