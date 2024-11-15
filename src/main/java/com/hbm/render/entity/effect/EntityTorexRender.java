package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.main.HBMxx;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/** 用于渲染爆炸云 */
public class EntityTorexRender extends EntityRenderer<EntityNukeTorex> {
    public static final ResourceLocation TEXTURE_PARTICLE = new ResourceLocation(HBMxx.MODID,"textures/particle/hbm_smoke.png");
    public static final ResourceLocation TEXTURE_FLARE = new ResourceLocation(HBMxx.MODID,"textures/particle/flare.png");
    public EntityTorexRender(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityNukeTorex pEntity) {
        return null;
    }

    @Override
    public void render(EntityNukeTorex pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
//        cloudletWrapper(pEntity,pPoseStack,pBuffer, pPartialTick);
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
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    /** 渲染爆炸云 */
    private void cloudletWrapper(EntityNukeTorex cloud, PoseStack pPoseStack, MultiBufferSource pBuffer, float pPartialTick) {
        //参考ParticleEngine
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(pPoseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        //参考ParticleRenderType.PARTICLE_SHEET_OPAQUE
        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.setShader(GameRenderer::getParticleShader);
        RenderSystem.setShaderTexture(0, TEXTURE_PARTICLE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

        ArrayList<EntityNukeTorex.Cloudlet> cloudlets = new ArrayList<>(cloud.cloudlets);
        cloudlets.sort(cloudSorter);

        for(EntityNukeTorex.Cloudlet cloudlet : cloudlets) {
            //1710传的是interp，我不知道这个参数的意义，暂时传一个pPartialTick
            Vec3 vec = cloudlet.getInterpPos(pPartialTick);
            double x = vec.x - cloud.position().x;
            double y = vec.y - cloud.position().y;
            double z = vec.z - cloud.position().z;
            tessellateCloudlet(buffer, x, y, z, cloudlet, pPartialTick);
//            tessellateCloudlet(buffer, vec.x,vec.y,vec.z, cloudlet, pPartialTick);
        }

//        EntityNukeTorex.Cloudlet cloudlet = cloudlets.get(0);
//        Vec3 vec = cloudlet.getInterpPos(pPartialTick);
//        double x = vec.x - cloud.position().x;
//        double y = vec.y - cloud.position().y;
//        double z = vec.z - cloud.position().z;
//        tessellateCloudlet(buffer, x, y, z, cloudlet, pPartialTick);

        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        posestack.popPose();
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
    /** 用于比较cloudlet */
    private Comparator cloudSorter = new Comparator() {
        @Override
        public int compare(Object arg0, Object arg1) {
            EntityNukeTorex.Cloudlet first = (EntityNukeTorex.Cloudlet) arg0;
            EntityNukeTorex.Cloudlet second = (EntityNukeTorex.Cloudlet) arg1;
            Player player = Minecraft.getInstance().player;
            assert player != null;
            double dist1 = player.distanceToSqr(first.posX, first.posY, first.posZ);
            double dist2 = player.distanceToSqr(second.posX, second.posY, second.posZ);

            return Double.compare(dist2, dist1);
        }
    };
    /** 渲染单个云朵 */
    //这段主要学的是原版的粒子渲染，camera的运用可以参考SingleQuadParticle
    private void tessellateCloudlet(VertexConsumer buffer, double posX, double posY, double posZ, EntityNukeTorex.Cloudlet cloud, float pPartialTicks) {
        //ActiveRenderInfo在高版本对应camera，只是不知道功能是否相符。
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 vec3 = camera.getPosition();
        Quaternionf rotation = camera.rotation();
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f = (float)(posX - vec3.x());
        float f1 = (float)(posY - vec3.y());
        float f2 = (float)(posZ - vec3.z());

        float alpha = cloud.getAlpha();
        float scale = cloud.getScale();

        for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(rotation);
            vector3f.mul(scale);
            vector3f.add(f,f1,f2);
        }

        float brightness = cloud.type == cloud.type.CONDENSATION ? 0.9F : 0.75F * cloud.colorMod;
        Vec3 color = cloud.getInterpColor(pPartialTicks).scale(255);
        color = color.scale(brightness);

        buffer.vertex(avector3f[0].x(),avector3f[0].y(), avector3f[0].z()).uv(1,1).color((int) color.x, (int) color.y, (int) color.z,alpha).uv2((int) brightness).endVertex();
        buffer.vertex(avector3f[1].x(),avector3f[1].y(), avector3f[1].z()).uv(1,0).color((int) color.x, (int) color.y, (int) color.z,alpha).uv2((int) brightness).endVertex();
        buffer.vertex(avector3f[2].x(),avector3f[2].y(), avector3f[2].z()).uv(0,0).color((int) color.x, (int) color.y, (int) color.z,alpha).uv2((int) brightness).endVertex();
        buffer.vertex(avector3f[3].x(),avector3f[3].y(), avector3f[3].z()).uv(0,1).color((int) color.x, (int) color.y, (int) color.z,alpha).uv2((int) brightness).endVertex();
    }
//    private void tessellateCloudlet(BufferBuilder buffer, double posX, double posY, double posZ, EntityNukeTorex.Cloudlet cloud, float pPartialTicks) {
//
//    }

//    private void tessellateFlash(Tessellator tess, double posX, double posY, double posZ, float scale, float alpha, float interp) {
//        float f1 = ActiveRenderInfo.rotationX;
//        float f2 = ActiveRenderInfo.rotationZ;
//        float f3 = ActiveRenderInfo.rotationYZ;
//        float f4 = ActiveRenderInfo.rotationXY;
//        float f5 = ActiveRenderInfo.rotationXZ;
//
//        tess.setColorRGBA_F(1F, 1F, 1F, alpha);
//
//        tess.addVertexWithUV((double) (posX - f1 * scale - f3 * scale), (double) (posY - f5 * scale), (double) (posZ - f2 * scale - f4 * scale), 1, 1);
//        tess.addVertexWithUV((double) (posX - f1 * scale + f3 * scale), (double) (posY + f5 * scale), (double) (posZ - f2 * scale + f4 * scale), 1, 0);
//        tess.addVertexWithUV((double) (posX + f1 * scale + f3 * scale), (double) (posY + f5 * scale), (double) (posZ + f2 * scale + f4 * scale), 0, 0);
//        tess.addVertexWithUV((double) (posX + f1 * scale - f3 * scale), (double) (posY - f5 * scale), (double) (posZ + f2 * scale - f4 * scale), 0, 1);
//    }
}
