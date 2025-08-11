package com.hbm.item;

import com.hbm.capabilities.ItemCapabilityWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.ArrayList;
import java.util.List;

public class BlockItemHBM extends BlockItem {
    public BlockItemHBM(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
    protected boolean areCapabilityConfigsLoaded() {
        return true;
    }
    protected void gatherCapabilities(List<ItemCapabilityWrapper.ItemCapability> capabilities, ItemStack stack, CompoundTag nbt) {
    }

    @Override
    public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        if (!areCapabilityConfigsLoaded()) {
            //Only expose the capabilities if the required configs are loaded
            return super.initCapabilities(stack, nbt);
        }
        List<ItemCapabilityWrapper.ItemCapability> capabilities = new ArrayList<>();
        gatherCapabilities(capabilities, stack, nbt);
        if (capabilities.isEmpty()) {
            return super.initCapabilities(stack, nbt);
        }
        return new ItemCapabilityWrapper(stack, capabilities.toArray(ItemCapabilityWrapper.ItemCapability[]::new));
    }
}
