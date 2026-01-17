package com.hbm.Inventory.recipe;

import com.google.common.collect.Maps;
import com.hbm.item.research.ItemBreedingRod;
import com.hbm.item.research.ItemBreedingRod.RodForm;
import com.hbm.item.research.ItemBreedingRod.RodType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Objects;

public final class BreederRecipes {

    private static final Map<Object, BreederRecipe> RECIPES = Maps.newHashMap();

    private BreederRecipes() {}

    static {
        registerDefaults();
    }

    private static void registerDefaults() {
        setRecipe(RodType.LITHIUM, RodType.TRITIUM, 200);
        setRecipe(RodType.CO, RodType.CO60, 100);
        setRecipe(RodType.RA226, RodType.AC227, 300);
        setRecipe(RodType.TH232, RodType.THF, 500);
        setRecipe(RodType.U235, RodType.NP237, 300);
        setRecipe(RodType.NP237, RodType.PU238, 200);
        setRecipe(RodType.PU238, RodType.PU239, 1000);
        setRecipe(RodType.U238, RodType.RGP, 300);
        setRecipe(RodType.URANIUM, RodType.RGP, 200);
        setRecipe(RodType.RGP, RodType.WASTE, 200);

    }

    private static void setRecipe(RodType in, RodType out, int flux) {
        addRodRecipe(RodForm.SINGLE, in, out, flux);
        addRodRecipe(RodForm.DUAL, in, out, flux * 2);
        addRodRecipe(RodForm.QUAD, in, out, flux * 3);
    }

    private static void addRodRecipe(RodForm form, RodType in, RodType out, int flux) {
        RECIPES.put(new RodKey(in, form), BreederRecipe.ofRod(out, form, flux));
    }

    public static BreederRecipe getOutput(ItemStack stack) {
        if (stack.getItem() instanceof ItemBreedingRod rod) {
            RodType type = ItemBreedingRod.getType(stack);
            return RECIPES.get(new RodKey(type, rod.getForm()));
        }
        return RECIPES.get(new ItemKey(stack.getItem()));
    }

    public record RodKey(RodType type, RodForm form) {}

    public record ItemKey(Item item) {}

    public static class BreederRecipe {
        private final RodType rodType;
        private final RodForm form;
        private final ItemStack staticOutput;
        private final int flux;

        private BreederRecipe(RodType rodType, RodForm form, ItemStack staticOutput, int flux) {
            this.rodType = rodType;
            this.form = form;
            this.staticOutput = staticOutput;
            this.flux = flux;
        }

        public static BreederRecipe ofRod(RodType output, RodForm form, int flux) {
            return new BreederRecipe(output, form, ItemStack.EMPTY, flux);
        }

        public static BreederRecipe ofStatic(ItemStack stack, int flux) {
            return new BreederRecipe(null, null, stack.copy(), flux);
        }

        public int flux() {
            return flux;
        }

        public ItemStack createOutput(ItemStack input) {
            if (!staticOutput.isEmpty()) {
                return staticOutput.copy();
            }
            ItemStack stack = ItemBreedingRod.createStack(form, Objects.requireNonNull(rodType));
            if (input.hasTag()) {
                stack.setTag(input.getTag().copy());
            }
            ItemBreedingRod.setType(stack, rodType);
            return stack;
        }

        public ItemStack consumeInput(ItemStack input) {
            ItemStack copy = input.copy();
            copy.shrink(1);
            return copy.isEmpty() ? ItemStack.EMPTY : copy;
        }
    }
}
