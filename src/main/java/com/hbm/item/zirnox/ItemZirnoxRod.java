package com.hbm.item.zirnox;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemZirnoxRod extends Item {
    public static final String TAG_TYPE = "zirnoxType";
    public static final String TAG_LIFE = "life";

    public enum ZirnoxRodType {
        NATURAL_URANIUM_FUEL(250_000, 30),
        URANIUM_FUEL(200_000, 50),
        TH232(20_000, 0, true),
        THORIUM_FUEL(200_000, 40),
        MOX_FUEL(165_000, 75),
        PLUTONIUM_FUEL(175_000, 65),
        U233_FUEL(150_000, 100),
        U235_FUEL(165_000, 85),
        LES_FUEL(150_000, 150),
        LITHIUM(20_000, 0, true),
        ZFB_MOX(50_000, 35);

        public final int maxLife;
        public final int heat;
        public final boolean breeding;

        ZirnoxRodType(int maxLife, int heat) {
            this(maxLife, heat, false);
        }

        ZirnoxRodType(int maxLife, int heat, boolean breeding) {
            this.maxLife = maxLife;
            this.heat = heat;
            this.breeding = breeding;
        }

        public static ZirnoxRodType fromIndex(int index) {
            ZirnoxRodType[] values = values();
            if (index < 0 || index >= values.length) {
                return NATURAL_URANIUM_FUEL;
            }
            return values[index];
        }
    }

    public ItemZirnoxRod(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static ZirnoxRodType getRodType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_TYPE)) {
            return ZirnoxRodType.NATURAL_URANIUM_FUEL;
        }
        return ZirnoxRodType.fromIndex(tag.getInt(TAG_TYPE));
    }

    public static int getRodTypeIndex(ItemStack stack) {
        return getRodType(stack).ordinal();
    }

    public static void setRodType(ItemStack stack, ZirnoxRodType type) {
        stack.getOrCreateTag().putInt(TAG_TYPE, type.ordinal());
    }

    public static ItemStack createStack(Item item, ZirnoxRodType type) {
        ItemStack stack = new ItemStack(item);
        setRodType(stack, type);
        return stack;
    }

    public static int getLifeTime(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getInt(TAG_LIFE);
    }

    public static void setLifeTime(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt(TAG_LIFE, value);
    }

    public static void incrementLifeTime(ItemStack stack) {
        setLifeTime(stack, getLifeTime(stack) + 1);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getLifeTime(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ZirnoxRodType type = getRodType(stack);
        if (type.maxLife <= 0) {
            return 0;
        }
        float ratio = Mth.clamp(getLifeTime(stack) / (float) type.maxLife, 0F, 1F);
        return Math.round(13 - ratio * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        ZirnoxRodType type = getRodType(stack);
        if (type.maxLife <= 0) {
            return 0x00FF00;
        }
        float ratio = Mth.clamp(getLifeTime(stack) / (float) type.maxLife, 0F, 1F);
        return Mth.hsvToRgb(Math.max(0F, 0.33F - ratio * 0.33F), 1F, 1F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ZirnoxRodType type = getRodType(stack);
        double percent = type.maxLife > 0 ? (getLifeTime(stack) * 100.0 / type.maxLife) : 0D;
        String typeName = type.name().toLowerCase(Locale.ROOT).replace('_', ' ');
        tooltip.add(Component.literal(ChatFormatting.AQUA + "Type: " + ChatFormatting.RESET + typeName));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Depletion: " + ChatFormatting.RESET + String.format(Locale.ROOT, "%.1f%%", percent)));
        if (type.breeding) {
            tooltip.add(Component.literal(ChatFormatting.GOLD + "Breeding rod"));
        } else {
            tooltip.add(Component.literal(ChatFormatting.GOLD + "Heat: " + ChatFormatting.RESET + type.heat));
        }
        tooltip.add(Component.literal(ChatFormatting.GRAY + "Lifetime: " + String.format(Locale.ROOT, "%,d", type.maxLife)));
    }
}
