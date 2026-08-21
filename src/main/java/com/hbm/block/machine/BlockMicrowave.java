package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.MicrowaveEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 微波炉方块。
 * 移植自旧版 MachineMicrowave：单方块、有 GUI。
 */
public class BlockMicrowave extends BlockMachineBase {
    public BlockMicrowave(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MicrowaveEntityBE(pPos, pState);
    }
}
