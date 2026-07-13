package com.hbm.particle.type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class ParticleGiblets extends ParticleHBMBase{
    private float momentumYaw;
    private float momentumPitch;
    private int gibType;
    private SpriteSet spriteSet;
    public ParticleGiblets(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        RandomSource rand = pLevel.random;
        this.lifetime = 140 + rand.nextInt(20);
        this.gravity = 2F;
        this.gibType = gibType;

        if(gibType == 2) this.gravity *= 2;

        this.momentumYaw = (float) rand.nextGaussian() * 15F;
        this.momentumPitch = (float) rand.nextGaussian() * 15F;

        setSprite(sprites.get(gibType, 3));
        this.spriteSet = sprites;
    }

    public void setGibType(int gibType){
        this.gibType = gibType;
        setSprite(this.spriteSet.get(gibType, 3));
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.onGround) {
            if(gibType == 2) return;
            Particle particle = Minecraft.getInstance().particleEngine.makeParticle(new DustParticleOptions(gibType == 1 ? DustParticleOptions.REDSTONE_PARTICLE_COLOR : Vec3.fromRGB24(MapColor.COLOR_LIGHT_GREEN.col).toVector3f(), 1), x, y, z, 0, 0, 0);
            particle.setLifetime(20 + random.nextInt(20));
            Minecraft.getInstance().particleEngine.add(particle);
        }
    }
}
