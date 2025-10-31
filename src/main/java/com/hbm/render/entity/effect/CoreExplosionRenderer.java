package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityCoreExplosion;
import com.hbm.render.model.entity.CoreExplosionModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CoreExplosionRenderer extends GeoEntityRenderer<EntityCoreExplosion> {
    public CoreExplosionRenderer(EntityRendererProvider.Context context) {
        super(context, new CoreExplosionModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public RenderType getRenderType(EntityCoreExplosion animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, EntityCoreExplosion entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.shadowRadius = entity.getVariant().shadowRadius();
        this.scaleHeight = 1.0F;
        this.scaleWidth = 1.0F;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
