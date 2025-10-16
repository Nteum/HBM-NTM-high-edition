package com.hbm.entity.weapon.missile;

import com.hbm.entity.IRadarDetectableNT;
import com.hbm.entity.ModEntityType;
import com.hbm.entity.projectile.EntityThrowableNT;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;

public class EntityMissileAntiBallistic extends EntityThrowableNT implements IRadarDetectableNT {
    public EntityMissileAntiBallistic(Level pLevel){
        this(ModEntityType.ENTITY_MISSILE_ANTI_BALLISTIC.get(), pLevel);
    }
    public EntityMissileAntiBallistic(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public String getUnlocalizedName() {
        return null;
    }

    @Override
    public int getBlipLevel() {
        return 0;
    }

    @Override
    public boolean canBeSeenBy(Object radar) {
        return false;
    }

    @Override
    public boolean paramsApplicable(RadarScanParams params) {
        return false;
    }

    @Override
    public boolean suppliesRedstone(RadarScanParams params) {
        return false;
    }

    @Override
    protected void defineSynchedData() {

    }
}
