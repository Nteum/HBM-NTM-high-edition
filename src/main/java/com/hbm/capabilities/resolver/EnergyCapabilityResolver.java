package com.hbm.capabilities.resolver;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.ISidedStrictEnergyHandler;
import com.hbm.api.energy.IStrictEnergyHandler;
import com.hbm.capabilities.Capabilities;
import com.hbm.integration.energy.EnergyCompatUtils;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//ref:mek
@NothingNullByDefault
public class EnergyCapabilityResolver implements ICapabilityResolver {

    private final Map<Capability<?>, LazyOptional<?>> cachedCapabilities = new IdentityHashMap<>();
    private final IEnergyContainer handler;

    public EnergyCapabilityResolver(IEnergyContainer handler) {
        this.handler = handler;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        //暂时只支持HBM的能量，以后可能兼容其他能量体系
        return Collections.singletonList(Capabilities.ENERGY);
    }

    @Override
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable Direction side) {
        return getCachedOrResolve(capability, cachedCapabilities, handler);
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        invalidate(cachedCapabilities.get(capability));
    }

    @Override
    public void invalidateAll() {
        for (LazyOptional<?> lazyOptional : new ArrayList<>(cachedCapabilities.values())) {
            invalidate(lazyOptional);
        }
    }

    protected void invalidate(@Nullable LazyOptional<?> cachedCapability) {
        if (cachedCapability != null && cachedCapability.isPresent()) {
            cachedCapability.invalidate();
        }
    }
    //对能力进行resolve的主函数
    public static <T> LazyOptional<T> getCachedOrResolve(Capability<T> capability, Map<Capability<?>, LazyOptional<?>> cachedCapabilities, IEnergyContainer handler) {
        if (cachedCapabilities.containsKey(capability)) {
            //If we already contain a cached object for this lazy optional then get it and use it
            LazyOptional<?> cachedCapability = cachedCapabilities.get(capability);
            if (cachedCapability.isPresent()) {
                //If the capability is still present (valid), just return the cached object
                return cachedCapability.cast();
            }
        }
        //如果没有就新建一个并进入cache
        LazyOptional<T> uncachedCapability = (LazyOptional<T>) LazyOptional.of(() -> handler);
        cachedCapabilities.put(capability, uncachedCapability);
        return uncachedCapability;
    }
}