package com.hbm.capabilities;

import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;

public class CapabilityCache implements INBTSerializable<CompoundTag> {
    public static String STOREKEY = "blockCapabilities";
    private final Map<Capability<?>, ICapabilityResolver> capabilityResolvers = new IdentityHashMap<>();
    private final List<ICapabilityResolver> uniqueResolvers = new ArrayList<>();
    private final Set<Capability<?>> alwaysDisabled = new ReferenceOpenHashSet<>();
    private final Map<Capability<?>, List<BooleanSupplier>> semiDisabled = new IdentityHashMap<>();
    public final static Map<Capability<?>, String> CAPABILITY_NAME = new IdentityHashMap<>();
    static {
        CAPABILITY_NAME.put(ForgeCapabilities.ENERGY, HBMKey.ENERGY);
        CAPABILITY_NAME.put(ForgeCapabilities.FLUID_HANDLER, HBMKey.FLUIDS);
    }
    //添加能力resolver
    public void addCapabilityResolver(ICapabilityResolver resolver) {
        if (resolver == null)return;
        uniqueResolvers.add(resolver);
        List<Capability<?>> supportedCapabilities = resolver.getSupportedCapabilities();
        for (Capability<?> supportedCapability : supportedCapabilities) {
            if (capabilityResolvers.put(supportedCapability, resolver) != null) {
                HBM.LOGGER.warn("Multiple capability resolvers registered for {}. Overriding", supportedCapability.getName(), new Exception());
            }
        }
    }
    //标记一些capabilities不可用
    public void addDisabledCapabilities(Capability<?>... capabilities) {
        Collections.addAll(alwaysDisabled, capabilities);
    }

    //添加不可用的capabilities
    public void addDisabledCapabilities(Collection<Capability<?>> capabilities) {
        alwaysDisabled.addAll(capabilities);
    }
    public void addSemiDisabledCapability(Capability<?> capability, BooleanSupplier checker) {
        semiDisabled.computeIfAbsent(capability, cap -> new ArrayList<>()).add(checker);
    }
    public boolean isCapabilityDisabled(Capability<?> capability, @Nullable Direction side) {
        //Treat unregistered capabilities as being disabled to skip and further logic relating to them
        if (!capability.isRegistered() || alwaysDisabled.contains(capability)) {
            return true;
        }
        if (semiDisabled.containsKey(capability)) {
            List<BooleanSupplier> predicates = semiDisabled.get(capability);
            for (BooleanSupplier predicate : predicates) {
                if (predicate.getAsBoolean()) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean canResolve(Capability<?> capability) {
        return capabilityResolvers.containsKey(capability);
    }
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        if (!isCapabilityDisabled(capability, side) && canResolve(capability)) {
            return getCapabilityUnchecked(capability, side);
        }
        return LazyOptional.empty();
    }
    public <T> LazyOptional<T> getCapabilityUnchecked(Capability<T> capability, @Nullable Direction side) {
        ICapabilityResolver capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver == null) {
            return LazyOptional.empty();
        }
        return capabilityResolver.resolve(capability, side);
    }
    public void validate(Capability<?> capability, @Nullable Direction side){
        ICapabilityResolver capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver != null) {
            capabilityResolver.validate(capability, side);
        }
    }
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        ICapabilityResolver capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver != null) {
            capabilityResolver.invalidate(capability, side);
        }
    }
    public void invalidateSides(Capability<?> capability, Direction... sides) {
        ICapabilityResolver capabilityResolver = capabilityResolvers.get(capability);
        if (capabilityResolver != null) {
            for (Direction side : sides) {
                capabilityResolver.invalidate(capability, side);
            }
        }
    }
    public void invalidateAll() {
        uniqueResolvers.forEach(ICapabilityResolver::invalidateAll);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        for (Map.Entry<Capability<?>, ICapabilityResolver> entry : capabilityResolvers.entrySet()) {
            Object cap = entry.getValue().resolve(entry.getKey(), null).orElse(null);
            if (cap instanceof INBTSerializable<?> serializable)
                compoundTag.put(CAPABILITY_NAME.get(entry.getKey()),serializable.serializeNBT());
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (Map.Entry<Capability<?>, ICapabilityResolver> entry : capabilityResolvers.entrySet()) {
            Object cap = entry.getValue().resolve(entry.getKey(), null).orElse(null);
            if (cap instanceof INBTSerializable serializable)
                serializable.deserializeNBT(nbt.get(CAPABILITY_NAME.get(entry.getKey())));
        }
    }
}
