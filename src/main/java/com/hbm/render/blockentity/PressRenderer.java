package com.hbm.render.blockentity;

import com.hbm.HBMxx;
import com.hbm.Procedure0;
import com.hbm.block.ModBlocks;
import com.hbm.block.machine.BlockPress;
import com.hbm.blockentity.machine.PressEntity;
import com.hbm.model.SpecialModels;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.List;

public class PressRenderer implements BlockEntityRenderer<PressEntity> {
    private int cnt = 0;
    public PressRenderer(Context pContext){
//        pContext.getBlockRenderDispatcher().getBlockModel(ModBlocks.machine_press.get().defaultBlockState());
//        EntityModelSet modelSet = pContext.getModelSet();
//        ModelPart body = pContext.bakeLayer(new ModelLayerLocation(new ResourceLocation(HBMxx.MODID, "block/press_body"), "body"));
//        ModelPart head = pContext.bakeLayer(new ModelLayerLocation(new ResourceLocation(HBMxx.MODID, "block/press_head"), "head"));
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
        //显示当前正在锻压的物品
        pPoseStack.pushPose();
        //位置平移
        pPoseStack.translate(0.5,1.0,0.5);
        //旋转到在锻压机上平放（Axis.XN是绕X轴翻转）
        pPoseStack.mulPose(Axis.XN.rotationDegrees(-90));
        //大小缩小一半
        pPoseStack.scale(0.5F,0.5F,0.5F);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = pBlockEntity.getItem(2);
        BakedModel model = itemRenderer.getModel(itemStack, pBlockEntity.getLevel(), null, 0);
        itemRenderer.render(itemStack, ItemDisplayContext.GUI,true,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,model);
        pPoseStack.popPose();

        //渲染锻压机头的移动
        pPoseStack.pushPose();
        int press = pBlockEntity.press;
        double extension = 0.9 - 0.9 * press / 100;
        pPoseStack.translate(0,extension,0);
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState blockState = ModBlocks.part_press_head.get().defaultBlockState();
        blockRenderer.renderSingleBlock(blockState,pPoseStack,pBuffer,pPackedLight,pPackedOverlay);
        pPoseStack.popPose();
    }
}
