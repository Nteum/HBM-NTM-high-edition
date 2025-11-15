package com.hbm.render.blockentity;

import com.hbm.block.machine.BlockPress;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.render.RenderUtils;
import com.hbm.render.model.Models;
import com.hbm.utils.DirectionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PressRenderer implements BlockEntityRenderer<PressEntity> {
    private int cnt = 0;
    public PressRenderer(Context pContext){
    }
    /**
     * 函数接口BlockEntityRendererProvider对应的方法，必须实现，否则无法在onEntityRenderers中注册。
     * */
//    public static BlockEntityRenderer<PressEntity> create(Context pContext) {
//        return new PressRenderer(pContext);
//    }
    /**主要渲染函数
     * 输入参数：
     * 1. pBlockEntity：就是对应的方块实体
     * 2. pPartialTick：
     * 3. pPoseStack：用于管理Pose。
     *  3.1. translate：平移堆栈中最后一个pose，Pose是一个静态嵌套类，包含姿势矩阵和法线矩阵
     *  3.2. scale：缩放堆栈中最后一个pose
     *  3.3. mulPose：
     *  3.4. rotateAround：让堆栈中最后一个pose围绕特定点旋转
     *  3.5. pushPose：将一个新pose推到堆栈尾
     *  3.6. popPose：
     * 4. pBuffer：
     * 5. pPackedLight
     * 6. pPackedOverlay
     * */
    @Override
    public void render(PressEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        BlockState blockState = pBlockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof BlockPress)) return;
        BakedModel[] models = ((BlockPress) blockState.getBlock()).getModels();
        if (models.length < 2) return;

        DirectionUtils.generalMachineRotate(pPoseStack, blockState);
        RenderUtils.renderModel(models[0], pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());

        //渲染锻压机头的移动
        pPoseStack.pushPose();
        int press = pBlockEntity.press;
        double extension = 0.9 - 0.9 * press / 100;
        pPoseStack.translate(0,extension,0);
        RenderUtils.renderModel(models[1], pPoseStack, pBuffer, pPackedLight, pPackedOverlay, RenderType.cutout());
        pPoseStack.popPose();

        //显示当前正在锻压的物品
        pPoseStack.pushPose();
        //位置平移
        pPoseStack.translate(0.5,1.0,0.5);
        //旋转到在锻压机上平放（Axis.XN是绕X轴翻转）
        pPoseStack.mulPose(Axis.XN.rotationDegrees(-90));
        //大小缩小一半
        pPoseStack.scale(0.5F,0.5F,0.5F);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getItemHandler().getStackInSlot(2);
        BakedModel model = itemRenderer.getModel(itemStack, pBlockEntity.getLevel(), null, 0);
        itemRenderer.render(itemStack, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,model);
        pPoseStack.popPose();

        pPoseStack.popPose();
    }
}
