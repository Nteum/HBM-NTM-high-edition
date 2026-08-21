package com.hbm.core.capability;

import com.hbm.core.blockentity.BECapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemStackCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private ItemStack innerStack;
    private Map<Capability<?>, Pair<?, LazyOptional<?>>> capabilities;

    public ItemStackCapabilityProvider(){
    }
    public ItemStackCapabilityProvider update(ItemStack itemStack, CompoundTag nbt){
        this.innerStack = itemStack;
        if (nbt != null) deserializeNBT(nbt);
        return this;
    }

    private Map<Capability<?>, Pair<?, LazyOptional<?>>> getCapabilities(){
        if (capabilities == null) capabilities = new HashMap<>();
        return capabilities;
    }

    public <T> ItemStackCapabilityProvider addCapability(Capability<T> capability, T handler){
        getCapabilities().put(capability, Pair.of(handler, LazyOptional.of(() -> handler)));
        return this;
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<Capability<?>, Pair<?, LazyOptional<?>>> entry : getCapabilities().entrySet()) {
            Object handler = entry.getValue().getLeft();
            if (handler instanceof INBTSerializable<?> inbtseralizer){
                tag.put(entry.getKey().getName(), inbtseralizer.serializeNBT());
            }
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null || nbt.isEmpty()) return;
        for (Map.Entry<Capability<?>, Pair<?, LazyOptional<?>>> entry : getCapabilities().entrySet()) {
            Object handler = entry.getValue().getLeft();
            String name = entry.getKey().getName();
            if (handler instanceof INBTSerializable inbtseralizer && nbt.contains(name)){
                inbtseralizer.deserializeNBT(nbt.get(name));
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (capabilities.containsKey(cap)){
            Pair<?, LazyOptional<?>> pair = capabilities.get(cap);
            if (pair != null) return pair.getRight().cast();
        }
        return LazyOptional.empty();
    }
}
