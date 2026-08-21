package com.hbm.core.item;

import com.hbm.capabilities.ItemCapabilityWrapper;
import com.hbm.core.capability.ItemStackCapabilityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockItemCapacities extends BlockItem {
    public BlockItemCapacities(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ItemStackCapabilityProvider provider = new ItemStackCapabilityProvider();
        addCapabilities(stack, nbt, provider);
        return provider.update(stack, nbt);
    }

    protected abstract void addCapabilities(ItemStack stack, @Nullable CompoundTag nbt, ItemStackCapabilityProvider provider);
}
