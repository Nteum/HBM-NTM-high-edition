package com.hbm.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** 爆炸产生的烟雾的粒子
 * 主要模仿TNT爆炸的粒子 HugeExplosionParticle
 * */
@OnlyIn(Dist.CLIENT)
public class HBMSmokeParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    protected HBMSmokeParticle(ClientLevel pLevel, double pX, double pY, double pZ,double pQuadSizeMultiplier, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.lifetime = 100 + this.random.nextInt(40);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 2.0F * (1.0F - (float)pQuadSizeMultiplier * 0.5F);
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    protected int getLightColor(float pPartialTick) {
        return 15728880;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void setAlpha(float pAlpha) {
        super.setAlpha(pAlpha);
    }

    @Override
    public Particle scale(float pScale) {
        return super.scale(pScale*0.2F);
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
            return new HBMSmokeParticle(pLevel, pX, pY, pZ, pXSpeed, this.sprites);
        }
    }
}
