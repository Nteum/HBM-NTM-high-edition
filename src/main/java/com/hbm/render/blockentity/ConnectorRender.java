package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.TileConnector;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Set;

public class ConnectorRender implements BlockEntityRenderer<TileConnector> {
    private final BakedModel model;
    private static float scale = 0.85f;
    public ConnectorRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.CONNECTOR);
    }
    @Override
    public void render(TileConnector pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState().getValue(BlockStateProperties.FACING), 0.5f, 0.5f,  0.5f);
        pPoseStack.scale(scale, scale, scale);
        RenderUtils.renderModel(this.model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();

        pPoseStack.pushPose();
        BlockPos blockPos = pBlockEntity.getBlockPos();
        Set<BlockPos> connected = pBlockEntity.getConnected();
        for (BlockPos connectedPos : connected) {
            if (blockPos.asLong() < connectedPos.asLong()){
                RenderUtils.renderLine(blockPos.getCenter(), connectedPos.getCenter(), pPoseStack, pBuffer, pPartialTick);
            }
        }
        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TileConnector pBlockEntity) {
        return true;
    }
}

