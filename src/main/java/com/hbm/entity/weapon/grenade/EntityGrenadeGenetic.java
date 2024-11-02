package com.hbm.entity.weapon.grenade;

import com.hbm.entity.ModEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;

public class EntityGrenadeGenetic extends ThrownGrenade{
    public EntityGrenadeGenetic(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public EntityGrenadeGenetic(double pX, double pY, double pZ,Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_GENETIC.get(), pX, pY, pZ,pLevel);
    }

    public EntityGrenadeGenetic(LivingEntity pShooter, Level pLevel) {
        super(ModEntityType.ENTITY_GRENADE_GENETIC.get(), pShooter,pLevel);
    }

    //回弹时候的回弹系数
    @Override
    public double getBounceMod() {
        return 0.5D;
    }
    @Override
    public int getDefaultFuseTime() {
        return 80;
    }

    @Override
    public void explode() {
        this.discard();
        this.level().explode(this, Math.floor(this.getX())+0.5D , this.getY(0.0625D), Math.floor(this.getZ())+0.5D , 4.0F, Level.ExplosionInteraction.TNT);
    }
}
