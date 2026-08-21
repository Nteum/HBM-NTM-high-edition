package com.hbm.render.blockentity;

import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.machine.TileEntityHeatBoiler;
import com.hbm.core.client.render.RendererBlockNaked;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.lwjgl.opengl.GL11;

public class RendererBoiler implements BlockEntityRenderer<TileEntityHeatBoiler> {
    public static BakedModel model;
    public RendererBoiler(BlockEntityRendererProvider.Context pContext){}
    @Override
    public void render(TileEntityHeatBoiler pBlockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (model == null) model = blockRenderer.getBlockModel(blockState);
        poseStack.pushPose();
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) DirectionUtils.generalMachineRotate(poseStack, blockState);
        if (blockState.hasProperty(HBMBlockProperties.BROKEN) && !blockState.getValue(HBMBlockProperties.BROKEN)){
            if (pBlockEntity.getFluids().getFluidInTank(1).getAmount() > pBlockEntity.getFluids().getTankCapacity(1) * 0.9){
                double sine = Math.sin(System.currentTimeMillis() / 50D % (Math.PI * 2));
                sine *= 0.01f;
                poseStack.scale((float) (1 - sine), (float) (1 + sine), (float) (1 - sine));
            }
        }
        RenderUtils.renderModel(model, poseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        poseStack.popPose();
    }
}
