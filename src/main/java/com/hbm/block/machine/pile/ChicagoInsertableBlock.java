package com.hbm.block.machine.pile;

import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import static com.hbm.block.machine.pile.ChicagoPileStateProperties.SHIELDED;

public abstract class ChicagoInsertableBlock extends RotatedPillarBlock {

    protected ChicagoInsertableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(SHIELDED, Boolean.FALSE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            state = this.defaultBlockState();
        }
        return state.setValue(SHIELDED, Boolean.FALSE);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, SHIELDED);
    }

    protected BlockState copyAxisShield(BlockState from, BlockState to) {
        return to.setValue(AXIS, from.getValue(AXIS))
                .setValue(SHIELDED, from.getValue(SHIELDED));
    }

    protected InteractionResult handleShieldUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(ModItems.SHELL.get())) {
            if (state.getValue(SHIELDED)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(SHIELDED, Boolean.TRUE), 3);
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.isEmpty() && player.isShiftKeyDown() && state.getValue(SHIELDED)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(SHIELDED, Boolean.FALSE), 3);
                ItemStack shell = new ItemStack(ModItems.SHELL.get());
                popResource(level, pos, shell);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = handleShieldUse(state, level, pos, player, hand);
        if (result.consumesAction()) {
            return result;
        }
        return super.use(state, level, pos, player, hand, hit);
    }
}
