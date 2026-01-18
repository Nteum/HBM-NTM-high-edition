package com.hbm.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

public class ParticleDigammaSmoke extends ParticleHBMBase{
    public ParticleDigammaSmoke(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        this.lifetime = 100 + pLevel.random.nextInt(40);

        this.scale(5f);
        this.rCol = 0.5f + pLevel.random.nextFloat() * 0.2f;
        this.gCol = this.bCol = 0;
        /*
         * 1710版本写的是this.noClip = true;似乎是设定是不会碰撞，我暂时用这个属性来代替
         * */
        this.hasPhysics = false;
        this.friction = 0.99f;
    }

    @Override
    public void tick() {
        this.alpha = 1 - (float) age / lifetime;
        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
    // 1710 写的是brightness
    @Override
    protected int getLightColor(float pPartialTick) {
        return 240;
    }
}