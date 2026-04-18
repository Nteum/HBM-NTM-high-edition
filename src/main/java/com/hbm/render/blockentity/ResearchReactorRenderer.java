package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.research.ResearchReactorBlockEntity;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class ResearchReactorRenderer extends MultiPartRenderer<ResearchReactorBlockEntity> {

    public ResearchReactorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void renderMultiPart(ResearchReactorBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel baseModel = modelManager.getModel(Models.RESEARCH_REACTOR_BASE);
        BakedModel rodModel = modelManager.getModel(Models.RESEARCH_REACTOR_RODS);
        if (baseModel == modelManager.getMissingModel()) {
            return;
        }
        if (rodModel == modelManager.getMissingModel()) {
            rodModel = baseModel;
        }
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();
        BlockState state = entity.getBlockState();

        renderBlockModel(baseModel, state, modelRenderer, poseStack, buffer, packedLight, packedOverlay, null);

        double offset = entity.getRenderLevel(partialTick) * 0.75D;
        poseStack.pushPose();
        poseStack.translate(0.0D, offset, 0.0D);
        renderBlockModel(rodModel, state, modelRenderer, poseStack, buffer, packedLight, packedOverlay, null);
        poseStack.popPose();
    }
}
