package com.hbm.item.tool;

import com.hbm.HBMLang;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.ItemStackEnergyHandler;
import com.hbm.registries.HBMCaps;
import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.item.CapabilityItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends CapabilityItem {
    long capacity;
    long inout;
    boolean isEmpty;
    public static int DEFAULT_DAMAGE = 1000;
    public BatteryItem(long capacity, long inout, Properties pProperties) {
        super(pProperties.defaultDurability(DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.inout = inout;
    }
    public BatteryItem(boolean isEmpty, long capacity, long inout, Properties pProperties){
        super(pProperties.durability(DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.inout = inout;
        this.isEmpty = isEmpty;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(HBMLang.ENERGY.key(),getEnergy(pStack)));
    }
    public static long getEnergy(ItemStack pStack){
        long result = 0;
//        IEnergyStorage iEnergyStorage = pStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
//        if (iEnergyStorage instanceof HBMEnergyStorage energyStorage)
//            result = energyStorage.getEnergyStored();
        IEnergyHandler energyHandler = pStack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler != null){
            result = energyHandler.getStored();
        }
        return result;
    }
    @Override
    protected void gatherCapabilities(List<ItemCapabilityWrapper.ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
        super.gatherCapabilities(capabilities, stack, nbt);
        capabilities.add(new ItemStackEnergyHandler(capacity,inout,this.isEmpty));
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return capacity > 0 && getEnergy(pStack) < capacity;
    }
}
