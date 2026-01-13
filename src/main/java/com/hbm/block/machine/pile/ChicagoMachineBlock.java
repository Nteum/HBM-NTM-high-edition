package com.hbm.block.machine.pile;

import com.hbm.blockentity.machine.pile.ChicagoPileBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class ChicagoMachineBlock extends ChicagoInsertableBlock implements EntityBlock {

    protected ChicagoMachineBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return createBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return createTicker(level, type);
    }

    protected abstract BlockEntity createBlockEntity(BlockPos pos, BlockState state);

    protected abstract <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type);

    protected static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(BlockEntityType<T> provided,
                                                                                    BlockEntityType<? extends ChicagoPileBlockEntity> expected,
                                                                                    ChicagoPileBlockEntity.ServerTicker<? super ChicagoPileBlockEntity> ticker) {
        if (expected == provided) {
            return (Level level, BlockPos pos, BlockState state, T blockEntity) -> ticker.tick(level, pos, state, (ChicagoPileBlockEntity) blockEntity);
        }
        return null;
    }
}
