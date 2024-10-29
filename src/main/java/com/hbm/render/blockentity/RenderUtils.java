package com.hbm.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

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
}
