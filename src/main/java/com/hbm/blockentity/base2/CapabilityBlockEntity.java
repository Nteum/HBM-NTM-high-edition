package com.hbm.blockentity.base2;

import com.hbm.capabilities.CapabilitiesContent;
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

    public final CapabilitiesContent capabilitiesContent = new CapabilitiesContent();

    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return capabilitiesContent.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.capabilitiesContent.invalidateAll();
    }
}