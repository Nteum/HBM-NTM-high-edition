package com.hbm.render.blockentity;


import com.hbm.blockentity.machine.AssemblerEntity;
import com.hbm.model.Models;
import com.hbm.registries.ModItems;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.blockentity.RenderUtils.renderBlockModel;

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
        /** 根据不同方向调整渲染的偏移。据说可以通过调整模型偏置解决这个问题，但我最终也没找到模型偏置怎么调，只能采用最粗暴的手段：
         * 根据机器渲染的偏移量反向调整。注意：有向方块的方向的方向和玩家放置的时候面向的方向相反。 */
        switch (direction){
            case NORTH -> {
                rotation = 180;
                pPoseStack.translate(1.0D,0,1.0D);
            }
            case WEST -> {
                rotation = 90;
                pPoseStack.translate(1.0D,0,0);
            }
            case SOUTH -> {
                rotation = 0;
                pPoseStack.translate(-0D,0,-0D);
            }
            case EAST -> {
                rotation = 270;
                pPoseStack.translate(0,0,1.0D);
            }
        }

        boolean running = true;
        if (running){
            count = (count + 1) % 360;
        }

        //边上的四个齿轮
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        pPoseStack.translate(-0.6, 0.75, 1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        pPoseStack.translate(-0.6, 0.75, -1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        pPoseStack.translate(0.6, 0.75, -1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        pPoseStack.translate(0.6, 0.75, 1.0625);
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(-count));
        renderBlockModel(model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        pPoseStack.translate(0.4 * offset/90, 0, 0);
        //格架
        renderBlockModel(model2,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.translate(0, 0, sway * 0.3);
        //机械臂
        renderBlockModel(model1,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

        //正在制作的物品
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        //位置平移
        pPoseStack.translate(0,0.85,0);
        //旋转到在锻压机上平放（Axis.XN是绕X轴翻转）
        pPoseStack.mulPose(Axis.XN.rotationDegrees(-90));
        pPoseStack.mulPose(Axis.ZN.rotationDegrees(90));
        //大小缩小一半
        pPoseStack.scale(0.5F,0.5F,0.5F);
        ItemStack itemStack = new ItemStack(ModItems.grenade_generic.get());
        BakedModel productModel = itemRenderer.getModel(itemStack, pBlockEntity.getLevel(), null, 0);
        itemRenderer.render(itemStack, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,productModel);
        pPoseStack.popPose();

        //装配机身体部分
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YN.rotationDegrees(rotation));
        renderBlockModel(model3,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

//        pPoseStack.translate(-1.0D,0,-1.0D);
        pPoseStack.popPose();
    }

}
