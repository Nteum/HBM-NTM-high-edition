package com.hbm.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleFoam extends ParticleHBMBase{
    byte seed = 0;
    private float baseScale = 1.0F;
    private float maxScale = 1.5F;

    // Parameters for the trail effect
    private List<Vec3> trail = new ArrayList<Vec3>();
    private int trailLength = 15;
    private float initialVelocity;
    private float buoyancy = 0.05F;
    private float jitter = 0.15F;
    private float drag = 0.96F;
    private int explosionPhase; // 0=burst up, 1=peak, 2=settle
    public ParticleFoam(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        setLifetime(60 + pLevel.random.nextInt(60));
        gravity = 0.005f + pLevel.random.nextFloat() * 0.015f;
        float angle = (float) (pLevel.random.nextDouble() * Math.PI * 2);
        float strength = (float) (pLevel.random.nextDouble() * 0.5);
        this.xd = Mth.cos(angle) * strength;
        this.zd = Mth.sin(angle) * strength;
        this.yd = 2 + pLevel.random.nextFloat() * 3;
        setScale(0.3f + pLevel.random.nextFloat() * 0.7f);
        explosionPhase = 0; // Start in burst phase
        escapeVanillaFrac();
        seed = (byte) pLevel.random.nextInt(Byte.MAX_VALUE);
    }

    @Override
    public void tick() {
        trail.add(0, new Vec3(x, y, z));
        while (trail.size() > trailLength) {
            trail.remove(trail.size() - 1);
        }

        float phaseRatio = (float) age / lifetime;
        if (phaseRatio < 0.3F) {
            explosionPhase = 0;

            if (phaseRatio < 0.15F) {
                yd += buoyancy * 6.0F;
            } else {
                yd += buoyancy * (1.0F - (phaseRatio / 0.3F)) * 2.0F;
            }

            this.setScale(baseScale + (maxScale - baseScale) * (phaseRatio / 0.3F));
        } else if (phaseRatio < 0.6F) {
            explosionPhase = 1;
            yd *= 0.98F;

            this.setScale(maxScale);
        } else {
            this.gravity = 0.98f;
            explosionPhase = 2;

            this.setScale(maxScale * (1.0F - ((phaseRatio - 0.6F) / 0.4F) * 0.7F));
        }

        alpha = 0.8F * (1.0F - phaseRatio * phaseRatio);

        xd += (random.nextFloat() - 0.5F) * jitter;
        zd += (random.nextFloat() - 0.5F) * jitter;

        // drag like ninja drags the low taper fade
        xd *= drag;
        yd *= drag;
        zd *= drag;

        super.tick();

        // Kill particle if it hits ground
        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        renderFoamBubbles(pBuffer, pRenderInfo, pPartialTicks, x, y, z, getScale(), alpha);

        for (int i = 1; i < trail.size(); i++) {
            Vec3 point = trail.get(i);
            float trailScale = getScale() * (1.0F - (float)i / trailLength);
            float trailAlpha = alpha * (1.0F - (float)i / trailLength) * 0.7F;

            renderFoamBubbles(pBuffer, pRenderInfo, pPartialTicks, point.x, point.y, point.z, trailScale, trailAlpha);
        }
    }

    private void renderFoamBubbles(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks, double x, double y, double z, float scale, float alpha) {
        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = pRenderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(pRenderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
        }

        Random urandom = new Random(seed + (long)(x * 100) + (long)(y * 10) + (long)z);

        int bubbleCount = explosionPhase == 0 ? 8 : (explosionPhase == 1 ? 6 : 4);

        for (int i = 0; i < bubbleCount; i++) {
            float whiteness = 0.9F + urandom.nextFloat() * 0.1F;
            setColor(whiteness, whiteness, whiteness);
            setAlpha(alpha);

            float bubbleScale = scale * (urandom.nextFloat() * 0.5F + 0.75F);
            float offset = explosionPhase == 0 ? 0.4F : (explosionPhase == 1 ? 0.6F : 0.9F);

            float pX = (float) (f + (urandom.nextGaussian()) * offset);
            float pY = (float) (f1 + (urandom.nextGaussian()) * offset * 0.7F);
            float pZ = (float) (f2 + (urandom.nextGaussian()) * offset);

            // 下面勿动
            int brightness = this.getLightColor(pPartialTicks);
            Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int k = 0; k < 4; ++k) {
                Vector3f vector3f = avector3f[k];
                vector3f.rotate(quaternionf);
                vector3f.mul(bubbleScale * quadSize);
                vector3f.add(pX, pY, pZ);
            }

            float f6 = this.getU0();
            float f7 = this.getU1();
            float f4 = this.getV0();
            float f5 = this.getV1();

            pBuffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
            pBuffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(brightness).endVertex();
        }
    }
}