package com.hbm.block.machine;

import com.hbm.blockentity.machine.BoilerEntity;
import com.hbm.blockentity.machine.ElectricFurnaceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockElectricFurnace extends BlockLitSingleBlockMachine{
    public BlockElectricFurnace(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElectricFurnaceEntity(pPos,pState);
    }
}
