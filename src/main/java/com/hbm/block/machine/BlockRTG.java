package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.RTGEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * RTG 发电机方块。
 * 移植自旧版 MachineRTG：单方块、15 个 RTG 燃料棒槽位、产生热量与 HE 能量。
 */
public class BlockRTG extends BlockMachineBase {
    public BlockRTG(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RTGEntityBE(pPos, pState);
    }
}
