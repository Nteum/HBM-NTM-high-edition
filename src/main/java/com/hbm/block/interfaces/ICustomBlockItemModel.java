package com.hbm.block.interfaces;

import com.hbm.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * 需要特殊建模的物品模型
 * */
public interface ICustomBlockItemModel {
    default BakedModel[] getModels(){
        return new BakedModel[0];
    }

    default void renderStatic(ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay){
        poseStack.pushPose();
        BakedModel[] models = getModels();
        for (BakedModel model : models) {
            RenderUtils.renderModel(model, poseStack, buffer, light, overlay, RenderType.cutout());
        }
        poseStack.popPose();
    }
}
