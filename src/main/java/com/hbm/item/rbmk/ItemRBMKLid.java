package com.hbm.item.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.reactor.rbmk.RBMKLidType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemRBMKLid extends Item {

    private final RBMKLidType lidType;

    public ItemRBMKLid(Properties properties, RBMKLidType lidType) {
        super(properties);
        this.lidType = lidType;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockRBMKBase)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof RBMKBaseEntity baseEntity)) {
            return InteractionResult.PASS;
        }
        boolean sneaking = context.getPlayer() != null && context.getPlayer().isShiftKeyDown();
        RBMKLidType current = state.getValue(BlockRBMKBase.LID);
        if (sneaking) {
            if (!current.isPresent()) {
                return InteractionResult.CONSUME;
            }
            baseEntity.setLidType(RBMKLidType.NONE);
            BlockRBMKBase.dropLidItem(level, pos, current);
            return InteractionResult.CONSUME;
        }
        if (current == lidType) {
            return InteractionResult.CONSUME;
        }
        if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        baseEntity.setLidType(lidType);
        if (current.isPresent()) {
            BlockRBMKBase.dropLidItem(level, pos, current);
        }
        return InteractionResult.CONSUME;
    }
}
