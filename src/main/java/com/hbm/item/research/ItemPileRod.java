package com.hbm.item.research;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Basic fuel/control rod used by the Research & Breeder reactors.
 */
public class ItemPileRod extends Item {

    public static final String TAG_LIFE = "hbmPileLife";

    private final Spec spec;

    public ItemPileRod(Properties properties, Spec spec) {
        super(properties.stacksTo(1));
        this.spec = spec;
    }

    public Spec getSpec() {
        return spec;
    }

    public int getLifetime(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_LIFE) : 0;
    }

    public void addLifetime(ItemStack stack, int amount) {
        if (amount <= 0) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        int next = Math.min(spec.maxLife(), tag.getInt(TAG_LIFE) + amount);
        tag.putInt(TAG_LIFE, next);
    }

    public boolean isSpent(ItemStack stack) {
        return spec.maxLife() > 0 && getLifetime(stack) >= spec.maxLife();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Flux: " + ChatFormatting.WHITE + format(spec.fluxPerTick()) + " n/t"));
        if (spec.heatPerTick() != 0) {
            tooltip.add(Component.literal(ChatFormatting.GOLD + "Heat: " + ChatFormatting.WHITE + format(spec.heatPerTick()) + " kHE/t"));
        }
        if (spec.controlEffect() != 0) {
            tooltip.add(Component.literal(ChatFormatting.AQUA + "Control: " + ChatFormatting.WHITE + format(spec.controlEffect() * 100) + "%"));
        }
        if (spec.breederInput()) {
            tooltip.add(Component.literal(ChatFormatting.GREEN + "Breeder ingredient"));
        }
        if (spec.maxLife() > 0) {
            int life = getLifetime(stack);
            tooltip.add(Component.literal(ChatFormatting.GRAY + "Wear: " + life + " / " + spec.maxLife()));
        }
    }

    private static String format(double value) {
        if (Math.abs(value) < 0.01D) {
            return "0";
        }
        return String.format("%.1f", value);
    }

    public record Spec(double fluxPerTick, double heatPerTick, double controlEffect,
                       int maxLife, int wearPerTick, boolean breederInput) {
        public Spec {
            if (maxLife < 0) {
                maxLife = 0;
            }
            if (wearPerTick <= 0) {
                wearPerTick = 1;
            }
        }

        public static Spec fuel(double flux, double heat, int maxLife, int wear) {
            return new Spec(flux, heat, 0.0D, maxLife, wear, false);
        }

        public static Spec control(double controlEffect, int maxLife, int wear) {
            return new Spec(0.0D, 0.0D, controlEffect, maxLife, wear, false);
        }

        public static Spec source(double flux, double heat, int maxLife, int wear) {
            return new Spec(flux, heat, 0.0D, maxLife, wear, false);
        }

        public static Spec breeder(int maxLife) {
            return new Spec(0.0D, 0.0D, 0.0D, maxLife, 1, true);
        }
    }
}
