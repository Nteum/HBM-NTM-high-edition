package net.mcreator.nuclearcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.nuclearcraft.entity.AtomicBombExplosionEntity;
import net.mcreator.nuclearcraft.entity.layer.AtomicBombExplosionLayer;
import net.mcreator.nuclearcraft.entity.model.AtomicBombExplosionModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/client/renderer/AtomicBombExplosionRenderer.class */
public class AtomicBombExplosionRenderer extends GeoEntityRenderer<AtomicBombExplosionEntity> {
    public AtomicBombExplosionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AtomicBombExplosionModel());
        this.f_114477_ = 10.0f;
        addRenderLayer(new AtomicBombExplosionLayer(this));
    }

    public RenderType getRenderType(AtomicBombExplosionEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_(m_5478_(animatable));
    }

    public void preRender(PoseStack poseStack, AtomicBombExplosionEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.scaleHeight = 1.0f;
        this.scaleWidth = 1.0f;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
