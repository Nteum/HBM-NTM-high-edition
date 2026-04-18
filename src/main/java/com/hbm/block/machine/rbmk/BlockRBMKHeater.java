package com.hbm.block.machine.rbmk;

import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKHeaterEntity;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 简化版 RBMK 加热器：可以被玩家开启/关闭，并在服务端定期向 RBMK 管理器上报热量。
 */
public class BlockRBMKHeater extends Block implements EntityBlock, ILookOverlay {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BlockRBMKHeater(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIT, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        toggle(level, pos, state, !state.getValue(LIT));
        return InteractionResult.CONSUME;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        if (level.isClientSide) {
            return;
        }
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(LIT)) {
            toggle(level, pos, state, powered);
        }
    }

    private static void toggle(Level level, BlockPos pos, BlockState state, boolean active) {
        level.setBlock(pos, state.setValue(LIT, active), Block.UPDATE_ALL);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RBMKHeaterEntity heater) {
            heater.setActive(active);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKHeaterEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof RBMKHeaterEntity heater) {
                heater.serverTick();
            }
        };
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.core.Direction direction) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }
}
