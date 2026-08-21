package com.hbm.block.machine;

import com.hbm.blockentity.machine.BERtgFurnace;
import com.hbm.core.block.BlockFurnaceBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineFurnaceRtg extends BlockFurnaceBase {
    public MachineFurnaceRtg(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BERtgFurnace(pPos, pState);
    }
}
