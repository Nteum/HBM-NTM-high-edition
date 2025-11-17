package com.hbm.render;

import com.hbm.HBM;
import com.hbm.render.entity.FiveHundredKgExplosionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * Close port of the original RenderDeathBlast – towering additive spikes and a
 * collapsing plasma orb – so the 500kg blast matches classic HBM visuals.
 */
public class FiveHundredKgExplosionRenderer extends EntityRenderer<FiveHundredKgExplosionEntity> {

    private static final int BEAM_COUNT = 8;
    private static final float BEAM_HEIGHT = 250.0F;
    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation(HBM.MODID, "textures/entity/duck.png");

    public FiveHundredKgExplosionRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(FiveHundredKgExplosionEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        renderBeamRing(entity, partialTick, poseStack, buffer, 0.5F, 1.0F, 0.0F, 0.0F);
        renderBeamRing(entity, partialTick, poseStack, buffer, 0.25F, 1.0F, 0.0F, 1.0F);
        renderOrb(entity, partialTick, poseStack, buffer);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void renderBeamRing(FiveHundredKgExplosionEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                                       float radius, float r, float g, float b) {
        float life = (entity.tickCount + partialTick) / (float) FiveHundredKgExplosionEntity.LIFETIME_TICKS;
        float alpha = Mth.clamp(1.0F - life, 0.0F, 1.0F);
        if (alpha <= 0.0F) {
            return;
        }
        VertexConsumer consumer = buffer.getBuffer(ModRenderTypes.ATOMIC_FLASH);
        Matrix4f matrix = poseStack.last().pose();
        double angleStep = Math.PI * 2.0D / BEAM_COUNT;
        for (int i = 0; i < BEAM_COUNT; i++) {
            double angle0 = angleStep * i;
            double angle1 = angle0 + angleStep;
            float x0 = (float) (Math.cos(angle0) * radius);
            float z0 = (float) (Math.sin(angle0) * radius);
            float x1 = (float) (Math.cos(angle1) * radius);
            float z1 = (float) (Math.sin(angle1) * radius);
            addBeamQuad(consumer, matrix, x0, z0, x1, z1, r, g, b, alpha * 0.85F);
        }
    }

    private static void addBeamQuad(VertexConsumer consumer, Matrix4f matrix, float x0, float z0, float x1, float z1, float r, float g, float b, float alpha) {
        consumer.vertex(matrix, x0, BEAM_HEIGHT, z0).color(r, g, b, alpha * 0.25F).endVertex();
        consumer.vertex(matrix, x0, 0.0F, z0).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x1, 0.0F, z1).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x1, BEAM_HEIGHT, z1).color(r, g, b, alpha * 0.25F).endVertex();
    }

    private static void renderOrb(FiveHundredKgExplosionEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float progress = Mth.clamp((entity.tickCount + partialTick) / (float) FiveHundredKgExplosionEntity.LIFETIME_TICKS, 0.0F, 1.0F);
        float scale = Math.max(0.0F, 10.0F - 10.0F * progress);
        if (scale <= 0.0F) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        drawSphere(poseStack, buffer.getBuffer(ModRenderTypes.DEATH_BLAST_CORE), 24, 32, 1.0F, 0.0F, 1.0F, progress);
        poseStack.scale(1.25F, 1.25F, 1.25F);
        float additiveAlpha = progress * 0.125F;
        for (int i = 0; i < 8; i++) {
            drawSphere(poseStack, buffer.getBuffer(ModRenderTypes.DEATH_BLAST_GLARE), 24, 32, 1.0F, 0.0F, 0.0F, additiveAlpha);
            poseStack.scale(1.05F, 1.05F, 1.05F);
        }
        poseStack.popPose();
    }

    private static void drawSphere(PoseStack poseStack, VertexConsumer consumer, int latSegments, int lonSegments,
                                   float r, float g, float b, float alpha) {
        if (alpha <= 0.0F) {
            return;
        }
        Matrix4f matrix = poseStack.last().pose();
        for (int lat = 0; lat < latSegments; lat++) {
            float lat0 = (float) Math.PI * ((float) lat / latSegments - 0.5F);
            float lat1 = (float) Math.PI * (((float) lat + 1.0F) / latSegments - 0.5F);
            float sinLat0 = Mth.sin(lat0);
            float cosLat0 = Mth.cos(lat0);
            float sinLat1 = Mth.sin(lat1);
            float cosLat1 = Mth.cos(lat1);

            for (int lon = 0; lon < lonSegments; lon++) {
                float lon0 = (float) (2.0F * Math.PI * lon / lonSegments);
                float lon1 = (float) (2.0F * Math.PI * (lon + 1.0F) / lonSegments);
                float cosLon0 = Mth.cos(lon0);
                float sinLon0 = Mth.sin(lon0);
                float cosLon1 = Mth.cos(lon1);
                float sinLon1 = Mth.sin(lon1);

                float x00 = cosLon0 * cosLat0;
                float z00 = sinLon0 * cosLat0;
                float y00 = sinLat0;

                float x01 = cosLon1 * cosLat0;
                float z01 = sinLon1 * cosLat0;
                float y01 = sinLat0;

                float x10 = cosLon0 * cosLat1;
                float z10 = sinLon0 * cosLat1;
                float y10 = sinLat1;

                float x11 = cosLon1 * cosLat1;
                float z11 = sinLon1 * cosLat1;
                float y11 = sinLat1;

                addTriangle(consumer, matrix, x00, y00, z00, x10, y10, z10, x11, y11, z11, r, g, b, alpha);
                addTriangle(consumer, matrix, x00, y00, z00, x11, y11, z11, x01, y01, z01, r, g, b, alpha);
            }
        }
    }

    private static void addTriangle(VertexConsumer consumer, Matrix4f matrix,
                                    float x0, float y0, float z0,
                                    float x1, float y1, float z1,
                                    float x2, float y2, float z2,
                                    float r, float g, float b, float alpha) {
        consumer.vertex(matrix, x0, y0, z0).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, alpha).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, alpha).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(FiveHundredKgExplosionEntity entity) {
        return FALLBACK_TEXTURE;
    }
}
