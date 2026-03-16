package com.hbm.api.energy;

import com.hbm.HBMKey;
import com.hbm.api.IContentsListener;
import com.hbm.registries.HBMCaps;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.capabilities.resolver.BasicCapabilityResolver;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.utils.ItemDataUtils;

import java.util.function.Consumer;

public class ItemStackEnergyHandler extends ItemCapabilityWrapper.ItemCapability implements IContentsListener {
    protected BasicEnergyContainer energyStorage = null;
    public ItemStackEnergyHandler(long capacity, long inout, boolean isEmpty){
        this(new BasicEnergyContainer(capacity,inout));
        if (!isEmpty)
            this.energyStorage.setEnergy(capacity);
    }
    public ItemStackEnergyHandler(BasicEnergyContainer energyStorage){
        this.energyStorage = energyStorage;
        this.energyStorage.setListener(this);   // 调用本类的onContentsChanged
    }
    public ItemStackEnergyHandler(long capacity, long input, long output, boolean isEmpty){
        this(new BasicEnergyContainer(capacity, input, output));
        if (!isEmpty)
            this.energyStorage.setEnergy(capacity);
    }
    @Override
    protected void gatherCapabilityResolvers(Consumer<ICapabilityResolver> consumer) {
        consumer.accept(new BasicCapabilityResolver(()->new ProxyEnergyHandler(this.energyStorage), HBMCaps.LONG_ENERGY));
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

