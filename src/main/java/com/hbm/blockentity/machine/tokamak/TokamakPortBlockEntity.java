package com.hbm.blockentity.machine.tokamak;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TokamakPortBlockEntity extends TokamakPeripheralBlockEntity {

    public TokamakPortBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityType.TOKAMAK_PORT_ENTITY.get(), pos, state);
    }

    private TokamakPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap != ForgeCapabilities.FLUID_HANDLER && cap != HBMCaps.LONG_ENERGY && cap != ForgeCapabilities.ENERGY) {
            return super.getCapability(cap, side);
        }
        ControllerLink link = findController();
        if (link == null) {
            return LazyOptional.empty();
        }
        return link.controller().getCapability(cap, link.directionFromController());
    }
}
