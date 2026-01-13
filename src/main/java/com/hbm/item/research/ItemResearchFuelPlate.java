package com.hbm.item.research;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fuel plate used by the Research Reactor. Each plate stores its wear state in
 * NBT so we can display a durability bar and react differently depending on
 * the configured transfer function.
 */
public class ItemResearchFuelPlate extends Item {

    private static final String TAG_LIFE = "hbmPlateLife";

    private final int lifeTime;
    private final FunctionType functionType;
    private final int reactivity;

    public ItemResearchFuelPlate(Properties properties, int lifeTime, FunctionType functionType, int reactivity) {
        super(properties.stacksTo(1));
        this.lifeTime = lifeTime;
        this.functionType = functionType;
        this.reactivity = reactivity;
    }

    public int react(ItemStack stack, int incomingFlux) {
        if (functionType != FunctionType.PASSIVE) {
            addLifetime(stack, incomingFlux);
        }
        return switch (functionType) {
            case LOGARITHM -> (int) (Math.log10(incomingFlux + 1) * 0.5D * reactivity);
            case SQUARE_ROOT -> (int) (Math.sqrt(incomingFlux) * reactivity / 10.0D);
            case NEGATIVE_QUADRATIC -> (int) Math.max((incomingFlux - (incomingFlux * incomingFlux / 10000.0D)) / 100.0D * reactivity, 0);
            case LINEAR -> (int) (incomingFlux / 100.0D * reactivity);
            case PASSIVE -> {
                addLifetime(stack, reactivity);
                yield reactivity;
            }
        };
    }

    public boolean isSpent(ItemStack stack) {
        return getLifetime(stack) >= lifeTime;
    }

    private int getLifetime(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_LIFE) : 0;
    }

    private void addLifetime(ItemStack stack, int delta) {
        if (delta <= 0) {
            return;
        }
        CompoundTag tag = stack.getOrCreateTag();
        int next = Math.min(lifeTime, tag.getInt(TAG_LIFE) + delta);
        tag.putInt(TAG_LIFE, next);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        if (lifeTime <= 0) {
            return 13;
        }
        double pct = 1.0D - (getLifetime(stack) / (double) lifeTime);
        return Math.max(1, (int) Math.round(pct * 13.0D));
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x3AFF42;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "[Research Reactor Plate]"));
        tooltip.add(Component.literal(ChatFormatting.DARK_AQUA + "   " + getFunctionDescription()));
        tooltip.add(Component.literal(ChatFormatting.DARK_AQUA + "   Yield of " + formatNumber(lifeTime) + " events"));
    }

    private String getFunctionDescription() {
        return switch (functionType) {
            case LOGARITHM -> "f(x) = log10(x + 1) * 0.5 * " + reactivity;
            case SQUARE_ROOT -> "f(x) = sqrt(x) * " + reactivity + " / 10";
            case NEGATIVE_QUADRATIC -> "f(x) = [x - (x² / 10000)] / 100 * " + reactivity;
            case LINEAR -> "f(x) = x / 100 * " + reactivity;
            case PASSIVE -> "f(x) = " + reactivity;
        };
    }

    private static String formatNumber(int value) {
        if (value < 1000) {
            return Integer.toString(value);
        }
        return String.format("%.1fk", value / 1000.0);
    }

    public enum FunctionType {
        LOGARITHM,
        SQUARE_ROOT,
        NEGATIVE_QUADRATIC,
        LINEAR,
        PASSIVE
    }
}
