package com.hbm.block.machine.rbmk;

import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.reactor.rbmk.RBMKDoddOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.network.NetworkHooks;
import java.util.List;

/**
 * Minimal RBMK fuel channel. Accepts {@link ItemRBMKFuelRod} items and injects
 * heat into the RBMK column directly below.
 */
public class BlockRBMKFuelChannel extends Block implements EntityBlock, ILookOverlay {

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 20.0D, 16.0D);

    public BlockRBMKFuelChannel(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RBMKFuelChannelEntity fuelChannel)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            fuelChannel.tryExtractFuel(player);
            return InteractionResult.CONSUME;
        }

        if (fuelChannel.tryInsertFuel(player, hand)) {
            return InteractionResult.CONSUME;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, fuelChannel, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RBMKFuelChannelEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof RBMKFuelChannelEntity fuelChannel) {
                fuelChannel.serverTick();
            }
        };
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RBMKFuelChannelEntity fuelChannel && fuelChannel.isBurning()) {
            return 15;
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof RBMKFuelChannelEntity fuelChannel) {
            return fuelChannel.comparatorSignal();
        }
        return 0;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RBMKFuelChannelEntity fuelChannel) {
                fuelChannel.prepareForDrop();
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, fuelChannel);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        return RBMKDoddOverlay.describe(level, pos);
    }
}
