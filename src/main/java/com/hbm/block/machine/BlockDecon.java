package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.DeconEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 净化装置方块（单方块）。
 * 移植自旧版 BlockDecon：清除范围内生物的辐射。
 */
public class BlockDecon extends BlockMachineBase {
    public BlockDecon(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DeconEntity(pPos, pState);
    }
}
