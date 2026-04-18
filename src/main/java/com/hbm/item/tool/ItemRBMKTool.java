package com.hbm.item.tool;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.reactor.rbmk.RBMKLinkable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Allows RBMK peripherals and external display panels to link to a specific
 * RBMK column.
 */
public class ItemRBMKTool extends Item {

    private static final String TAG_TARGET = "LinkedColumn";

    public ItemRBMKTool(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState state = level.getBlockState(clickedPos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (!player.isCrouching()) {
            return InteractionResult.PASS;
        }

        if (state.getBlock() instanceof BlockRBMKBase baseBlock) {
            BlockPos corePos = baseBlock.getCore(state, level, clickedPos);
            saveTarget(stack, corePos);
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable(
                        "item.hbm.rbmk_tool.linked",
                        corePos.getX(),
                        corePos.getY(),
                        corePos.getZ()).withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        BlockEntity entity = level.getBlockEntity(clickedPos);
        if (entity instanceof RBMKLinkable linkable) {
            BlockPos target = getTarget(stack);
            if (!level.isClientSide) {
                if (target == null) {
                    player.displayClientMessage(Component.translatable("item.hbm.rbmk_tool.target_missing")
                            .withStyle(ChatFormatting.RED), true);
                } else if (linkable.linkToColumn(target)) {
                    player.displayClientMessage(Component.translatable(
                            "item.hbm.rbmk_tool.set",
                            target.getX(),
                            target.getY(),
                            target.getZ(),
                            linkable.getLinkDisplayName()).withStyle(ChatFormatting.GREEN), true);
                } else {
                    player.displayClientMessage(Component.translatable(
                            "item.hbm.rbmk_tool.invalid",
                            target.getX(),
                            target.getY(),
                            target.getZ()).withStyle(ChatFormatting.RED), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.hbm.rbmk_tool.desc1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.hbm.rbmk_tool.desc2").withStyle(ChatFormatting.GRAY));
        BlockPos target = getTarget(stack);
        if (target != null) {
            tooltip.add(Component.translatable("item.hbm.rbmk_tool.target",
                    target.getX(), target.getY(), target.getZ()).withStyle(ChatFormatting.YELLOW));
        } else {
            tooltip.add(Component.translatable("item.hbm.rbmk_tool.target_missing")
                    .withStyle(ChatFormatting.RED));
        }
    }

    private static void saveTarget(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTag().putLong(TAG_TARGET, pos.asLong());
    }

    @Nullable
    private static BlockPos getTarget(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(TAG_TARGET)) {
            return null;
        }
        return BlockPos.of(stack.getTag().getLong(TAG_TARGET));
    }
}
