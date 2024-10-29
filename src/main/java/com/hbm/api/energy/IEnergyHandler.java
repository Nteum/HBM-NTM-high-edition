package com.hbm.api.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

//能量提供者和能量消费者的父类
public interface IEnergyHandler extends IEnergyConnector{
    public long getPower();
    public void setPower(long power);
    public long getMaxPower();
    //以下是debug用的
//    public static final boolean particleDebug = false;
//
//    public default Vec3 getDebugParticlePosMK2() {
//        BlockEntity te = (BlockEntity) this;
//        Vec3 vec = Vec3.createVectorHelper(te.xCoord + 0.5, te.yCoord + 1, te.zCoord + 0.5);
//        return vec;
//    }
//
//    public default void provideInfoForECMK2(CompoundTag data) {
//        data.putLong(CompatEnergyControl.L_ENERGY_HE, this.getPower());
//        data.putLong(CompatEnergyControl.L_CAPACITY_HE, this.getMaxPower());
//    }
}
