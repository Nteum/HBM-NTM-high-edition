package com.hbm.block.machine;

import com.hbm.blockentity.machine.BoilerEntity;
import com.hbm.blockentity.machine.ElectricBoilerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockElectricBoiler extends BlockLitSingleBlockMachine{
    public BlockElectricBoiler(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElectricBoilerEntity(pPos,pState);
    }
}
