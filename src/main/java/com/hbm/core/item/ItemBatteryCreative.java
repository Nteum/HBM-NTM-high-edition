package com.hbm.core.item;

import com.hbm.core.capability.ItemStackCapabilityProvider;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.energy.InfiniteEnergyHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemBatteryCreative extends ItemCapabilities{
    public ItemBatteryCreative(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected void addCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ItemStackCapabilityProvider provider) {
        provider.addCapability(HBMCaps.LONG_ENERGY, new InfiniteEnergyHandler());
    }
}
