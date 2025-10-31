package net.mcreator.nuclearcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.nuclearcraft.entity.FiveHundredKgExplosionEntity;
import net.mcreator.nuclearcraft.entity.layer.FiveHundredKgExplosionLayer;
import net.mcreator.nuclearcraft.entity.model.FiveHundredKgExplosionModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/client/renderer/FiveHundredKgExplosionRenderer.class */
public class FiveHundredKgExplosionRenderer extends GeoEntityRenderer<FiveHundredKgExplosionEntity> {
    public FiveHundredKgExplosionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FiveHundredKgExplosionModel());
        this.f_114477_ = 5.0f;
        addRenderLayer(new FiveHundredKgExplosionLayer(this));
    }

    public RenderType getRenderType(FiveHundredKgExplosionEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_(m_5478_(animatable));
    }

    public void preRender(PoseStack poseStack, FiveHundredKgExplosionEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.scaleHeight = 1.0f;
        this.scaleWidth = 1.0f;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getDeathMaxRotation(FiveHundredKgExplosionEntity entityLivingBaseIn) {
        return 0.0f;
    }
}
