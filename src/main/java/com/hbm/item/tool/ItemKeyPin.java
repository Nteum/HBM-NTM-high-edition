package com.hbm.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 钥匙销子。移植自旧版 ItemKeyPin：
 * - 记录一个 pin 配置（NBT "pins"）
 * - key_fake 变体不可复制/修改
 */
public class ItemKeyPin extends Item {
    public static final String TAG_PINS = "pins";
    public boolean canTransfer = true;

    public ItemKeyPin(Properties properties){
        super(properties);
    }

    public static int getPins(ItemStack stack){
        if (stack.getTag() == null){
            stack.setTag(new net.minecraft.nbt.CompoundTag());
            return 0;
        }
        return stack.getTag().getInt(TAG_PINS);
    }

    public static void setPins(ItemStack stack, int i){
        if (stack.getTag() == null) stack.setTag(new net.minecraft.nbt.CompoundTag());
        stack.getTag().putInt(TAG_PINS, i);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (getPins(stack) != 0){
            tooltip.add(Component.literal("Pin configuration: " + getPins(stack)));
        } else {
            tooltip.add(Component.literal("Pins not set!"));
        }
        if (!canTransfer){
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("Pins can neither be changed, nor copied."));
        }
    }
}
