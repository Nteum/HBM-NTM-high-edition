package com.hbm.render.blockentity;

import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.render.model.Models;
import com.hbm.render.utils.ModelAdjustUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

import static com.hbm.render.RenderUtils.renderBlockModel;

public class CrucibleRenderer implements BlockEntityRenderer<CrucibleEntity> {
    public static BakedModel crucible_model;

    public CrucibleRenderer(BlockEntityRendererProvider.Context pContext){
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        crucible_model = modelManager.getModel(Models.CRUCIBLE);
    }
    @Override
    public void render(CrucibleEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();
        BlockState blockState = pBlockEntity.getBlockState();
        //根据方向确定旋转角度
//        Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
//        int rotation = 0;
//        switch (direction){
//            case NORTH -> rotation = 0;
//            case WEST -> rotation = 90;
//            case SOUTH -> rotation = 180;
//            case EAST -> rotation = 270;
//        }
        ModelAdjustUtils.generalMachineRotate(pPoseStack, blockState);

        //坩埚本体部分
        pPoseStack.pushPose();
//        pPoseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        renderBlockModel(crucible_model,blockState,modelRenderer,pPoseStack,pBuffer,pPackedLight,pPackedOverlay,null);
        pPoseStack.popPose();

//        ResourceLocation overlay = new ResourceLocation(HBMxx.MODID,"fluid/irradiated_water_overlay");
//        pPoseStack.pushPose();
//        VertexConsumer buffer = pBuffer.getBuffer(RenderType.solid());
//        AbstractTexture texture = Minecraft.getInstance().textureManager.getTexture(overlay);
//        //texture和sprite有什么关系
//        pPoseStack.popPose();

//        VertexConsumer buffer = pBuffer.getBuffer(RenderType.solid());
//        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(ModFluidTypes.SULFURIC_ACID.get());
//        Minecraft.getInstance().getModelManager().getAtlas(new ResourceLocation())
//        buffer.vertex()
    }
}
