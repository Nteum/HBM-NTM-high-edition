//package com.hbm.entity.logic;
//
//import com.hbm.entity.ModEntityType;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraftforge.common.Tags;
//import net.minecraftforge.common.world.ForgeChunkManager;
//
///**
// * 核弹爆炸后生成的虚拟实体
// * 用来更新方块消失、核辐射等后续影响
// * */
//public class NukeExplodeEntity extends Entity {
//    //Strength of the blast
//    public int strength;
//    //How many rays are calculated per tick
//    public int speed;
//    public int length;
//    public boolean fallout = true;
//    private int falloutAdd = 0;
//
////    ExplosionNukeRayBatched explosion;
//    public NukeExplodeEntity(EntityType<?> pEntityType, Level pLevel) {
//        super(pEntityType, pLevel);
//    }
////    public NukeExplodeEntity(Level world, int strength, int speed, int length) {
////        super(ModEntityType.NUKE_EXPLODE_ENTITY.get(), world);
////        this.strength = strength;
////        this.speed = speed;
////        this.length = length;
//
////    }
//
//    @Override
//    protected void defineSynchedData() {
//
//    }
//
//    @Override
//    protected void readAdditionalSaveData(CompoundTag pCompound) {
//
//    }
//
//    @Override
//    protected void addAdditionalSaveData(CompoundTag pCompound) {
//
//    }
//
//    @Override
//    public boolean shouldRender(double pX, double pY, double pZ) {
//        return false;
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//    }
//}
