package com.hbm.entity.weapon.grenade;

import com.hbm.entity.ModEntityType;
import com.hbm.entity.effect.EntityBlackHole;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeBlackHole extends ThrownGrenade{
    public EntityGrenadeBlackHole(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public EntityGrenadeBlackHole(double pX, double pY, double pZ, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_BLACK_HOLE.get(), pX, pY, pZ, pLevel);
    }

    public EntityGrenadeBlackHole(LivingEntity pShooter, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_BLACK_HOLE.get(), pShooter, pLevel);
    }

    @Override
    public double getBounceMod() {
        return 0.25D;
    }

    @Override
    public int getDefaultFuseTime() {
        return 80;
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide)
        {
            this.discard();
            this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 1.5F, Level.ExplosionInteraction.TNT);

            EntityBlackHole bl = new EntityBlackHole(this.level(), 1.5F);
            bl.setPos(this.getX(),this.getY(),this.getZ());
            this.level().addFreshEntity(bl);
        }
    }
}
