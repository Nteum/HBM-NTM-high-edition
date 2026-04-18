package com.hbm.block.logistic;

import com.hbm.blockentity.logistic.TileConveyorInserter;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConveyorInserter extends ConveyorMachineBase{
    public ConveyorInserter(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction userFacing = pContext.getHorizontalDirection();
        return this.defaultBlockState().setValue(MAIN_PORT_SIDE, userFacing.getOpposite()).setValue(SECONDARY_PORT_SIDE, 0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileConveyorInserter(pPos, pState);
    }
}
