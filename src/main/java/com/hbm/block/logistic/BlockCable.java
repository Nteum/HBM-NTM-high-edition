package com.hbm.block.logistic;

import com.hbm.blockentity.machine.CableEntityBEPipeBase;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class BlockCable extends AbstractPipeBlock implements EntityBlock{

    public BlockCable(Properties pProperties) {
        super(pProperties, 0.18F);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CableEntityBEPipeBase(pPos,pState);
    }

    @Override
    protected boolean connBlockEntityCond(LevelAccessor pLevel, BlockState state, BlockPos blockPos, BlockPos neighbourPos) {
        BlockEntity blockEntity = pLevel.getBlockEntity(neighbourPos);
        return state.hasBlockEntity() && (blockEntity.getCapability(ForgeCapabilities.ENERGY).isPresent() || blockEntity.getCapability(HBMCaps.LONG_ENERGY).isPresent());
    }
}
