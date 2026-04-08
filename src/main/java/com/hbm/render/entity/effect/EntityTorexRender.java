package com.hbm.render.entity.effect;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityNukeTorex;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;

/** 用于渲染爆炸云 */
public class EntityTorexRender extends EntityRenderer<EntityNukeTorex> {
    public static final ResourceLocation TEXTURE_PARTICLE = new ResourceLocation(HBM.MODID,"textures/particle/hbm_smoke.png");
    public static final ResourceLocation TEXTURE_FLARE = new ResourceLocation(HBM.MODID,"textures/particle/flare.png");

    // Keep disabled for now: current VertexFormat path can crash on some Forge render states.
    // Cloud density is provided by vanilla smoke particle fallback in EntityNukeTorex.
    private static final boolean USE_CUSTOM_CLOUDLET_RENDER = false;

    private static final int CLOUDLET_COUNT = 1024; //调试粒子用

    private static final int CLOUDLET_Multiplier = 12; //调试粒子用
    private static final int MAX_SORTED_CLOUDLETS = CLOUDLET_COUNT * CLOUDLET_Multiplier; //调试粒子用
    private static final Comparator<CloudletEntry> FAR_TO_NEAR = (a, b) -> Double.compare(b.distanceSq, a.distanceSq);

    private final ArrayList<CloudletEntry> cloudletEntries = new ArrayList<>();
    private final Vector3f cameraRight = new Vector3f();
    private final Vector3f cameraUp = new Vector3f();

    public EntityTorexRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityNukeTorex pEntity) {
        return TEXTURE_PARTICLE;
    }

    @Override
    public void render(EntityNukeTorex pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (USE_CUSTOM_CLOUDLET_RENDER && !pEntity.cloudlets.isEmpty()) {
            cloudletWrapper(pEntity, pPoseStack, pBuffer, pPartialTick);
        }
//        if(pEntity.tickCount < 101) flashWrapper(pEntity, interp);
//        if(pEntity.tickCount < 10 && System.currentTimeMillis() - ModEventHandlerClient.flashTimestamp > 1_000) ModEventHandlerClient.flashTimestamp = System.currentTimeMillis();
//        if(pEntity.didPlaySound && !pEntity.didShake && System.currentTimeMillis() - ModEventHandlerClient.shakeTimestamp > 1_000) {
//            ModEventHandlerClient.shakeTimestamp = System.currentTimeMillis();
//            pEntity.didShake = true;
//            EntityPlayer player = MainRegistry.proxy.me();
//            player.hurtTime = 15;
//            player.maxHurtTime = 15;
//            player.attackedAtYaw = 0F;
//        }
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    /** 渲染爆炸云 */
    private void cloudletWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, MultiBufferSource pBuffer, float pPartialTick) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Quaternionf rotation = camera.rotation();
        cameraRight.set(1.0F, 0.0F, 0.0F).rotate(rotation);
        cameraUp.set(0.0F, 1.0F, 0.0F).rotate(rotation);

        buildSortedCloudletEntries(cloud, cameraPos, pPartialTick);
        if (cloudletEntries.isEmpty()) {
            return;
        }

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE_PARTICLE));
        Matrix4f pose = pPoseStack.last().pose();
        Matrix3f normal = pPoseStack.last().normal();
        for (int i = 0; i < cloudletEntries.size(); i++) {
            tessellateCloudlet(consumer, pose, normal, cloudletEntries.get(i), pPartialTick);
        }
    }
    /** 渲染爆炸闪光 */
