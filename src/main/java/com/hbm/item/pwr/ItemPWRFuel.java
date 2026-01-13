package com.hbm.item.pwr;

import com.hbm.reactor.pwr.PWRFuelType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class ItemPWRFuel extends Item {
    public static final String TAG_FUEL_TYPE = "pwrType";

    public enum FuelState {
        FRESH,
        HOT,
        DEPLETED
    }

    private final FuelState state;

    public ItemPWRFuel(Properties properties, FuelState state) {
        super(properties);
        this.state = state;
    }

    public FuelState getState() {
        return state;
    }

    public static int getFuelTypeIndex(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_FUEL_TYPE)) {
            return 0;
        }
        return tag.getInt(TAG_FUEL_TYPE);
    }

    public static PWRFuelType getFuelType(ItemStack stack) {
        return PWRFuelType.fromIndex(getFuelTypeIndex(stack));
    }

    public static void setFuelType(ItemStack stack, PWRFuelType type) {
        stack.getOrCreateTag().putInt(TAG_FUEL_TYPE, type.ordinal());
    }

    public static ItemStack createStack(Item item, PWRFuelType type) {
        ItemStack stack = new ItemStack(item);
        setFuelType(stack, type);
        return stack;
    }

    public static boolean isFreshFuel(ItemStack stack) {
        return stack.getItem() instanceof ItemPWRFuel fuel && fuel.state == FuelState.FRESH;
    }

    public static boolean isHotFuel(ItemStack stack) {
        return stack.getItem() instanceof ItemPWRFuel fuel && fuel.state == FuelState.HOT;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        PWRFuelType type = getFuelType(stack);
        String color = ChatFormatting.GOLD.toString();
        String reset = ChatFormatting.RESET.toString();
        tooltip.add(Component.literal(color + "Heat per flux: " + reset + String.format(Locale.ROOT, "%.1f", type.heatEmission) + " TU"));
        tooltip.add(Component.literal(color + "Reaction function: " + reset + type.getFunctionLabel()));
        tooltip.add(Component.literal(color + "Fuel type: " + reset + type.getDangerLabel()));
    }
}
