package com.hbm.particle.type;

import com.hbm.particle.ParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

public class ParticleMukeWave extends ParticleHBMBase{
    public ParticleMukeWave(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprites);
        setLifetime(25);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderTypes.MUKE_WAVE;
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {

    }
}