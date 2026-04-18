package com.hbm.block.logistic;

import com.hbm.blockentity.logistic.TileConveyorExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class ConveyorExtractor extends ConveyorMachineBase{
    public ConveyorExtractor(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction userFacing = pContext.getHorizontalDirection();
        return this.defaultBlockState().setValue(MAIN_PORT_SIDE, userFacing).setValue(SECONDARY_PORT_SIDE, 0);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileConveyorExtractor(pPos, pState);
    }
}
