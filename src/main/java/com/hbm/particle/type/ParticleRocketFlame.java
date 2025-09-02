package com.hbm.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleRocketFlame extends TextureSheetParticle {
    float[] posAdjust = new float[10];
    public ParticleRocketFlame(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet sprite) {
        super(pLevel, pX, pY, pZ);
//        this.gravity = -0.2f;
        this.friction = 0.91f;
        this.lifetime = 300 + pLevel.random.nextInt(50);
        this.setSpriteFromAge(sprite);
        for (int i = 0; i < 10; i++) {
            posAdjust[i] = pLevel.random.nextFloat() * 3;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {

        Vec3 vec3 = pRenderInfo.getPosition();
        float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf;
        if (this.roll == 0.0F) {
            quaternionf = pRenderInfo.rotation();
        } else {
            quaternionf = new Quaternionf(pRenderInfo.rotation());
            quaternionf.rotateZ(Mth.lerp(pPartialTicks, this.oRoll, this.roll));
        }
        float spread = (float) Math.pow(((float) (age) / (float) lifetime) * 4F, 1.5) + 1F;
        spread *= this.quadSize;

        for (int i = 0; i < 10; i++) {
            float add = random.nextFloat() * 0.3F;
            float dark = 1 - Math.min(((float) (age) / (float) (lifetime * 0.5f)), 1);

            setColor(dark + add, 0.6f * dark + add, add);
            setAlpha((float) Math.pow(1 - Math.min(((float) (age) / (float) (lifetime)), 1), 0.5) * 0.75f);
            int j = 240;

            float scale = (posAdjust[i] * 0.5F + 0.1F + ((float) (age) / (float) lifetime) * 2F) * quadSize;
            float pX = (float) (f + (posAdjust[i] - 1D) * 0.2F * spread);
            float pY = (float) (f1 + (posAdjust[i] - 1D) * 0.5F * spread);
            float pZ = (float) (f2 + (posAdjust[i] - 1D) * 0.2F * spread);

            Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F),
                    new Vector3f(-1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, 1.0F, 0.0F),
                    new Vector3f(1.0F, -1.0F, 0.0F)};

            for(int k = 0; k < 4; ++k) {
                Vector3f vector3f = avector3f[k];
                vector3f.rotate(quaternionf);
                vector3f.mul(scale);
                vector3f.add(pX, pY, pZ);
            }

            float f6 = this.getU0();
            float f7 = this.getU1();
            float f4 = this.getV0();
            float f5 = this.getV1();

            pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
            pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
            pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
            pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ParticleRocketFlame(pLevel, pX, pY, pZ, this.sprites);
        }
    }
}
