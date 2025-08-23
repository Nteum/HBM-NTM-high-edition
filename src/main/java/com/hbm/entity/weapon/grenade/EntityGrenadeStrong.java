package com.hbm.entity.weapon.grenade;

import com.hbm.entity.ModEntityType;
import com.hbm.explosion.ExplosionUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeStrong extends ThrownGrenade{
    public EntityGrenadeStrong(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityGrenadeStrong(double pX, double pY, double pZ,Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_STRONG.get(), pX, pY, pZ,pLevel);
    }

    public EntityGrenadeStrong( LivingEntity pShooter, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_STRONG.get(), pShooter,pLevel);
    }

    @Override
    public double getBounceMod() {
        return 0.25D;
    }

    @Override
    public int getDefaultFuseTime() {
        return 100;
    }

    @Override
    public void explode() {
        this.discard();
        ExplosionUtils.explode(this.level(), this.getX() , this.getY(), this.getZ() , 5.0F,true,false,false);
    }
}
