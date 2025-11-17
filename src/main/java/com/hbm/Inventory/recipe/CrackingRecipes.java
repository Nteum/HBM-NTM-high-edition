package com.hbm.Inventory.recipe;

import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simple fluid-to-fluid cracking table migrated from the legacy HBM codebase.
 * Each operation consumes 100mB of the input plus 200mB of steam, producing two output fractions and 2mB of spent steam.
 */
public final class CrackingRecipes {

    public static final int INPUT_CONSUMPTION = 100;
    public static final int STEAM_CONSUMPTION = 200;
    public static final int SPENT_STEAM_OUTPUT = 2;

    private static final Map<Fluid, RecipeOutput> RECIPES = new HashMap<>();

    private CrackingRecipes() {
    }

    public static void registerDefaults() {
        RECIPES.clear();
        put(ModFluids.OIL.source().get(), stack(ModFluids.CRACK_OIL.source().get(), 80), stack(ModFluids.PETROLEUM.source().get(), 20));
        put(ModFluids.BITUMEN.source().get(), stack(ModFluids.OIL.source().get(), 80), stack(ModFluids.AROMATICS.source().get(), 20));
        put(ModFluids.SMEAR.source().get(), stack(ModFluids.NAPHTHA.source().get(), 60), stack(ModFluids.PETROLEUM.source().get(), 40));
        put(ModFluids.REFINERY_GAS.source().get(), stack(ModFluids.PETROLEUM.source().get(), 30), stack(ModFluids.UNSATURATEDS.source().get(), 20));
        put(ModFluids.DIESEL.source().get(), stack(ModFluids.KEROSENE.source().get(), 40), stack(ModFluids.PETROLEUM.source().get(), 30));
        put(ModFluids.DIESEL_CRACK.source().get(), stack(ModFluids.KEROSENE.source().get(), 40), stack(ModFluids.PETROLEUM.source().get(), 30));
        put(ModFluids.KEROSENE.source().get(), stack(ModFluids.PETROLEUM.source().get(), 60), FluidStack.EMPTY);
        put(ModFluids.WOOD_OIL.source().get(), stack(ModFluids.HEATING_OIL.source().get(), 40), stack(ModFluids.AROMATICS.source().get(), 10));
        put(ModFluids.NAPHTHA.source().get(), stack(ModFluids.AROMATICS.source().get(), 80), stack(ModFluids.PETROLEUM.source().get(), 20));
        put(ModFluids.HEATING_OIL_VACUUM.source().get(), stack(ModFluids.HEATING_OIL.source().get(), 80), stack(ModFluids.REFORM_GAS.source().get(), 20));
        put(ModFluids.REFORMATE.source().get(), stack(ModFluids.UNSATURATEDS.source().get(), 40), stack(ModFluids.REFORM_GAS.source().get(), 60));
        put(ModFluids.BIOGAS.source().get(), stack(ModFluids.PETROLEUM.source().get(), 20), stack(ModFluids.AROMATICS.source().get(), 20));
    }

    private static FluidStack stack(Fluid fluid, int amount) {
        return new FluidStack(fluid, amount);
    }

    private static void put(Fluid input, FluidStack left, FluidStack right) {
        RECIPES.put(input, new RecipeOutput(left, right));
    }

    public static Optional<RecipeOutput> get(Fluid fluid) {
        return Optional.ofNullable(RECIPES.get(fluid));
    }

    public static Map<Fluid, RecipeOutput> entries() {
        return Collections.unmodifiableMap(RECIPES);
    }

    public record RecipeOutput(FluidStack left, FluidStack right) { }
}
