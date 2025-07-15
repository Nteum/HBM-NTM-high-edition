package com.hbm.api.energy;

public class ProxyEnergyHandler implements IEnergyHandler{
    private IEnergyContainer energyContainer;
    public ProxyEnergyHandler(IEnergyContainer energyContainer){
        this.energyContainer = energyContainer;
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return this.energyContainer;
    }
    @Override
    public void onContentsChanged() {

    }
}
