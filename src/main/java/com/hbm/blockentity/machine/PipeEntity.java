package com.hbm.blockentity.machine;

import com.hbm.api.energy.fe.HBMEnergyStorage;
import com.hbm.api.energy.fe.SidedEnergyWrapper;
import com.hbm.api.providers.IBlockProvider;
import com.hbm.block.states.TransmitterType;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.blockentity.base.TransmitterBlockEntity;
import com.hbm.capabilities.resolver.manager.FluidHandlerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;

public class PipeEntity extends BasePipeBlockEntity {
    public PipeEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.PIPE_ENTITY.get(), pPos, pBlockState);
//        capabilitiesCache.addCapabilityResolver(new SidedEnergyWrapper(new HBMEnergyStorage(10_000)));
    }
}
