package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.SirenEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 警报器方块（单方块）。
 * 移植自旧版 MachineSiren（简化：红石触发固定声音，无磁带选择）。
 */
public class BlockSiren extends BlockMachineBase {
    public BlockSiren(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SirenEntity(pPos, pState);
    }
}
