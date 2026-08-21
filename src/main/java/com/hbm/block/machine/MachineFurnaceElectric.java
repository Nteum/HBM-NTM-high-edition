package com.hbm.block.machine;

import com.hbm.blockentity.machine.TileEntityMachineElectricFurnace;
import com.hbm.core.block.BlockFurnaceBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MachineFurnaceElectric extends BlockFurnaceBase {
    public MachineFurnaceElectric(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileEntityMachineElectricFurnace(pPos, pState);
    }
}
