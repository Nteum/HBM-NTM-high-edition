package com.hbm.block.machine.pile;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.pile.ChicagoDetectorBlockEntity;
import com.hbm.blockentity.machine.pile.ChicagoPileBlockEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import static com.hbm.block.machine.pile.ChicagoPileStateProperties.TRIGGERED;

public class ChicagoGraphiteDetectorBlock extends ChicagoMachineBlock {

    public ChicagoGraphiteDetectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(TRIGGERED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TRIGGERED);
    }

    public boolean isPowered(BlockState state) {
        return state.getValue(TRIGGERED);
    }

    @Override
    protected BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChicagoDetectorBlockEntity(pos, state);
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityType.CHICAGO_DETECTOR.get(), ChicagoPileBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult shield = handleShieldUse(state, level, pos, player, hand);
        if (shield.consumesAction()) {
            return shield;
        }

        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && held.isEmpty()) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChicagoDetectorBlockEntity detector) {
                    ItemStack stack = detector.extractRod();
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
                level.setBlock(pos, copyAxisShield(state, ModBlocks.chicago_graphite_drilled.get().defaultBlockState()), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (held.is(ModItems.SCREWDRIVER.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof ChicagoDetectorBlockEntity detector) {
                    int delta = player.isShiftKeyDown() ? -1 : 1;
                    int value = detector.adjustThreshold(delta);
                    player.displayClientMessage(detector.thresholdMessage(value), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
