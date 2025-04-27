package com.hbm.capabilities.resolver;

import com.hbm.api.annotations.NothingNullByDefault;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
//ref:mek
@NothingNullByDefault
public class BasicCapabilityResolver implements ICapabilityResolver {

    public final Capability<?> supportedCapability;
    private final NonNullSupplier<?> supplier;
    @Nullable
    private LazyOptional<?> cachedCapability;

    public <T> BasicCapabilityResolver(NonNullSupplier<T> supplier, Capability<? super T> supportedCapabilities) {
        this.supportedCapability = supportedCapabilities;
        this.supplier = supplier;
    }

    @Override
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable Direction side) {
        if (cachedCapability == null || !cachedCapability.isPresent()) {
            //If the capability has not been retrieved yet, or it is not valid then recreate it
            cachedCapability = LazyOptional.of(supplier);
        }
        return cachedCapability.cast();
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return List.of(supportedCapability);
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        //We only have one capability so just invalidate everything
        invalidateAll();
    }

    @Override
    public void validate(Capability<?> capability, @Nullable Direction side) {
        cachedCapability = LazyOptional.of(supplier);
    }

    @Override
    public void invalidateAll() {
        if (cachedCapability != null && cachedCapability.isPresent()) {
            cachedCapability.invalidate();
            cachedCapability = null;
        }
    }
}