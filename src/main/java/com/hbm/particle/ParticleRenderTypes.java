package com.hbm.particle;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

// 一些粒子渲染的内容
/**
 * 感觉没必要完全规避GL11的使用，尽管更高的版本mc似乎在逐渐脱离gl，但那时候渲染机制绝对会继续变化
 * 指望现在写的渲染代码在更高版本一样有效是没意义的。
 * */
@OnlyIn(Dist.CLIENT)
public class ParticleRenderTypes {
    public static final ParticleRenderType SHOCK_WAVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
        }
        public String toString() {
            return "SHOCK_WAVE";
        }
    };
    public static final ParticleRenderType RADIATION_FOG = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            GL11.glDisable(GL11.GL_LIGHTING);
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.defaultBlendFunc();
            pBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
        @Override
        public void end(Tesselator pTesselator) {
            // 由于这个没有RenderSystem做基础，或许需要手动恢复一下？
            GL11.glEnable(GL11.GL_LIGHTING);
            pTesselator.end();
        }
        public String toString() {
            return "RADIATION_FOG";
        }
    };
    public static final ParticleRenderType MUKE_WAVE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder pBuilder, TextureManager pTextureManager) {
            // 保存当前渲染状态
            RenderSystem.backupProjectionMatrix();
            // 设置渲染状态
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.disableCull();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        @Override
        public void end(Tesselator pTesselator) {
            pTesselator.end();
            RenderSystem.restoreProjectionMatrix();
        }
        public String toString() {
            return "MUKE_WAVE";
        }
    };
}