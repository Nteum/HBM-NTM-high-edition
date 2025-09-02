package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class ChemplantRenderer implements BlockEntityRenderer<ChemplantEntity> {
    boolean running = true;
    public static BakedModel body_model;
    public static BakedModel piston_model;
    public static BakedModel spinner_model;
    public ChemplantRenderer(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        body_model = modelManager.getModel(Models.CHEMPLANT_BODY);
        piston_model = modelManager.getModel(Models.CHEMPLANT_PISTON);
        spinner_model = modelManager.getModel(Models.CHEMPLANT_SPINNER);
    }
    @Override
    public void render(ChemplantEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        running = pBlockEntity.progress > 0;

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        //化工厂主体
        RenderUtils.renderBlockModel(body_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        //旋转刷
        int rotation = (int) (System.currentTimeMillis() % (360 * 5)) / 5;
        pPoseStack.pushPose();
        pPoseStack.translate(-0.625, 0, 0.625);
        if (running)
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-rotation));
        else
            pPoseStack.mulPose(Axis.YP.rotationDegrees(-45));
        RenderUtils.renderBlockModel(spinner_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        //另一侧旋转刷
        pPoseStack.popPose();
        pPoseStack.pushPose();
        pPoseStack.translate(0.625, 0, 0.625);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(running ? rotation : 45));
        RenderUtils.renderBlockModel(spinner_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();
        //piston
        double push = Math.sin((System.currentTimeMillis() % 2000) / 1000D * Math.PI) * 0.25 - 0.25;
        pPoseStack.pushPose();
        if (running)
            pPoseStack.translate(0,push,0);
        else
            pPoseStack.translate(0,-0.25,0);
        RenderUtils.renderBlockModel(piston_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();
        //液缸中的液体（暂时未做）
        pPoseStack.popPose();
    }

    public void generalMachineRotate(PoseStack poseStack, Direction facing){
        switch (facing){
            case SOUTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
                poseStack.translate(1,0,0);
            }
            case EAST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(0,0,0);
            }
            case NORTH -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(0,0,-1);
            }
            case WEST -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(270));
                poseStack.translate(1,0,-1);
            }
        }
    }

}
