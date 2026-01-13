package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.ZirnoxReactorBlockEntity;
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

public class ZirnoxRenderer extends MultiPartRenderer<ZirnoxReactorBlockEntity> {
    private final BakedModel baseModel;

    public ZirnoxRenderer(BlockEntityRendererProvider.Context context) {
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        this.baseModel = modelManager.getModel(Models.ZIRNOX_BASE);
    }

    @Override
    public void renderMultiPart(ZirnoxReactorBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState blockState = entity.getBlockState();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();
        renderBlockModel(baseModel, blockState, modelRenderer, poseStack, buffer, packedLight, packedOverlay, null);
    }
}