//    private void flashWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, MultiBufferSource pBuffer, float interp) {
//
////        GL11.glPushMatrix();
//        pPoseStack.pushPose();
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0);
//        GL11.glDisable(GL11.GL_ALPHA_TEST);
//        GL11.glDepthMask(false);
////        RenderHelper.disableStandardItemLighting();
////
////        bindTexture(flash);
////
////        Tessellator tess = Tessellator.instance;
////        tess.startDrawingQuads();
//
//        double age = Math.min(cloud.tickCount + interp, 100);
//        float alpha = (float) ((100D - age) / 100F);
//
//        Random rand = new Random(cloud.getId());
//
//        for(int i = 0; i < 3; i++) {
//            float x = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
//            float y = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
//            float z = (float) (rand.nextGaussian() * 0.5F * cloud.rollerSize);
//            tessellateFlash(tess, x, y + cloud.coreHeight, z, (float) (25 * cloud.rollerSize), alpha, interp);
//        }
//
////        tess.draw();
//
//        GL11.glDepthMask(true);
//        GL11.glEnable(GL11.GL_ALPHA_TEST);
////        RenderHelper.enableStandardItemLighting();
//        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
//        GL11.glDisable(GL11.GL_BLEND);
//        pPoseStack.popPose();
////        GL11.glPopMatrix();
//    }
    private void buildSortedCloudletEntries(EntityNukeTorex cloud, Vec3 cameraPos, float partialTick) {
        cloudletEntries.clear();

        int limit = Math.min(cloud.cloudlets.size(), MAX_SORTED_CLOUDLETS);
        ensureEntryCapacity(limit);
        Vec3 cloudOrigin = cloud.position();
        int accepted = 0;
        for (int i = 0; i < cloud.cloudlets.size() && accepted < limit; i++) {
            EntityNukeTorex.Cloudlet cloudlet = cloud.cloudlets.get(i);
            float alpha = cloudlet.getAlpha();
            float scale = cloudlet.getScale();
            if (alpha <= 0.001F || scale <= 0.001F) {
                continue;
            }

            Vec3 interpPos = cloudlet.getInterpPos(partialTick);
            CloudletEntry entry = cloudletEntries.get(accepted++);
            entry.cloudlet = cloudlet;
            entry.relX = (float) (interpPos.x - cloudOrigin.x);
            entry.relY = (float) (interpPos.y - cloudOrigin.y);
            entry.relZ = (float) (interpPos.z - cloudOrigin.z);
            entry.distanceSq = cameraPos.distanceToSqr(interpPos);
        }

        if (accepted < cloudletEntries.size()) {
            cloudletEntries.subList(accepted, cloudletEntries.size()).clear();
        }
        cloudletEntries.sort(FAR_TO_NEAR);
    }

    private void ensureEntryCapacity(int target) {
        while (cloudletEntries.size() < target) {
            cloudletEntries.add(new CloudletEntry());
        }
    }

    /** 渲染单个云朵（避免每个 cloudlet 产生临时 Vector3f[]） */
    private void tessellateCloudlet(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, CloudletEntry entry, float partialTick) {
        EntityNukeTorex.Cloudlet cloud = entry.cloudlet;
        if (cloud == null) {
            return;
        }

        float alpha = Mth.clamp(cloud.getAlpha(), 0.0F, 1.0F);
        if (alpha <= 0.001F) {
            return;
        }

        float scale = cloud.getScale();
        float rightX = cameraRight.x() * scale;
        float rightY = cameraRight.y() * scale;
        float rightZ = cameraRight.z() * scale;
        float upX = cameraUp.x() * scale;
        float upY = cameraUp.y() * scale;
        float upZ = cameraUp.z() * scale;

        float x = entry.relX;
        float y = entry.relY;
        float z = entry.relZ;

        float brightness = cloud.type == EntityNukeTorex.TorexType.CONDENSATION ? 0.9F : 0.75F * cloud.colorMod;
        Vec3 color = cloud.getInterpColor(partialTick);
        float red = Mth.clamp((float) (color.x * brightness), 0.0F, 1.0F);
        float green = Mth.clamp((float) (color.y * brightness), 0.0F, 1.0F);
        float blue = Mth.clamp((float) (color.z * brightness), 0.0F, 1.0F);
        int fullBright = LightTexture.FULL_BRIGHT;

        putVertex(buffer, pose, normal, x - rightX - upX, y - rightY - upY, z - rightZ - upZ, 1.0F, 1.0F, red, green, blue, alpha, fullBright);
        putVertex(buffer, pose, normal, x - rightX + upX, y - rightY + upY, z - rightZ + upZ, 1.0F, 0.0F, red, green, blue, alpha, fullBright);
        putVertex(buffer, pose, normal, x + rightX + upX, y + rightY + upY, z + rightZ + upZ, 0.0F, 0.0F, red, green, blue, alpha, fullBright);
        putVertex(buffer, pose, normal, x + rightX - upX, y + rightY - upY, z + rightZ - upZ, 0.0F, 1.0F, red, green, blue, alpha, fullBright);
    }

    private void putVertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, float x, float y, float z,
                           float u, float v, float red, float green, float blue, float alpha, int light) {
        buffer.vertex(pose, x, y, z)
                .uv(u, v)
                .color(red, green, blue, alpha)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static final class CloudletEntry {
        EntityNukeTorex.Cloudlet cloudlet;
        float relX;
        float relY;
        float relZ;
        double distanceSq;
    }

}
