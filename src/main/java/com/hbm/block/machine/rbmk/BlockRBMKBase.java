package com.hbm.block.machine.rbmk;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.reactor.rbmk.RBMKLidType;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * 最基础的 RBMK 反应堆柱体。当前仅用于测试服务端注册流程，后续会继续扩展盖板、控制棒等逻辑。
 */
public class BlockRBMKBase extends BlockDummyable {

    public static final EnumProperty<RBMKLidType> LID = EnumProperty.create("lid", RBMKLidType.class);

    public BlockRBMKBase(Properties properties) {
        super(properties);
        SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        this.registerDefaultState(this.defaultBlockState().setValue(LID, RBMKLidType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LID);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new RBMKBaseEntity(pPos, pState);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            RBMKLidType lid = state.getValue(LID);
            if (lid.isPresent()) {
                dropLidItem(level, pos, lid);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    public static void dropLidItem(Level level, BlockPos pos, RBMKLidType lid) {
        ItemStack stack = ItemStack.EMPTY;
        if (lid == RBMKLidType.GLASS) {
            stack = ModItems.rbmk_lid_glass.get().getDefaultInstance();
        } else if (lid == RBMKLidType.SOLID) {
            stack = ModItems.rbmk_lid.get().getDefaultInstance();
        }
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, stack);
        }
    }
}
