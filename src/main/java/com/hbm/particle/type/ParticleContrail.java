package com.hbm.particle.type;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ParticleContrail extends TextureSheetParticle {
    public ParticleContrail(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        setSprite(sprites.get(pLevel.random));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return null;
    }
}
