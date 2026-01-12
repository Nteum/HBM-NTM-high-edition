package com.hbm.particle.type;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
/**
 * 主要用于添加一些高版本的操作
 * */
public abstract class ParticleHBMBase extends TextureSheetParticle {
    // 粒子整体放大缩小的倍率，高版本没有这个变量
    // 注意不要直接给scale赋值，而是要用set，否则无法联动修改其他参数
    private float scale = 1.0f;
    public ParticleHBMBase(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        setSprite(sprites.get(pLevel.random));
    }

    @Override
    public ParticleRenderType getRenderType() {
        // mc的HugeExplosionParticle就是PARTICLE_SHEET_LIT，没特殊情况就选它。
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public boolean isCollided(Direction.Axis axis){
        Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(xd,yd,zd), this.getBoundingBox(), this.level, List.of());
        if (axis == Direction.Axis.X) return Math.abs(xd) >= 1.0E-5 && Math.abs(vec3.x) < 1.0E-5;
        else if (axis == Direction.Axis.Y) return Math.abs(yd) >= 1.0E-5 && Math.abs(vec3.y) < 1.0E-5;
        else if (axis == Direction.Axis.Z)return Math.abs(zd) >= 1.0E-5 && Math.abs(vec3.z) < 1.0E-5;
        else return false;
    }

    // 整数赋值的颜色，参考VertexConsumer
    public void setColor(int color){
        this.setColor((int) (FastColor.ARGB32.red(color) * 255.0F), (int) (FastColor.ARGB32.green(color) * 255.0F), (int) (FastColor.ARGB32.blue(color) * 255.0F));
        this.setAlpha((int) (FastColor.ARGB32.alpha(color) * 255.0F));
    }
    @Override
    public void setColor(float red, float green, float blue) {
        super.setColor(Mth.clamp(red, 0, 1), Mth.clamp(green, 0, 1), Mth.clamp(blue, 0, 1));
    }

    @Override
    public Particle scale(float pScale) {
        this.scale *= pScale;
        return super.scale(pScale);
    }
    // 直接设置scale的值，同时修改其他参数
    public Particle setScale(float scale) {
        float oldScale = this.scale;
        this.scale = scale;
        return super.scale(scale / oldScale);
    }

    public float getScale() {
        return scale;
    }

    /*
     * 在系统有自己的设定时绕开原版限制运动的方式，
     * */
    public void escapeVanillaFrac(){
        this.gravity = 0;
        this.friction = 1;
    }

}