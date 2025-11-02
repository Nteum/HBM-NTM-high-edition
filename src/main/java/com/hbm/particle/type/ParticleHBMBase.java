package com.hbm.particle.type;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class ParticleHBMBase extends TextureSheetParticle {
    protected ParticleHBMBase(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        setSprite(sprites.get(pLevel.random));
    }

    public boolean isCollided(Direction.Axis axis){
        Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(xd,yd,zd), this.getBoundingBox(), this.level, List.of());
        if (axis == Direction.Axis.X) return Math.abs(xd) >= 1.0E-5 && Math.abs(vec3.x) < 1.0E-5;
        else if (axis == Direction.Axis.Y) return Math.abs(yd) >= 1.0E-5 && Math.abs(vec3.y) < 1.0E-5;
        else if (axis == Direction.Axis.Z)return Math.abs(zd) >= 1.0E-5 && Math.abs(vec3.z) < 1.0E-5;
        else return false;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return null;
    }
}
