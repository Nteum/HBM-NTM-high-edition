package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.blockentity.weapon.NukeBombEntity;
import com.hbm.model.Models;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.blockentity.RenderUtils.renderBlockModel;

public class NukeFatRender implements BlockEntityRenderer<NukeBombEntity> {
    public static BakedModel fat_man_model;
    public NukeFatRender(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        fat_man_model = modelManager.getModel(Models.FAT_MAN);
    }
    @Override
    public void render(NukeBombEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockState blockState = pBlockEntity.getBlockState();
        BlockRenderDispatcher blockDispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer blockRenderer = blockDispatcher.getModelRenderer();
        //根据方向确定旋转角度
        Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
        int rotation = 0;
        switch (direction){
            case NORTH -> rotation = 0;
            case WEST -> rotation = 90;
            case SOUTH -> rotation = 180;
            case EAST -> rotation = 270;
        }

        //渲染核弹模型
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        renderBlockModel(fat_man_model,blockState,blockRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();
    }
}
