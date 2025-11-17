package com.hbm.render.blockentity;


import com.hbm.blockentity.machine.AssemblerEntity;
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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class AssemblerRenderer implements BlockEntityRenderer<AssemblerEntity> {
    public static BakedModel model;
    public static BakedModel model1;
    public static BakedModel model2;
    public static BakedModel model3;
    int count = 0;

    public AssemblerRenderer(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model = modelManager.getModel(Models.ASSEMBLER_COG);
        model1 = modelManager.getModel(Models.ASSEMBLER_ARM);
        model2 = modelManager.getModel(Models.ASSEMBLER_SLIDER);
        model3 = modelManager.getModel(Models.ASSEMBLER_BODY);
    }
    @Override
    public void render(AssemblerEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        BlockState blockState = pBlockEntity.getBlockState();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
        int rotation = 0;
        int offset = count <= 90?count:(count<=270)?180-count:count-360;
        double sway = Math.sin(offset / Math.PI / 60);

        pPoseStack.pushPose();
        DirectionUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState());
//        ModelAdjustUtils.generalMachineRotate(pPoseStack, pBlockEntity.getBlockState());

        if (pBlockEntity.running){
            count = (count + 1) % 360;
        }

        //边上的四个齿轮
        pPoseStack.pushPose();
        pPoseStack.translate(-0.6, 0.75, 1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(-0.6, 0.75, -1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(0.6, 0.75, -1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(0.6, 0.75, 1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.translate(0.4 * offset/90, 0, 0);
        //格架
        renderBlockModel(model2,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.translate(0, 0, sway * 0.3);
        //机械臂
        renderBlockModel(model1,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        //正在制作的物品
        if (pBlockEntity.showItem != null){
            pPoseStack.pushPose();
            //位置平移
            pPoseStack.translate(0,0.85,0);
            //旋转到在锻压机上平放（Axis.XN是绕X轴翻转）
            pPoseStack.mulPose(Axis.XN.rotationDegrees(-90));
            pPoseStack.mulPose(Axis.ZN.rotationDegrees(90));
            //大小缩小一半
            pPoseStack.scale(0.5F,0.5F,0.5F);
//        ItemStack itemStack = new ItemStack(ModItems.grenade_generic.get());
            BakedModel productModel = itemRenderer.getModel(pBlockEntity.showItem, pBlockEntity.getLevel(), null, 0);
            itemRenderer.render(pBlockEntity.showItem, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,productModel);
            pPoseStack.popPose();
        }

        //装配机身体部分
        pPoseStack.pushPose();
        renderBlockModel(model3,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.popPose();
    }


}
