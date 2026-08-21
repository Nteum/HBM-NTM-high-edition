package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.EPressEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 电动锻压机方块。
 * 移植自旧版 MachineEPress：单方块、用电能驱动锻压。
 */
public class BlockEPress extends BlockMachineBase {
    public BlockEPress(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new EPressEntityBE(pPos, pState);
    }
}
