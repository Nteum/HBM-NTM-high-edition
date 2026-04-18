package com.hbm.render.entity.effect;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.effect.EntityNukeTorex.Cloudlet;
import com.hbm.entity.effect.EntityNukeTorex.TorexType;
import com.hbm.render.util.DirectBufferAccess;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.Random;

import static com.hbm.lib.internal.UnsafeHolder.U;

public class EntityTorexRender extends EntityRenderer<EntityNukeTorex> {

    public static final ResourceLocation TEXTURE_PARTICLE = new ResourceLocation(HBM.MODID, "textures/particle/particle_base.png");
    public static final ResourceLocation TEXTURE_FLARE = new ResourceLocation(HBM.MODID, "textures/particle/flare.png");

    public static final int FLASH_BASE_DURATION = 30;
    public static final int FLARE_BASE_DURATION = 100;

    private static final float ONE_THIRD = 1F / 3F;

    // NEW_ENTITY vertex format: pos(12) + color(4) + uv0(8) + uv1(4) + uv2(4) + normal(3) + padding(1) = 36
    private static final int VERTEX_STRIDE = 36;
    private static final int QUAD_STRIDE = VERTEX_STRIDE * 4;

    private static final Comparator<Cloudlet> FAR_TO_NEAR = (a, b) -> Double.compare(b.renderSortDistanceSq, a.renderSortDistanceSq);
    private final Vector3f cameraRight = new Vector3f();
    private final Vector3f cameraUp = new Vector3f();

