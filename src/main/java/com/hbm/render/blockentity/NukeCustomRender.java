package com.hbm.render.blockentity;

import com.hbm.blockentity.weapon.NukeBombCustomEntity;
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

public class NukeCustomRender extends MultiPartRenderer<NukeBombCustomEntity> {
    public static BakedModel bomb_model;
    public NukeCustomRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        bomb_model = modelManager.getModel(Models.CUSTOM_NUKE);
    }
//    @Override
//    public void render(NukeBombCustomEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
//        BlockState blockState = pBlockEntity.getBlockState();
//        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
//        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
//        //根据方向确定旋转角度
//        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
//        int rotation = 0;
//        switch (direction){
//            case NORTH -> rotation = 0;
//            case WEST -> rotation = 90;
//            case SOUTH -> rotation = 180;
//            case EAST -> rotation = 270;
//        }
//
//        //渲染核弹模型
//        pPoseStack.pushPose();
//        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
//        renderBlockModel(bomb_model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
//        pPoseStack.popPose();
//    }

    @Override
    public void renderMultiPart(NukeBombCustomEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
        renderBlockModel(bomb_model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
    }
}
