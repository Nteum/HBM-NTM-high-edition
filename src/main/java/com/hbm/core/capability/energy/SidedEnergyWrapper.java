package com.hbm.core.capability.energy;

import com.hbm.core.api.capability.HBMEnergyHandler;
import net.minecraft.core.Direction;

public class SidedEnergyWrapper implements HBMEnergyHandler {
    private HBMEnergyHandler energyHandler;
    Direction side;
    // 注意这个IOMode是指某个面的出入方式，而非energyhandler本身的出入方式
    int IOMode;

    public SidedEnergyWrapper(HBMEnergyHandler handler, Direction side, int IOMode){
        this.energyHandler = handler;
        this.side = side;
        this.IOMode = IOMode;
    }

    @Override
    public boolean canReceive() {
        return (IOMode == 1 || IOMode == 3) && HBMEnergyHandler.super.canReceive();
    }

    @Override
    public boolean canExtract() {
        return (IOMode == 2 || IOMode == 3) && HBMEnergyHandler.super.canExtract();
    }

    @Override
    public long getEnergy() {
        return energyHandler.getEnergy();
    }

    @Override
    public long getCapacity() {
        return energyHandler.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        energyHandler.setEnergy(energy);
    }

    @Override
    public void onContentsChanged() {

    }
}
