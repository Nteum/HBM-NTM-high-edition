//package com.hbm.capabilities.proxy;
//
//import com.hbm.api.annotations.NothingNullByDefault;
//import com.hbm.api.energy.fe.IHBMEnergyStorage;
//import com.hbm.capabilities.holder.IHolder;
//import net.minecraft.core.Direction;
//import org.jetbrains.annotations.Nullable;
//
//@NothingNullByDefault
//public class ProxyEnergyHandler extends ProxyHandler implements IHBMEnergyStorage {
//
//    private final is energyHandler;
//
//    public ProxyEnergyHandler(ISidedEnergyStorage energyHandler, @Nullable Direction side, @Nullable IHolder holder) {
//        super(side, holder);
//        this.energyHandler = energyHandler;
//    }
//
//    @Override
//    public int getEnergyContainerCount() {
//        return energyHandler.getEnergyContainerCount(side);
//    }
//
//    @Override
//    public FloatingLong getEnergy(int container) {
//        return energyHandler.getEnergy(container, side);
//    }
//
//    @Override
//    public void setEnergy(int container, FloatingLong energy) {
//        if (!readOnly) {
//            energyHandler.setEnergy(container, energy, side);
//        }
//    }
//
//    @Override
//    public FloatingLong getMaxEnergy(int container) {
//        return energyHandler.getMaxEnergy(container, side);
//    }
//
//    @Override
//    public FloatingLong getNeededEnergy(int container) {
//        return energyHandler.getNeededEnergy(container, side);
//    }
//
//    @Override
//    public FloatingLong insertEnergy(int container, FloatingLong amount, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? amount : energyHandler.insertEnergy(container, amount, side, action);
//    }
//
//    @Override
//    public FloatingLong extractEnergy(int container, FloatingLong amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FloatingLong.ZERO : energyHandler.extractEnergy(container, amount, side, action);
//    }
//
//    @Override
//    public FloatingLong insertEnergy(FloatingLong amount, Action action) {
//        return readOnly || readOnlyInsert.getAsBoolean() ? amount : energyHandler.insertEnergy(amount, side, action);
//    }
//
//    @Override
//    public FloatingLong extractEnergy(FloatingLong amount, Action action) {
//        return readOnly || readOnlyExtract.getAsBoolean() ? FloatingLong.ZERO : energyHandler.extractEnergy(amount, side, action);
//    }
//}