package com.hbm.core.item;

import com.hbm.HBMLang;
import com.hbm.api.energy.IEnergyHandler;
import com.hbm.core.api.capability.HBMEnergyHandler;
import com.hbm.core.capability.ItemStackCapabilityProvider;
import com.hbm.core.capability.energy.ItemStackEnergyHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBattery extends ItemCapabilities {
    long capacity;
    long input, output;
    boolean isEmpty;
    public static int DEFAULT_DAMAGE = 1000;
    public ItemBattery(long capacity, long inout, Properties pProperties) {
        super(pProperties.defaultDurability(DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.input = output = inout;
    }
    public ItemBattery(long capacity, long input, long output, Properties pProperties) {
        super(pProperties.defaultDurability(DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.input = input;
        this.output = output;
    }
    public ItemBattery(boolean isEmpty, long capacity, long inout, Properties pProperties){
        super(pProperties.durability(DEFAULT_DAMAGE));
        this.capacity = capacity;
        this.input = output = inout;
        this.isEmpty = isEmpty;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable(HBMLang.ENERGY.key(),getEnergy(pStack)));
    }
    public static long getEnergy(ItemStack pStack){
        long result = 0;
        IEnergyHandler energyHandler = pStack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler instanceof HBMEnergyHandler hbmEnergyHandler){
            result = hbmEnergyHandler.getEnergy();
        }
        return result;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return capacity > 0 && getEnergy(pStack) < capacity;
    }

    @Override
    protected void addCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ItemStackCapabilityProvider provider) {
        provider.addCapability(HBMCaps.LONG_ENERGY, new ItemStackEnergyHandler(stack, capacity, input, output, isEmpty));
    }
}
