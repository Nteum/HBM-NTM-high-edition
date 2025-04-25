package com.hbm.api.energy.fe;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.capabilities.resolver.BasicCapabilityResolver;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.lib.ItemDataUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.function.Consumer;

public class ItemStackEnergyHandler extends ItemCapabilityWrapper.ItemCapability implements IContentsListener {
    protected HBMEnergyStorage energyStorage = null;
    public ItemStackEnergyHandler(long capacity, long inout, boolean isEmpty){
        this(new HBMEnergyStorage(capacity,inout));
        if (!isEmpty)
            this.energyStorage.longEnergy = capacity;
    }
    public ItemStackEnergyHandler(HBMEnergyStorage energyStorage){
        energyStorage.setListener(energyStorage);
        this.energyStorage = energyStorage;
        this.energyStorage.listener = this; //本类型标记为
    }
    @Override
    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
//        super.gatherCapabilityResolvers(consumer);
        consumer.accept(new BasicCapabilityResolver(()->energyStorage, ForgeCapabilities.ENERGY));
    }

    @Override
    protected void load() {
        super.load();
        ItemDataUtils.readContainers(getStack(), HBMKey.ENERGY,this.energyStorage);
        saveDamageValue();
    }

    @Override
    public void onContentsChanged() {
        ItemDataUtils.writeContainers(getStack(), HBMKey.ENERGY, this.energyStorage);
        saveDamageValue();
    }
    private void saveDamageValue(){
        if (getStack().isDamageableItem())
            getStack().setDamageValue((int) ((1-energyStorage.getPercent())*getStack().getMaxDamage()));
    }
}

