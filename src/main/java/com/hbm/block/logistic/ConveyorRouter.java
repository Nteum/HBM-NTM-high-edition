package com.hbm.block.logistic;

import com.hbm.block.base.BlockContainerBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.blockentity.logistic.TileConveyorRouter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ConveyorRouter extends BlockContainerBase {
    public ConveyorRouter(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileConveyorRouter(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) {
            return null;
        }
        return createTickerHelper(pBlockEntityType, ModBlockEntityType.TILE_CONVEYOR_ROUTER.get(), UpdateableBlockEntity::serverTicker);
    }
}
