package com.hbm.block.machine;

import com.hbm.energy.BaseEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.Nullable;

public class BlockBattery extends BaseEntityBlock {
    public BlockBattery(Properties pProperties, long maxPower) {
        super(pProperties);
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }
}
