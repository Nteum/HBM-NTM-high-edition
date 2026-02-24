package com.hbm.render.blockentity;

import com.hbm.block.logistic.BlockConnector;
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class ConnectorRender implements BlockEntityRenderer<TileConnector> {
    private final BakedModel model;
    public ConnectorRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.CONNECTOR);
    }
    @Override
    public void render(TileConnector pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Direction facing = pBlockEntity.getBlockState().getValue(BlockStateProperties.FACING);

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, facing, 0.5f, 0.5f,  0.5f);
        RenderUtils.renderModel(this.model, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();

        pPoseStack.pushPose();
        BlockPos blockPos = pBlockEntity.getBlockPos();
        Vec3 blockLinkPoint = BlockConnector.getLinkPos(blockPos, facing);
        Set<BlockPos> connected = pBlockEntity.getConnected();
        for (BlockPos connectedPos : connected) {
            if (blockPos.asLong() < connectedPos.asLong()){
                BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(connectedPos);
                if (blockEntity instanceof TileConnector connector){
                    Vec3 connLinkPoint = BlockConnector.getLinkPos(connectedPos, connector.getBlockState().getValue(BlockStateProperties.FACING));
                    RenderUtils.renderLine(blockLinkPoint, connLinkPoint, pPoseStack, pBuffer, pPartialTick);
                }
            }
        }
        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TileConnector pBlockEntity) {
        return true;
    }
}

