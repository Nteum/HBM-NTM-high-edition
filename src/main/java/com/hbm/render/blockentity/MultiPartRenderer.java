package com.hbm.render.blockentity;

import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.render.utils.ModelAdjustUtils;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MultiPartRenderer<T extends DummyableBlockEntity> implements BlockEntityRenderer<T> {
    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        if (!pBlockEntity.isCore)return;
        BlockState blockState = pBlockEntity.getBlockState();

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
//        ModelAdjustUtils.generalMachineRotate(pPoseStack, blockState);

        renderMultiPart(pBlockEntity,pPartialTick,pPoseStack,pBuffer,pPackedLight,pPackedOverlay);

        pPoseStack.popPose();
    }
    public abstract void renderMultiPart(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay);
}
