package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileArcFurnace;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RenderArcFurnace extends BaseTileRender<TileArcFurnace> {
    public RenderArcFurnace(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(TileArcFurnace pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

    }
}
