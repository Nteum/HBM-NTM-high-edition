package com.hbm.blockentity.base;

import com.hbm.capabilities.CapabilityCache;
import com.hbm.capabilities.resolver.ICapabilityResolver;
import com.hbm.capabilities.resolver.manager.ICapabilityHandlerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class CapabilityBlockEntity extends UpdateableBlockEntity {

    private final CapabilityCache capabilityCache = new CapabilityCache();

    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected final void addCapabilityResolvers(List<ICapabilityHandlerManager<?>> capabilityHandlerManagers) {
        for (ICapabilityHandlerManager<?> capabilityHandlerManager : capabilityHandlerManagers) {
            //Add all managers that we support in our tile, as capability resolvers
            if (capabilityHandlerManager.canHandle()) {
                addCapabilityResolver(capabilityHandlerManager);
            }
        }
    }

    protected final void addCapabilityResolver(ICapabilityResolver resolver) {
        capabilityCache.addCapabilityResolver(resolver);
    }

    protected final void addDisabledCapabilities(Capability<?>... capabilities) {
        capabilityCache.addDisabledCapabilities(capabilities);
    }

    protected final void addDisabledCapabilities(Collection<Capability<?>> capabilities) {
        capabilityCache.addDisabledCapabilities(capabilities);
    }

    protected final void addSemiDisabledCapability(Capability<?> capability, BooleanSupplier checker) {
        capabilityCache.addSemiDisabledCapability(capability, checker);
    }

//    protected final void addConfigComponent(TileComponentConfig config) {
//        capabilityCache.addConfigComponent(config);
//    }

    protected <T> boolean canEverResolve(Capability<T> capability) {
        return capabilityCache.canResolve(capability);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capabilityCache.isCapabilityDisabled(capability, side)) {
            return LazyOptional.empty();
        } else if (capabilityCache.canResolve(capability)) {
            return capabilityCache.getCapabilityUnchecked(capability, side);
        }
        //Call to the TileEntity's Implementation of getCapability if we could not find a capability ourselves
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        //When the capabilities on our tile get invalidated, make sure to also invalidate all our cached ones
        invalidateCachedCapabilities();
    }

    public void invalidateCachedCapabilities() {
        capabilityCache.invalidateAll();
    }

    public void invalidateCapability(@NotNull Capability<?> capability, @Nullable Direction side) {
        capabilityCache.invalidate(capability, side);
    }

    public void invalidateCapability(@NotNull Capability<?> capability, Direction... sides) {
        capabilityCache.invalidateSides(capability, sides);
    }

    public void invalidateCapabilities(@NotNull Collection<Capability<?>> capabilities, @Nullable Direction side) {
        for (Capability<?> capability : capabilities) {
            invalidateCapability(capability, side);
        }
    }

    public void invalidateCapabilities(@NotNull Collection<Capability<?>> capabilities, Direction... sides) {
        for (Capability<?> capability : capabilities) {
            invalidateCapability(capability, sides);
        }
    }
}