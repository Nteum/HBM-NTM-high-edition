package com.hbm.explosion;

import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** 用于处理爆炸数据的工具类
 *
 * */
public class ExplosionNukeGeneric {
    //处理爆炸制造的伤害
    public static void dealDamage(Level level, Vec3 pos, double radius) {
        dealDamage(level, pos, radius, 250F);
    }
    public static void dealDamage(Level level, Vec3 pos, double radius, float maxDamage){
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos,pos).expandTowards(radius, radius, radius), Entity::isAlive);
        for (Entity entity : entities) {
            double dist = entity.distanceToSqr(pos);
            if(dist <= radius) {
                Vec3 eyePosition = entity.getEyePosition();
                if (!isExplosionExempt(entity) && isObstructed(level,pos,eyePosition)){
                    double damage = maxDamage * (radius - dist) / radius;
                    //生物受伤，原版是注册了了一个radiation的伤害源，这里暂时以generic代替
                    entity.hurt(level.damageSources().generic(),(float) damage);
                    entity.setSecondsOnFire(5);
                    Vec3 knock = eyePosition.subtract(pos).normalize().scale(0.2D);
                    entity.addDeltaMovement(knock);
                }
            }
        }
    }
    //是否免疫爆炸伤害
    private static boolean isExplosionExempt(Entity entity){
        return entity.ignoreExplosion();
    }
    //连线是否被阻挡
    public static boolean isObstructed(Level level,Vec3 start, Vec3 end){
        return level.clip(new ClipContext(start,end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,null)).getType() == HitResult.Type.MISS;
    }
}
