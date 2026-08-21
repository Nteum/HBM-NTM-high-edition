package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.KeyForgeEntityBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 钥匙锻造台。移植自旧版 MachineKeyForge：单方块、有 GUI。
 */
public class BlockKeyForge extends BlockMachineBase {
    public BlockKeyForge(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new KeyForgeEntityBE(pPos, pState);
    }
}
