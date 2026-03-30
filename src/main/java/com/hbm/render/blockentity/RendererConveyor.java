package com.hbm.render.blockentity;

import com.hbm.blockentity.logistic.TileConveyor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RendererConveyor implements BlockEntityRenderer<TileConveyor> {
    public RendererConveyor(BlockEntityRendererProvider.Context pContext){}
    @Override
    public void render(TileConveyor conveyor, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {

    }
}
