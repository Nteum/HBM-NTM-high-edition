package com.hbm.render.entity.effect;

import com.hbm.HBM;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.render.model.Models;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BlackHoleRender extends EntityRenderer<EntityBlackHole> {
    public static BakedModel model_sphere;
    public static final ResourceLocation BLACK_HOLE_TEXTURE = new ResourceLocation(HBM.MODID,"textures/models/black_hole.png");
    public static final ResourceLocation SWIRL_TEXTURE = new ResourceLocation(HBM.MODID,"textures/entity/bhole.png");
    public static final ResourceLocation DISC_TEXTURE = new ResourceLocation(HBM.MODID,"textures/entity/bhole_d.png");
    public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(HBM.MODID,"textures/models/white_default_texture.png");
    public BlackHoleRender(EntityRendererProvider.Context pContext) {
        super(pContext);
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        model_sphere = modelManager.getModel(Models.BLACK_HOLE);
    }

    @Override
    public void render(EntityBlackHole pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelRenderer = blockRenderer.getModelRenderer();

        pPoseStack.pushPose();
        //渲染黑洞中心球体
        RenderType renderType = RenderType.entityCutout(BLACK_HOLE_TEXTURE);
        float size = pEntity.getSize();  //尺寸暂时如此缩放
        pPoseStack.scale(size,size,size);
        modelRenderer.renderModel(pPoseStack.last(), pBuffer.getBuffer(renderType), null, model_sphere,
                1.0F,1.0F,1.0F,pPackedLight,OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);

        pPoseStack.popPose();

        renderJet(pEntity, pPoseStack, pBuffer);
        renderDisc(pEntity,pPoseStack,pBuffer,pPartialTick);

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityBlackHole pEntity) {
        return null;
    }

    //用于渲染黑洞喷流
    protected void renderJet(EntityBlackHole entity, PoseStack pPoseStack, MultiBufferSource pBuffer){
        RenderSystem.enableBlend();
        pPoseStack.pushPose();

        //根据id设定随机倾斜值
        int id = entity.getId();
        pPoseStack.mulPose(Axis.XP.rotationDegrees(id % 90 - 45));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(id % 360));

        RenderType renderType = RenderType.entityTranslucent(DEFAULT_TEXTURE);
        BufferBuilder buffer = (BufferBuilder)pBuffer.getBuffer(renderType);
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f normal = pPoseStack.last().normal();
        Vec3 jet = new Vec3(0.5,0,0);
        for(int j = -1; j <= 1; j += 2) {
            for(int i = 0; i < 12; i++) {
                buffer.vertex(matrix4f,(float) 0,0.0F,(float) 0).color(1.0F, 1.0F, 1.0F, 0.35F).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal,0f,1f,0f).endVertex();
                buffer.vertex(matrix4f,(float) jet.x,5*j,(float) jet.z).color(1.0F, 1.0F, 1.0F, 0F).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal,0f,1f,0f).endVertex();
                jet = jet.yRot((float)(Math.PI / 6 * j));
                buffer.vertex(matrix4f,(float) jet.x,5*j,(float) jet.z).color(1.0F, 1.0F, 1.0F, 0F).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal,0f,1f,0f).endVertex();
                buffer.vertex(matrix4f,(float) 0,0.0F,(float) 0).color(1.0F, 1.0F, 1.0F, 0.35F).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal,0f,1f,0f).endVertex();
            }
        }

        pPoseStack.popPose();
        RenderSystem.disableBlend();
    }
    //渲染吸积盘
    protected void renderDisc(EntityBlackHole entity, PoseStack pPoseStack, MultiBufferSource pBuffer, float pPartialTick){

        float glow = 0.75F;
        int count = 16;
        int step = 15;
        ColorHolder color;
        Vec3 vec = new Vec3(1, 0, 0);
        pPoseStack.pushPose();
        //根据id设定随机倾斜值（目前不知为什么，轮盘设定倾斜后渲染不出来）
        int id = entity.getId();
        pPoseStack.mulPose(Axis.XP.rotationDegrees(id % 90 - 45));
        pPoseStack.mulPose(Axis.YP.rotationDegrees(id % 360));


        int overlayCoord = OverlayTexture.NO_OVERLAY;
        int light = 240;

        VertexConsumer buffer = pBuffer.getBuffer(RenderType.entityTranslucent(DISC_TEXTURE));

        for (int i = 0; i < step; i++) {
            pPoseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + pPartialTick % 360) * -((float)Math.pow(i + 1, 1.25))));
            Matrix4f pose = pPoseStack.last().pose();
            Matrix3f normal = pPoseStack.last().normal();
            double s = 3 - i * 0.175D;
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < count; k++) {
                    if (j == 0){
                        color = setColorFromIteration(i, 1);
                    }else {
                        color = new ColorHolder(1,1,1,glow);
                    }
                    buffer.vertex(pose, (float) (vec.x*s), (float) 0, (float) (vec.z*s)).color(color.r,color.g,color.b,color.alpha)
                            .uv((float) (0.5+vec.x*0.25), (float) (0.5+vec.z*0.25)).overlayCoords(overlayCoord).uv2(light).normal(normal,0f,1f,0f).endVertex();
                    color = setColorFromIteration(i,0F);
                    buffer.vertex(pose, (float) (vec.x*s*2), (float) 0, (float) (vec.z*s*2)).color(color.r,color.g,color.b,color.alpha)
                            .uv((float) (0.5+vec.x*0.5), (float) (0.5+vec.z*0.5)).overlayCoords(overlayCoord).uv2(light).normal(normal,0f,1f,0f).endVertex();

                    vec = vec.yRot((float)(Math.PI * 2 / count));
                    buffer.vertex(pose, (float) (vec.x*s*2), (float) 0, (float) (vec.z*s*2)).color(color.r,color.g,color.b,color.alpha)
                            .uv((float) (0.5+vec.x*0.5), (float) (0.5+vec.z*0.5)).overlayCoords(overlayCoord).uv2(light).normal(normal,0f,1f,0f).endVertex();
                    if (j == 0){
                        color = setColorFromIteration(i, 1);
                    }else {
                        color = new ColorHolder(1,1,1,glow);
                    }
                    buffer.vertex(pose, (float) (vec.x*s), (float) 0, (float) (vec.z*s)).color(color.r,color.g,color.b,color.alpha)
                            .uv((float) (0.5+vec.x*0.25), (float) (0.5+vec.z*0.25)).overlayCoords(overlayCoord).uv2(light).normal(normal,0f,1f,0f).endVertex();
                }
            }
        }

        pPoseStack.popPose();
    }
    protected ColorHolder setColorFromIteration(int iteration, float alpha){
        if(iteration < 5) {
            float g = 0.125F + iteration * (1F / 10F);
            return new ColorHolder(1.0F, g, 0.0F, alpha);
        }else if(iteration == 5) {
            return new ColorHolder(1.0F, 1.0F, 0.0F, alpha);
        }else {

            int i = iteration - 6;
            float r = 1.0F - i * (1F / 9F);
            float g = 1F - i * (1F / 9F);
            float b = i * (1F / 5F);
            return new ColorHolder(r,g,b,alpha);
        }
    }
    protected record ColorHolder(float r, float g, float b, float alpha){}
}
