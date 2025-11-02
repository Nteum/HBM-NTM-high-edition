package net.mcreator.nuclearcraft.client.particle;

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
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/client/particle/SmokeParticle.class */
public class SmokeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public static SmokeParticleProvider provider(SpriteSet spriteSet) {
        return new SmokeParticleProvider(spriteSet);
    }

    /* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/client/particle/SmokeParticle$SmokeParticleProvider.class */
    public static class SmokeParticleProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public SmokeParticleProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        /* renamed from: createParticle, reason: merged with bridge method [inline-methods] */
        public Particle m_6966_(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new SmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    protected SmokeParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
        super(world, x, y, z);
        this.spriteSet = spriteSet;
        m_107250_(0.2f, 0.2f);
        this.f_107225_ = Math.max(1, 60 + (this.f_107223_.m_188503_(100) - 50));
        this.f_107226_ = 1.0f;
        this.f_107219_ = true;
        this.f_107215_ = vx * 1.0d;
        this.f_107216_ = vy * 1.0d;
        this.f_107217_ = vz * 1.0d;
        m_108335_(spriteSet);
    }

    public ParticleRenderType m_7556_() {
        return ParticleRenderType.f_107431_;
    }

    public void m_5989_() {
        super.m_5989_();
    }
}
