package com.hbm.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public static SmokeParticleProvider provider(SpriteSet spriteSet) {
        return new SmokeParticleProvider(spriteSet);
    }

    public static class SmokeParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmokeParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        /* renamed from: createParticle, reason: merged with bridge method [inline-methods] */
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    protected SmokeParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        setSize(0.2f, 0.2f);
        this.lifetime = Math.max(1, 60 + (this.random.nextInt(100) - 50));
        this.gravity = 1.0f;
        this.hasPhysics = true;
        this.xd = vx * 1.0d;
        this.yd = vy * 1.0d;
        this.zd = vz * 1.0d;
        pickSprite(spriteSet);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
    }
}
