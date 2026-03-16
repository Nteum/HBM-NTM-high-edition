package com.hbm.api.heat;

public interface IHeatHandler {
    int getHeat();
    int getMaxHeat();
    boolean canExtract();
    boolean canReceive();
    int extractHeat(int maxExtract, boolean simulate);
    int receiveHeat(int maxReceive, boolean simulate);
}
