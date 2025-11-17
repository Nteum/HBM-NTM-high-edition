package com.hbm.render;

import com.hbm.HBM;
import com.hbm.render.entity.AtomicBombExplosionEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

/**
 * Port of the classic RenderCloudTom effect: animated nuclear shock pillars
 * textured with tomblast.png that expand over time.
 */
public class AtomicBombExplosionRenderer extends EntityRenderer<AtomicBombExplosionEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(HBM.MODID, "textures/models/explosion/tomblast.png");
    private static final int SEGMENTS = 16;
    private static final int LAYERS = 5;
    private static final float HEIGHT_BASE = 20.0F;
    private static final float DEPTH = 20.0F;

    public AtomicBombExplosionRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(AtomicBombExplosionEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        Matrix4f matrix = poseStack.last().pose();

        double scale = Math.max(1.0D, entity.tickCount + partialTick);
        float angleStep = (float) (Math.PI * 2.0D / SEGMENTS);
        float playerTicks = Minecraft.getInstance().player != null
                ? (Minecraft.getInstance().player.tickCount + partialTick)
                : (entity.tickCount + partialTick);
        float movement = -playerTicks * 0.05F; // matches legacy texture scroll speed

        for (int segment = 0; segment < SEGMENTS; segment++) {
            double baseAngle = angleStep * segment;
            double nextAngle = baseAngle + angleStep;
            double cos0 = Math.cos(baseAngle);
            double sin0 = Math.sin(baseAngle);
            double cos1 = Math.cos(nextAngle);
            double sin1 = Math.sin(nextAngle);

            for (int layer = 0; layer < LAYERS; layer++) {
                double modifier = 1.0D - layer * 0.025D;
                double topY = HEIGHT_BASE + layer * 10.0D;
                double offset = layer == 0 ? 1.0D : 1.0D / layer;

                float vTop = (float) (1.0D + offset + movement);
                float vBottom = (float) (offset + movement);

                float x0 = (float) (cos0 * scale * modifier);
                float z0 = (float) (sin0 * scale * modifier);
                float x1 = (float) (cos1 * scale * modifier);
                float z1 = (float) (sin1 * scale * modifier);

                addVertex(consumer, matrix, x0, (float) topY, z0, 0.0F, vTop, 0.0F);
                addVertex(consumer, matrix, x0, -DEPTH, z0, 0.0F, vBottom, 1.0F);
                addVertex(consumer, matrix, x1, -DEPTH, z1, 1.0F, vBottom, 1.0F);
                addVertex(consumer, matrix, x1, (float) topY, z1, 1.0F, vTop, 0.0F);
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float u, float v, float alpha) {
        consumer.vertex(matrix, x, y, z)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(AtomicBombExplosionEntity entity) {
        return TEXTURE;
    }
}
