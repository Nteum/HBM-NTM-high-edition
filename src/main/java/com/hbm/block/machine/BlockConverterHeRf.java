package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.ConverterHeRfEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * HE ↔ RF 能量转换器。
 * 移植自旧版 BlockConverterHeRf：无 GUI、无流体、无配方、单方块、普通模型。
 * 左边 HBM 自定义能量（LONG_ENERGY）输入，右边输出 Forge Energy（FE）。
 */
public class BlockConverterHeRf extends BlockMachineBase {
    public BlockConverterHeRf(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ConverterHeRfEntityBE(pPos, pState);
    }
}
