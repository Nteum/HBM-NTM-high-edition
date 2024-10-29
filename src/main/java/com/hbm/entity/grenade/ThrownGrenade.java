package com.hbm.entity.grenade;

import com.hbm.entity.ModEntityType;
import com.hbm.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
/**
 *
 * */
public class ThrownGrenade extends ThrowableItemProjectile {

    public ThrownGrenade(EntityType<?> pEntityType, Level pLevel) {
        super((EntityType<? extends ThrowableItemProjectile>) pEntityType, pLevel);
    }
    public ThrownGrenade(Level pLevel, LivingEntity pShooter) {super(ModEntityType.GRENADE_GENETIC_ENTITY.get(), pShooter, pLevel);}
    public ThrownGrenade(Level pLevel, double pX, double pY, double pZ) {super(ModEntityType.GRENADE_GENETIC_ENTITY.get(), pX, pY, pZ, pLevel);}

    @Override
    protected @NotNull Item getDefaultItem() {return ModItems.grenade_generic.get();}

    /** 击中物品和实体的效果
     * （参考TNT的爆炸）
     * */
    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            this.level().explode(this, Math.floor(this.getX())+0.5D , this.getY(0.0625D), Math.floor(this.getZ())+0.5D , 4.0F, Level.ExplosionInteraction.TNT);
            this.discard();
        }
    }
}
