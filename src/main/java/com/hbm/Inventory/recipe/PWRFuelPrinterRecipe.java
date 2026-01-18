package com.hbm.Inventory.recipe;


import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.reactor.pwr.PWRFuelType;
import com.hbm.registries.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class PWRFuelPrinterRecipe extends CustomRecipe {
    private static final Map<RegistryObject<net.minecraft.world.item.Item>, PWRFuelType> FUEL_MAP = new LinkedHashMap<>();

    static {
        FUEL_MAP.put(ModItems.BILLET_URANIUM_FUEL, PWRFuelType.MEU);
        FUEL_MAP.put(ModItems.BILLET_U233, PWRFuelType.HEU233);
        FUEL_MAP.put(ModItems.BILLET_U235, PWRFuelType.HEU235);
        FUEL_MAP.put(ModItems.BILLET_NEPTUNIUM_FUEL, PWRFuelType.MEN);
        FUEL_MAP.put(ModItems.BILLET_NEPTUNIUM, PWRFuelType.HEN237);
        FUEL_MAP.put(ModItems.BILLET_MOX_FUEL, PWRFuelType.MOX);
        FUEL_MAP.put(ModItems.BILLET_PLUTONIUM_FUEL, PWRFuelType.MEP);
        FUEL_MAP.put(ModItems.BILLET_PU239, PWRFuelType.HEP239);
        FUEL_MAP.put(ModItems.BILLET_PU241, PWRFuelType.HEP241);
        FUEL_MAP.put(ModItems.BILLET_AMERICIUM_FUEL, PWRFuelType.MEA);
        FUEL_MAP.put(ModItems.BILLET_AM242, PWRFuelType.HEA242);
        FUEL_MAP.put(ModItems.BILLET_SCHRABIDIUM_FUEL, PWRFuelType.HES326);
        FUEL_MAP.put(ModItems.BILLET_HES, PWRFuelType.HES327);
        FUEL_MAP.put(ModItems.BILLET_ZFB_AM_MIX, PWRFuelType.BFB_AM_MIX);
        FUEL_MAP.put(ModItems.BILLET_ZFB_PU241, PWRFuelType.BFB_PU241);
    }

    public PWRFuelPrinterRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundPrinter = false;
        ItemStack billetStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(ModItems.pwr_printer.get())) {
                if (foundPrinter) {
                    return false;
                }
                foundPrinter = true;
                continue;
            }
            if (getFuelType(stack) != null) {
                if (!billetStack.isEmpty()) {
                    return false;
                }
                billetStack = stack;
                continue;
            }
            return false;
        }

        return foundPrinter && !billetStack.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack billetStack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (getFuelType(stack) != null) {
                billetStack = stack;
                break;
            }
        }

        if (billetStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        PWRFuelType type = getFuelType(billetStack);
        if (type == null) {
            return ItemStack.EMPTY;
        }
        return ItemPWRFuel.createStack(ModItems.pwr_fuel.get(), type);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(ModItems.pwr_printer.get())) {
                remaining.set(i, stack.copy());
            }
        }
        return remaining;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.PWR_FUEL_PRINTER.get();
    }

    private static PWRFuelType getFuelType(ItemStack stack) {
        for (Map.Entry<RegistryObject<net.minecraft.world.item.Item>, PWRFuelType> entry : FUEL_MAP.entrySet()) {
            if (stack.is(entry.getKey().get())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
