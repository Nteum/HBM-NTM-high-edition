package com.hbm.dim.orbit;

import com.hbm.HBM;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class SpaceSpecialEffects extends DimensionSpecialEffects {
    public SpaceSpecialEffects() {
        super(Float.NaN, false, SkyType.NONE, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 pFogColor, float pBrightness) {
        return new Vec3(0, 0, 0);
    }

    @Override
    public boolean isFoggyAt(int pX, int pY) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        // MC 自身的渲染器
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        // 背景默认的渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false); // 关闭深度写入，确保天空在最底层
        // 渲染背景星空
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableCull();
        float s = 100.0F; // 盒子半长
        for (int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            // 1. 根据当前绘制的面进行旋转
            if (i == 1) poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));  // 下
            if (i == 2) poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F)); // 上
            if (i == 3) poseStack.mulPose(Axis.XP.rotationDegrees(180.0F)); // 后
            if (i == 4) poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));  // 左
            if (i == 5) poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F)); // 右

            Matrix4f matrix4f = poseStack.last().pose();

            // 2. 绑定对应的贴图（注意：每个面一张图）
            RenderSystem.setShaderTexture(0, HBM.rl("textures/env/space/space_bg_0" + (i + 1) + ".png"));

            // 3. 绘制 Quad
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -s, s, -s).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -s, s, s).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, s, s, s).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, s, s, -s).uv(1.0F, 0.0F).endVertex();
            tesselator.end();

            poseStack.popPose();
        }
        RenderSystem.enableCull();

        // 渲染天体
        CelestialBodies.render(tesselator, bufferbuilder, level, ticks, partialTick, poseStack, camera, projectionMatrix);
        // 渲染太阳
//        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/environment/sun.png"));
//        poseStack.pushPose();
//
//        // 3. 这里的关键：不要应用 level 的旋转
//        // 如果你想让太阳在头顶，就让矩阵保持默认或只进行固定偏移
//        // 比如：绕 X 轴旋转 90 度，让太阳从地平线移到天顶
//        poseStack.mulPose(Axis.XP.rotationDegrees(45f));
//
//        Matrix4f matrix4f = poseStack.last().pose();
//        s = 30.0F; // 太阳的大小
//
//        // 4. 绘制平面 (Quad)
//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//        // 在远处绘制一个平面，通常 Z 轴设为 100.0 (天空距离)
//        bufferbuilder.vertex(matrix4f, -s, 100.0F, -s).uv(0.0F, 0.0F).endVertex();
//        bufferbuilder.vertex(matrix4f, s, 100.0F, -s).uv(1.0F, 0.0F).endVertex();
//        bufferbuilder.vertex(matrix4f, s, 100.0F, s).uv(1.0F, 1.0F).endVertex();
//        bufferbuilder.vertex(matrix4f, -s, 100.0F, s).uv(0.0F, 1.0F).endVertex();
//        tesselator.end();
//
//        poseStack.popPose();

        //恢复渲染状态
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        return true; // 返回 true 表示我们已经接管了天空渲染，不需要原版再画一次
    }
}
