package com.hbm.entity.weapon.grenade;

import com.hbm.entity.ModEntityType;
import com.hbm.explosion.ExplosionChaos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeFire extends ThrownGrenade{
    public EntityGrenadeFire(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityGrenadeFire(double pX, double pY, double pZ, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_FIRE.get(), pX, pY, pZ, pLevel);
    }

    public EntityGrenadeFire(LivingEntity pShooter, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_FIRE.get(), pShooter, pLevel);
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
    public void countDown() {
        int i = getFuse();
        if (i <= 0){
            if (!this.level().isClientSide) {
                explode();
            }else {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F, false);
            }
        }else {
            setFuse(i-1);
        }
    }

    @Override
    public void explode() {
        this.discard();
        int x = (int) Math.floor(this.getX());
        int y = (int) Math.floor(this.getY());
        int z = (int) Math.floor(this.getZ());
        ExplosionChaos.burn(this.level(), x, y, z, 5);
        ExplosionChaos.flameDeath(this.level(), x, y, z, 15);
        ExplosionChaos.frag(this.level(), x, y, z, 100, true, this.getOwner());
    }
}
