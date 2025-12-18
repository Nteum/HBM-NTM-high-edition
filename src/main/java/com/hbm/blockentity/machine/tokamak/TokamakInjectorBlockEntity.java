package com.hbm.blockentity.machine.tokamak;

import com.hbm.blockentity.ModBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TokamakInjectorBlockEntity extends TokamakPeripheralBlockEntity {

    public TokamakInjectorBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityType.TOKAMAK_INJECTOR_ENTITY.get(), pos, state);
    }

    private TokamakInjectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap != ForgeCapabilities.ITEM_HANDLER) {
            return super.getCapability(cap, side);
        }
        ControllerLink link = findController();
        if (link == null) {
            return LazyOptional.empty();
        }
        return link.controller().getCapability(cap, link.directionFromController());
    }
}
