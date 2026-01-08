package com.hbm.block.machine.pile;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.pile.ChicagoPileBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoSourceBlockEntity.SourceType;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChicagoGraphiteSourceBlock extends ChicagoMachineBlock {

    public ChicagoGraphiteSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChicagoSourceBlockEntity(pos, state);
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityType.CHICAGO_SOURCE.get(), ChicagoPileBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult shield = handleShieldUse(state, level, pos, player, hand);
        if (shield.consumesAction()) {
            return shield;
        }
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChicagoSourceBlockEntity source) {
                    ItemStack stack = source.extractRod();
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
                level.setBlock(pos, copyAxisShield(state, ModBlocks.chicago_graphite_drilled.get().defaultBlockState()), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChicagoSourceBlockEntity source) {
                    ItemStack stack = source.extractRod();
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
