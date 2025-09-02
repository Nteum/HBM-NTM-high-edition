package com.hbm.render;

import com.hbm.utils.EnumUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class RenderUtils {
    //参考blockRenderDispatcher的renderSingleBlock对单个模型进行渲染
    public static void renderBlockModel(BakedModel model, BlockState state, ModelBlockRenderer modelRenderer,
                                        PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, RenderType renderType){
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        int i = blockColors.getColor(state, (BlockAndTintGetter)null, (BlockPos)null, 0);
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, RandomSource.create(42), (ModelData) null))
            modelRenderer.renderModel(pPoseStack.last(), pBuffer.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)),
                    state, model, f,f1,f2, pPackedLight, pPackedOverlay, ModelData.EMPTY, rt);
    }
    /**
     * 主要参考ModelBlockRender#renderModel
     * */
    public static void renderModel(BakedModel model, PoseStack pPose, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, RenderType renderType){
        VertexConsumer buffer;
        if (renderType == null || (buffer = pBuffer.getBuffer(renderType)) == null) return;
        float pRed = 1.0F;
        float pGreen = 1.0f;
        float pBlue = 1.0f;
        BlockState dummyState = Blocks.AIR.defaultBlockState();
        RandomSource randomSource = RandomSource.create(42L);
        for(Direction direction : EnumUtils.DIRECTIONS) {
            renderQuadList(pPose.last(), buffer, pRed, pGreen, pBlue, model.getQuads(dummyState, direction, randomSource), pPackedLight, pPackedOverlay);
        }

        renderQuadList(pPose.last(), buffer, pRed, pGreen, pBlue, model.getQuads(dummyState, (Direction)null, randomSource), pPackedLight, pPackedOverlay);
    }
    private static void renderQuadList(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, List<BakedQuad> pQuads, int pPackedLight, int pPackedOverlay) {
        for(BakedQuad bakedquad : pQuads) {
            float f;
            float f1;
            float f2;
            if (bakedquad.isTinted()) {
                f = Mth.clamp(pRed, 0.0F, 1.0F);
                f1 = Mth.clamp(pGreen, 0.0F, 1.0F);
                f2 = Mth.clamp(pBlue, 0.0F, 1.0F);
            } else {
                f = 1.0F;
                f1 = 1.0F;
                f2 = 1.0F;
            }

            pConsumer.putBulkData(pPose, bakedquad, f, f1, f2, pPackedLight, pPackedOverlay);
        }

    }
}
