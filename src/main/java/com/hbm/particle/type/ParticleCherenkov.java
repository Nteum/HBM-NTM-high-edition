package com.hbm.particle.type;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ParticleCherenkov extends TextureSheetParticle {
    private final SpriteSet sprites;

    public ParticleCherenkov(ClientLevel level, double x, double y, double z,
                             double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.sprites = sprites;
        this.setSpriteFromAge(sprites);
        this.alpha = 0.8F + level.random.nextFloat() * 0.15F;
        this.quadSize = 0.12F + level.random.nextFloat() * 0.08F;
        this.lifetime = 40 + level.random.nextInt(20);
        this.gravity = -0.004F; // slightly pull upward
        this.friction = 0.94F;

        float green = 0.55F + level.random.nextFloat() * 0.15F;
        this.setColor(0.25F, green, 1.0F);

        this.xd *= 0.2F;
        this.yd = Math.max(this.yd, 0.01D);
        this.zd *= 0.2F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.setSpriteFromAge(this.sprites);

        if (this.age > this.lifetime - 8) {
            this.alpha = Math.max(0.0F, this.alpha - 0.08F);
        } else if (this.age < 6) {
            this.alpha = Math.min(1.0F, this.alpha + 0.02F);
        }

        this.yd += 0.003D;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9D;
        this.yd *= 0.85D;
        this.zd *= 0.9D;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new ParticleCherenkov(level, x, y, z, xd, yd, zd, this.sprites);
        }
    }
}
