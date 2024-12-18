package com.hbm.block.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

public class BlockBattery extends BaseSingleBlockMachine {
    public long maxPower;
    public BlockBattery(Properties pProperties, long maxPower) {
        super(pProperties);
        this.maxPower = maxPower;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    
}
