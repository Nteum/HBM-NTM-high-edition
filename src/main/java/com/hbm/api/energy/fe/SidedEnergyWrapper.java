package com.hbm.api.energy.fe;

import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.capabilities.resolver.SidedCapabilityWrapper;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SidedEnergyWrapper extends SidedCapabilityWrapper<IHBMEnergyStorage> {
    private final Map<Capability<?>, LazyOptional<?>> cachedCapabilities = new IdentityHashMap<>();
    static int FORBID = 0;
    static int INPUT = 1;
    static int OUTPUT = 2;
    static int INOUT = 3;
    public SidedEnergyWrapper(IHBMEnergyStorage energyStorage){
        super(energyStorage);
        cachedCapabilities.put(ForgeCapabilities.ENERGY, LazyOptional.empty());
        Arrays.stream(Direction.values()).forEach(direction -> this.directionConn.put(direction,Boolean.TRUE));
    }
    public static SidedEnergyWrapper io(IHBMEnergyStorage energyStorage, Direction[] directions){
        SidedEnergyWrapper wrapper = new SidedEnergyWrapper(energyStorage);
        Arrays.stream(directions).forEach(direction -> wrapper.directionConn.put(direction,true));
        return wrapper;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return List.of(ForgeCapabilities.ENERGY);
    }

    protected void invalidate(@Nullable LazyOptional<?> cachedCapability) {
        if (cachedCapability != null && cachedCapability.isPresent()) {
            cachedCapability.invalidate();
        }
    }
}
