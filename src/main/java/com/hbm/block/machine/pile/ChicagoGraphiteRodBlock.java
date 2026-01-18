package com.hbm.block.machine.pile;


import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import static com.hbm.block.machine.pile.ChicagoPileStateProperties.EXTENDED;

public class ChicagoGraphiteRodBlock extends ChicagoInsertableBlock {

    public ChicagoGraphiteRodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(EXTENDED, Boolean.TRUE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTENDED);
    }

    public boolean isInserted(BlockState state) {
        return state.getValue(EXTENDED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult shield = handleShieldUse(state, level, pos, player, hand);
        if (shield.consumesAction()) {
            return shield;
        }
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            toggleLine(level, pos, state);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void toggleLine(Level level, BlockPos origin, BlockState originState) {
        boolean target = !originState.getValue(EXTENDED);
        setExtended(level, origin, originState, target);
        Direction.Axis axis = originState.getValue(AXIS);
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != axis) {
                continue;
            }
            BlockPos.MutableBlockPos cursor = origin.mutable();
            for (int i = 1; i <= 3; i++) {
                cursor.move(dir);
                BlockState state = level.getBlockState(cursor);
                if (!state.is(this) || state.getValue(AXIS) != axis) {
                    break;
                }
                setExtended(level, cursor, state, target);
            }
        }
    }

    public static void setExtended(Level level, BlockPos pos, BlockState state, boolean extended) {
        level.setBlock(pos, state.setValue(EXTENDED, extended), 3);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean isMoving) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide) {
                popResource(level, pos, new ItemStack(ModItems.PILE_ROD_BORON.get()));
            }
        }
        super.onRemove(state, level, pos, replacement, isMoving);
    }
}
