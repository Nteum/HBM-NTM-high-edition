//package com.hbm.entity.logic;
//
//import com.hbm.entity.ModEntityType;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.item.PrimedTnt;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.projectile.Projectile;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//
//import javax.annotation.Nullable;
//
//public class GrenadeGeneticEntity extends EntityGrenadeBouncyBase {
//    public GrenadeGeneticEntity(EntityType<?> pEntityType, Level pLevel) {
//        super((EntityType<? extends Projectile>) pEntityType, pLevel);
//    }
//
//    public static GrenadeGeneticEntity create(Level pLevel, Player pPlayer){
////        return new GrenadeGeneticEntity(ModEntityType.GRENADE_GENETIC_ENTITY.get(), pLevel);
//    }
//
//    @Override
//    public boolean shouldRender(double pX, double pY, double pZ) {
//        return false;
//    }
//
//    //    @Override
////    protected void defineSynchedData() {
////
////    }
////
////    @Override
////    protected void readAdditionalSaveData(CompoundTag pCompound) {
////
////    }
////
////    @Override
////    protected void addAdditionalSaveData(CompoundTag pCompound) {
////
////    }
//
//    @Override
//    public void explode() {
//        if (!this.level().isClientSide){
//            this.discard();
//            this.level().explode(this,this.getX(),this.getY(),this.getZ(),2.0F,Level.ExplosionInteraction.TNT);
//        }
//    }
//
//    @Override
//    protected int getMaxTimer() {
//        return 100;
//    }
//
//    @Override
//    protected double getBounceMod() {
//        return 0.25D;
//    }
//}
