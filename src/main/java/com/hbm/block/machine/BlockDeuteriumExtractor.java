package com.hbm.block.machine;

import com.hbm.block.base.BlockMachineBase;
import com.hbm.blockentity.machine.DeuteriumExtractorEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 氘提取器方块（单方块）。
 * 移植自旧版 MachineDeuteriumExtractor：水 → 重水。
 */
public class BlockDeuteriumExtractor extends BlockMachineBase {
    public BlockDeuteriumExtractor(Properties pProperties){
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DeuteriumExtractorEntity(pPos, pState);
    }
}
