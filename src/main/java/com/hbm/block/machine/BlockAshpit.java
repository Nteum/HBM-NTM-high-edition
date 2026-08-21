package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.AshpitEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 灰烬收集器方块。
 * 移植自旧版 MachineAshpit：单方块、接收其他机器产生的灰烬并合成为灰烬物品。
 */
public class BlockAshpit extends BlockMachineBase {
    public BlockAshpit(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AshpitEntityBE(pPos, pState);
    }
}
