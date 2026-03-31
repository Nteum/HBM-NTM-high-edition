package com.hbm.item.tool;

import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemTooling extends Item {
    protected ToolType type;
    public ItemTooling(Properties pProperties, ToolType type) {
        super(pProperties);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide){
            ItemStack itemInHand = context.getItemInHand();
            BlockState blockState = level.getBlockState(context.getClickedPos());
            if (blockState.getBlock() instanceof IToolable toolable){
                if (toolable.onScrew(context, this.type)){
                    itemInHand.hurtAndBreak(1, context.getPlayer(), (p_29910_) -> p_29910_.broadcastBreakEvent(context.getHand()));
                }
            }
        }
        return super.useOn(context);
    }
}
