package com.hbm.Inventory.recipe;

import com.hbm.core.contents.fluid.HBMFluids;
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
        put(HBMFluids.OIL.source().get(), stack(HBMFluids.CRACKOIL.source().get(), 80), stack(HBMFluids.PETROLEUM.source().get(), 20));
        put(HBMFluids.BITUMEN.source().get(), stack(HBMFluids.OIL.source().get(), 80), stack(HBMFluids.AROMATICS.source().get(), 20));
        put(HBMFluids.SMEAR.source().get(), stack(HBMFluids.NAPHTHA.source().get(), 60), stack(HBMFluids.PETROLEUM.source().get(), 40));
        put(HBMFluids.GAS.source().get(), stack(HBMFluids.PETROLEUM.source().get(), 30), stack(HBMFluids.UNSATURATEDS.source().get(), 20));
        put(HBMFluids.DIESEL.source().get(), stack(HBMFluids.KEROSENE.source().get(), 40), stack(HBMFluids.PETROLEUM.source().get(), 30));
        put(HBMFluids.DIESEL_CRACK.source().get(), stack(HBMFluids.KEROSENE.source().get(), 40), stack(HBMFluids.PETROLEUM.source().get(), 30));
        put(HBMFluids.KEROSENE.source().get(), stack(HBMFluids.PETROLEUM.source().get(), 60), FluidStack.EMPTY);
        put(HBMFluids.WOODOIL.source().get(), stack(HBMFluids.HEATINGOIL.source().get(), 40), stack(HBMFluids.AROMATICS.source().get(), 10));
        put(HBMFluids.NAPHTHA.source().get(), stack(HBMFluids.AROMATICS.source().get(), 80), stack(HBMFluids.PETROLEUM.source().get(), 20));
        put(HBMFluids.HEAVYOIL_VACUUM.source().get(), stack(HBMFluids.HEAVYOIL.source().get(), 80), stack(HBMFluids.GAS.source().get(), 20));
        put(HBMFluids.REFORMATE.source().get(), stack(HBMFluids.UNSATURATEDS.source().get(), 40), stack(HBMFluids.GAS.source().get(), 60));
        put(HBMFluids.BIOGAS.source().get(), stack(HBMFluids.PETROLEUM.source().get(), 20), stack(HBMFluids.AROMATICS.source().get(), 20));
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