    public EntityTorexRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityNukeTorex pEntity) {
        return TEXTURE_PARTICLE;
    }

    // --- CE static helpers ---

    private static float getCloudAlphaBase(EntityNukeTorex cloud) {
        int maxAge = cloud.getEntityMaxAge();
        int fadeOut = maxAge * 3 / 4;
        int life = cloud.tickCount;
        if (life > fadeOut) {
            return 1F - (float) (life - fadeOut) / (float) (maxAge - fadeOut);
        }
        return 1F;
    }

    private static float getCloudletLifeFrac(Cloudlet cloudlet) {
        return (float) cloudlet.age / (float) cloudlet.cloudletLife;
    }

    private static float getCloudletAlpha(Cloudlet cloudlet, float lifeFrac, float cloudAlphaBase) {
        float alpha = (1F - lifeFrac) * cloudAlphaBase;
        if (cloudlet.type == TorexType.CONDENSATION) {
            alpha *= 0.25F;
        }
        if (alpha < 0.0001F) {
            return 0.0001F;
        }
        return alpha > 1F ? 1F : alpha;
    }

    private static float getCloudletScale(Cloudlet cloudlet, float lifeFrac) {
        return cloudlet.getStartingScale() + lifeFrac * cloudlet.getGrowingScale();
    }

    private static float getCloudletBrightness(Cloudlet cloudlet) {
        return cloudlet.type == TorexType.CONDENSATION ? 0.9F : 0.75F * cloudlet.colorMod;
    }

    private static double getCloudletGreying(Cloudlet cloudlet) {
        return cloudlet.type == TorexType.RING ? 0.05D : 0D;
    }

    private static double getCloudletColor(Cloudlet cloudlet, double prevColor, double color, float partialTicks, double greying) {
        if (cloudlet.type == TorexType.CONDENSATION) {
            return 1D;
        }
        return (prevColor + (color - prevColor) * partialTicks) + greying;
    }

    private static float clampCloudletColor(double color, float brightness) {
        float channel = (float) color * brightness;
        if (channel < 0.15F) {
            return 0.15F;
        }
        return channel > 1F ? 1F : channel;
    }

    private static int getCloudletLightmap(float r, float g, float b) {
        float avgBrightness = (r + g + b) * ONE_THIRD;
        if (avgBrightness > 1F) {
            avgBrightness = 1F;
        }
        int br = (int) (avgBrightness * 240F);
        return br < 48 ? 48 : br;
    }

    // --- Main render entry ---

    @Override
    public void render(EntityNukeTorex cloud, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        float scale = cloud.getEntityScale();
        float flashDuration = scale * FLASH_BASE_DURATION;
        float flareDuration = scale * FLARE_BASE_DURATION;

        pPoseStack.pushPose();

        if (!cloud.cloudlets.isEmpty()) {
            cloudletWrapper(cloud, pPoseStack, pBuffer, pPartialTick);
        }

        if (cloud.tickCount < flareDuration + 1) {
            flareWrapper(cloud, pPoseStack, pPartialTick, flareDuration);
        }

        if (cloud.tickCount < flashDuration + 1) {
            flashWrapper(cloud, pPoseStack, pPartialTick, flashDuration);
        }

        applyPlayerShake(cloud);

        pPoseStack.popPose();

        super.render(cloud, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    // --- Cloudlet rendering ---

    private void cloudletWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, MultiBufferSource pBuffer, float partialTick) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Quaternionf rotation = camera.rotation();
        cameraRight.set(1.0F, 0.0F, 0.0F).rotate(rotation);
        cameraUp.set(0.0F, 1.0F, 0.0F).rotate(rotation);

        sortCloudlets(cloud, cameraPos);
        if (cloud.cloudlets.isEmpty()) {
            return;
        }

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE_PARTICLE));
        Matrix4f pose = pPoseStack.last().pose();
        Matrix3f normal = pPoseStack.last().normal();

        if (consumer instanceof BufferBuilder bb
                && DefaultVertexFormat.NEW_ENTITY.getVertexSize() == VERTEX_STRIDE) {
            tessellateAllUnsafe(bb, cloud, pose, normal, partialTick);
        } else {
            float cloudAlphaBase = getCloudAlphaBase(cloud);
            for (int i = 0, count = cloud.cloudlets.size(); i < count; i++) {
                tessellateCloudlet(consumer, pose, normal, cloud, cloud.cloudlets.get(i), partialTick, cloudAlphaBase);
            }
        }
    }

    private void applyPlayerShake(EntityNukeTorex cloud) {
        if (!cloud.didPlaySound || cloud.didShake) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        int hurtDuration = 100 - (int) player.distanceTo(cloud);
        if (hurtDuration > 0) {
            player.hurtTime = (int) (hurtDuration * 1.5F);
            player.hurtDuration = hurtDuration;
        }
        cloud.didShake = true;
    }

    private void sortCloudlets(EntityNukeTorex cloud, Vec3 cameraPos) {
        int clientTick = Minecraft.getInstance().gui.getGuiTicks();
        if (cloud.lastRenderSortTick == clientTick) {
            return;
        }

        int count = cloud.cloudlets.size();
        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = cloud.cloudlets.get(i);
            cloudlet.renderSortDistanceSq = cameraPos.distanceToSqr(cloudlet.posX, cloudlet.posY, cloudlet.posZ);
        }
        cloud.cloudlets.sort(FAR_TO_NEAR);
        cloud.lastRenderSortTick = clientTick;
    }

    private static float getCloudletRenderPos(double prevPos, double pos, double cloudPos, float partialTick) {
        return (float) (Mth.lerp((double) partialTick, prevPos, pos) - cloudPos);
    }

    private void tessellateAllUnsafe(BufferBuilder bb, EntityNukeTorex entity, Matrix4f pose, Matrix3f normal, float partialTick) {
        DirectBufferAccess dba = (DirectBufferAccess) bb;
        int quadCount = entity.cloudlets.size();
        int totalBytes = quadCount * QUAD_STRIDE;

        dba.hbm$ensureCapacity(totalBytes);
        long baseAddr = dba.hbm$bufferAddress() + dba.hbm$nextElementByte();

        // Pre-transform camera basis through pose's 3x3 (direction-only, no translation)
        float crx = cameraRight.x(), cry = cameraRight.y(), crz = cameraRight.z();
        float cux = cameraUp.x(), cuy = cameraUp.y(), cuz = cameraUp.z();
        float trRx = pose.m00() * crx + pose.m10() * cry + pose.m20() * crz;
        float trRy = pose.m01() * crx + pose.m11() * cry + pose.m21() * crz;
        float trRz = pose.m02() * crx + pose.m12() * cry + pose.m22() * crz;
        float trUx = pose.m00() * cux + pose.m10() * cuy + pose.m20() * cuz;
        float trUy = pose.m01() * cux + pose.m11() * cuy + pose.m21() * cuz;
        float trUz = pose.m02() * cux + pose.m12() * cuy + pose.m22() * cuz;

        int overlay = OverlayTexture.NO_OVERLAY;
        int packedNormal = (BufferVertexConsumer.normalIntValue(normal.m10()) & 0xFF)
                | ((BufferVertexConsumer.normalIntValue(normal.m11()) & 0xFF) << 8)
                | ((BufferVertexConsumer.normalIntValue(normal.m12()) & 0xFF) << 16);

        float p00 = pose.m00(), p01 = pose.m01(), p02 = pose.m02();
        float p10 = pose.m10(), p11 = pose.m11(), p12 = pose.m12();
        float p20 = pose.m20(), p21 = pose.m21(), p22 = pose.m22();
        float p30 = pose.m30(), p31 = pose.m31(), p32 = pose.m32();

        float cloudAlphaBase = getCloudAlphaBase(entity);
        double cloudOriginX = Mth.lerp((double) partialTick, entity.xOld, entity.getX());
        double cloudOriginY = Mth.lerp((double) partialTick, entity.yOld, entity.getY());
        double cloudOriginZ = Mth.lerp((double) partialTick, entity.zOld, entity.getZ());

        long addr = baseAddr;
        int written = 0;

        for (int i = 0; i < quadCount; i++) {
            Cloudlet cloudlet = entity.cloudlets.get(i);

            float lifeFrac = getCloudletLifeFrac(cloudlet);
            float alpha = getCloudletAlpha(cloudlet, lifeFrac, cloudAlphaBase);
            if (alpha <= 0.001F) {
                continue;
            }
            float scale = getCloudletScale(cloudlet, lifeFrac);
            float brightness = getCloudletBrightness(cloudlet);
            double greying = getCloudletGreying(cloudlet);
            double colorR = getCloudletColor(cloudlet, cloudlet.prevColorR, cloudlet.colorR, partialTick, greying);
            double colorG = getCloudletColor(cloudlet, cloudlet.prevColorG, cloudlet.colorG, partialTick, greying);
            double colorB = getCloudletColor(cloudlet, cloudlet.prevColorB, cloudlet.colorB, partialTick, greying);
            float r = clampCloudletColor(colorR, brightness);
            float g = clampCloudletColor(colorG, brightness);
            float b = clampCloudletColor(colorB, brightness);
            int br = getCloudletLightmap(r, g, b);

            int red = (int) (r * 255F);
            int green = (int) (g * 255F);
            int blue = (int) (b * 255F);
            int alphaByte = (int) (alpha * 255F);
            int packedColor = (red & 0xFF) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 16) | ((alphaByte & 0xFF) << 24);
            int packedLightmap = br | (br << 16);

            // Scaled pre-transformed right/up
            float srx = trRx * scale, sry = trRy * scale, srz = trRz * scale;
            float sux = trUx * scale, suy = trUy * scale, suz = trUz * scale;

            // Transform center through full affine pose
            float cx = getCloudletRenderPos(cloudlet.prevPosX, cloudlet.posX, cloudOriginX, partialTick);
            float cy = getCloudletRenderPos(cloudlet.prevPosY, cloudlet.posY, cloudOriginY, partialTick);
            float cz = getCloudletRenderPos(cloudlet.prevPosZ, cloudlet.posZ, cloudOriginZ, partialTick);
            float tcx = p00 * cx + p10 * cy + p20 * cz + p30;
            float tcy = p01 * cx + p11 * cy + p21 * cz + p31;
            float tcz = p02 * cx + p12 * cy + p22 * cz + p32;

            // v0: center - right - up  (u=1, v=1)
            writeVertex(addr,
                    tcx - srx - sux, tcy - sry - suy, tcz - srz - suz,
                    packedColor, 1f, 1f, overlay, packedLightmap, packedNormal);
            // v1: center - right + up  (u=1, v=0)
            writeVertex(addr + VERTEX_STRIDE,
                    tcx - srx + sux, tcy - sry + suy, tcz - srz + suz,
                    packedColor, 1f, 0f, overlay, packedLightmap, packedNormal);
            // v2: center + right + up  (u=0, v=0)
            writeVertex(addr + VERTEX_STRIDE * 2L,
                    tcx + srx + sux, tcy + sry + suy, tcz + srz + suz,
                    packedColor, 0f, 0f, overlay, packedLightmap, packedNormal);
            // v3: center + right - up  (u=0, v=1)
            writeVertex(addr + VERTEX_STRIDE * 3L,
                    tcx + srx - sux, tcy + sry - suy, tcz + srz - suz,
                    packedColor, 0f, 1f, overlay, packedLightmap, packedNormal);

            addr += QUAD_STRIDE;
            written++;
        }

        int bytesWritten = written * QUAD_STRIDE;
        dba.hbm$setNextElementByte(dba.hbm$nextElementByte() + bytesWritten);
        dba.hbm$setVertices(dba.hbm$vertices() + written * 4);
    }

    private static void writeVertex(long a, float x, float y, float z,
                                    int color, float u, float v,
                                    int overlay, int lightmap, int normal) {
        U.putFloat(a, x);
        U.putFloat(a + 4, y);
        U.putFloat(a + 8, z);
        U.putInt(a + 12, color);
        U.putFloat(a + 16, u);
        U.putFloat(a + 20, v);
        U.putInt(a + 24, overlay);
        U.putInt(a + 28, lightmap);
        U.putInt(a + 32, normal);
    }

    private void tessellateCloudlet(VertexConsumer buffer, Matrix4f pose, Matrix3f normal,
                                    EntityNukeTorex cloud, Cloudlet cloudlet, float partialTick, float cloudAlphaBase) {
        float lifeFrac = getCloudletLifeFrac(cloudlet);
        float alpha = getCloudletAlpha(cloudlet, lifeFrac, cloudAlphaBase);
        if (alpha <= 0.001F) return;

        float scale = getCloudletScale(cloudlet, lifeFrac);
        float brightness = getCloudletBrightness(cloudlet);
        double greying = getCloudletGreying(cloudlet);
        double colorR = getCloudletColor(cloudlet, cloudlet.prevColorR, cloudlet.colorR, partialTick, greying);
        double colorG = getCloudletColor(cloudlet, cloudlet.prevColorG, cloudlet.colorG, partialTick, greying);
        double colorB = getCloudletColor(cloudlet, cloudlet.prevColorB, cloudlet.colorB, partialTick, greying);
        float r = clampCloudletColor(colorR, brightness);
        float g = clampCloudletColor(colorG, brightness);
        float b = clampCloudletColor(colorB, brightness);
        int br = getCloudletLightmap(r, g, b);
        int lightmap = br | (br << 16);

        float rightX = cameraRight.x() * scale;
        float rightY = cameraRight.y() * scale;
        float rightZ = cameraRight.z() * scale;
        float upX = cameraUp.x() * scale;
        float upY = cameraUp.y() * scale;
        float upZ = cameraUp.z() * scale;

        double cloudOriginX = Mth.lerp((double) partialTick, cloud.xOld, cloud.getX());
        double cloudOriginY = Mth.lerp((double) partialTick, cloud.yOld, cloud.getY());
        double cloudOriginZ = Mth.lerp((double) partialTick, cloud.zOld, cloud.getZ());
        float x = getCloudletRenderPos(cloudlet.prevPosX, cloudlet.posX, cloudOriginX, partialTick);
        float y = getCloudletRenderPos(cloudlet.prevPosY, cloudlet.posY, cloudOriginY, partialTick);
        float z = getCloudletRenderPos(cloudlet.prevPosZ, cloudlet.posZ, cloudOriginZ, partialTick);

        putVertex(buffer, pose, normal, x - rightX - upX, y - rightY - upY, z - rightZ - upZ, 1f, 1f, r, g, b, alpha, lightmap);
        putVertex(buffer, pose, normal, x - rightX + upX, y - rightY + upY, z - rightZ + upZ, 1f, 0f, r, g, b, alpha, lightmap);
        putVertex(buffer, pose, normal, x + rightX + upX, y + rightY + upY, z + rightZ + upZ, 0f, 0f, r, g, b, alpha, lightmap);
        putVertex(buffer, pose, normal, x + rightX - upX, y + rightY - upY, z + rightZ - upZ, 0f, 1f, r, g, b, alpha, lightmap);
    }

    private void putVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, float x, float y, float z,
                           float u, float v, float red, float green, float blue, float alpha, int light) {
        buffer.vertex(pose, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    // --- Flare rendering ---

    private void flareWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, float partialTick, float flareDuration) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf rotation = camera.rotation();
        Vector3f right = new Vector3f(1, 0, 0).rotate(rotation);
        Vector3f up = new Vector3f(0, 1, 0).rotate(rotation);

        double age = Math.min(cloud.tickCount + partialTick, flareDuration);
        float alpha = (float) Math.min(1, (flareDuration - age) / flareDuration);

        Random rand = new Random(cloud.getId());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentShader);
        RenderSystem.setShaderTexture(0, TEXTURE_FLARE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);

        Matrix4f pose = pPoseStack.last().pose();
        Matrix3f normalMat = pPoseStack.last().normal();

        for (int i = 0; i < 3; i++) {
            float x = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
            float y = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
            float z = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
            float posY = y + (float) cloud.coreHeight;
            float scale = (float) (10 * cloud.rollerSize);

            int br = (int) (alpha * 240);
            int lightmap = br | (br << 16);

            float srx = right.x() * scale, sry = right.y() * scale, srz = right.z() * scale;
            float sux = up.x() * scale, suy = up.y() * scale, suz = up.z() * scale;

            putVertex(buf, pose, normalMat, x - srx - sux, posY - sry - suy, z - srz - suz, 1f, 1f, 1f, 1f, 1f, alpha, lightmap);
            putVertex(buf, pose, normalMat, x - srx + sux, posY - sry + suy, z - srz + suz, 1f, 0f, 1f, 1f, 1f, alpha, lightmap);
            putVertex(buf, pose, normalMat, x + srx + sux, posY + sry + suy, z + srz + suz, 0f, 0f, 1f, 1f, 1f, alpha, lightmap);
            putVertex(buf, pose, normalMat, x + srx - sux, posY + sry - suy, z + srz - suz, 0f, 1f, 1f, 1f, 1f, alpha, lightmap);
        }

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    // --- Flash rendering ---

    private void flashWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, float partialTick, float flashDuration) {
        if (cloud.tickCount < flashDuration) {
            double intensity = (cloud.tickCount + partialTick) / flashDuration;
            intensity = intensity * Math.pow(Math.E, -intensity) * 2.717391304D;

            renderFlash(pPoseStack, 50F * flashDuration / (float) FLASH_BASE_DURATION, intensity, cloud.coreHeight);
        }
    }

    private void renderFlash(PoseStack pPoseStack, float scale, double intensity, double height) {
        double inverse = 1.0D - intensity;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buf = tesselator.getBuilder();

        Random random = new Random(432L);

        pPoseStack.pushPose();
        pPoseStack.scale(0.2F, 0.2F, 0.2F);
        pPoseStack.translate(0, height * 4, 0);

        for (int i = 0; i < 300; i++) {
            pPoseStack.pushPose();
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
            pPoseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));

            float vert1 = (random.nextFloat() * 20.0F + 5.0F + 10.0F) * (float) (intensity * scale);
            float vert2 = (random.nextFloat() * 2.0F + 1.0F + 2.0F) * (float) (intensity * scale);

            Matrix4f mat = pPoseStack.last().pose();

            buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            buf.vertex(mat, 0, 0, 0).color(1.0F, 1.0F, 1.0F, (float) inverse).endVertex();
            buf.vertex(mat, (float) (-0.866D * vert2), vert1, (float) (-0.5D * vert2)).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(mat, (float) (0.866D * vert2), vert1, (float) (-0.5D * vert2)).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(mat, 0.0F, vert1, vert2).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            buf.vertex(mat, (float) (-0.866D * vert2), vert1, (float) (-0.5D * vert2)).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
            tesselator.end();

            pPoseStack.popPose();
        }

        pPoseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}
