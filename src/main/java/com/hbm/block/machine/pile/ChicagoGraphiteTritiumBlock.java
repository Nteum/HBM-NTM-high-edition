package com.hbm.block.machine.pile;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ChicagoGraphiteTritiumBlock extends ChicagoInsertableBlock {

    public ChicagoGraphiteTritiumBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult shield = handleShieldUse(state, level, pos, player, hand);
        if (shield.consumesAction()) {
            return shield;
        }
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(ModItems.CELL_TRITIUM.get()));
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
                popResource(level, pos, new ItemStack(ModItems.CELL_TRITIUM.get()));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
