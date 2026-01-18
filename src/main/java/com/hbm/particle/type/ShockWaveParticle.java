package com.hbm.particle.type;

import com.hbm.particle.ParticleRenderTypes;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class ShockWaveParticle extends TextureSheetParticle {
    private float waveScale = 45F;
    public ShockWaveParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.lifetime = 40;
        this.gravity = 0;
        this.setSprite(sprites.get(pLevel.random));
    }

    public void setup(float scale, int maxAge) {
        this.waveScale = scale;
//        this.scale(scale);
        this.lifetime = maxAge;
    }

    @Override
    public void move(double pX, double pY, double pZ) {
        // 这个粒子不需要移动
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.SHOCK_WAVE;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float dx = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float dy = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float dz = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = pRenderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(pRenderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
        }
        float scale = this.getQuadSize(pPartialTicks);
        int bright = this.getLightColor(pPartialTicks);
        // begin
        bright = 240;
        this.setColor(1, 1, 1);
        this.setAlpha(1 - (((float)this.age + pPartialTicks) / (float)this.lifetime));
        scale = (1 - (float)Math.pow(Math.E, (this.age + pPartialTicks) * -0.0125)) * waveScale;
        // end
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(scale);
            vector3f.add(dx, dy, dz);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
        pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(bright).endVertex();
    }
}