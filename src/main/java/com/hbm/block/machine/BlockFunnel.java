package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.FunnelEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 组合漏斗方块。移植自旧版 MachineFunnel，OBJ 渲染简化为普通模型。
 */
public class BlockFunnel extends BlockMachineBase {
    public BlockFunnel(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FunnelEntityBE(pPos, pState);
    }
}
