package com.hbm.item.blockitem;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronCrateItem extends BlockItem {

    private final int slotCount;

    public IronCrateItem(Block block, Properties properties) {
        this(block, properties, 36);
    }

    protected IronCrateItem(Block block, Properties properties, int slotCount) {
        super(block, properties);
        this.slotCount = slotCount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag beTag = stack.getTagElement("BlockEntityTag");
        if (beTag == null || !beTag.contains("Items")) {
            if (flag.isAdvanced()) {
                tooltip.add(Component.translatable("tooltip.hbm.crate_empty").withStyle(ChatFormatting.GREEN));
            }
            return;
        }
        ItemStackHandler handler = new ItemStackHandler(slotCount);
        handler.deserializeNBT(beTag);
        int shown = 0;
        int filled = 0;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack content = handler.getStackInSlot(slot);
            if (content.isEmpty()) {
                continue;
            }
            filled++;
            if (shown < 10) {
                tooltip.add(Component.literal("  " + content.getHoverName().getString() + " ×" + content.getCount())
                        .withStyle(ChatFormatting.AQUA));
                shown++;
            }
        }
        if (filled == 0) {
            tooltip.add(Component.translatable("tooltip.hbm.crate_empty").withStyle(ChatFormatting.GREEN));
        } else if (filled > shown) {
            tooltip.add(Component.translatable("tooltip.hbm.crate_more", filled - shown)
                    .withStyle(fillColor(filled)));
        } else if (flag.isAdvanced()) {
            tooltip.add(Component.translatable("tooltip.hbm.crate_fill", filled, slotCount)
                    .withStyle(fillColor(filled)));
        }
    }

    private ChatFormatting fillColor(int filledSlots) {
        float percent = slotCount <= 0 ? 0.0F : filledSlots / (float) slotCount;
        if (percent <= 0.33F) {
            return ChatFormatting.GREEN;
        }
        if (percent <= 0.66F) {
            return ChatFormatting.YELLOW;
        }
        return ChatFormatting.RED;
    }
}
