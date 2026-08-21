package com.hbm.core.item;

import com.hbm.core.capability.ItemStackCapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

public abstract class ItemCapabilities extends Item {
    public ItemCapabilities(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ItemStackCapabilityProvider provider = new ItemStackCapabilityProvider();
        addCapabilities(stack, nbt, provider);
        return provider.update(stack, nbt);
    }

    protected abstract void addCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ItemStackCapabilityProvider provider);
}
