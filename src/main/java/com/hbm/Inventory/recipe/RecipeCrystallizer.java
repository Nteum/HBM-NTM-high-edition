package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid_handler.ContainerWithFluid;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class RecipeCrystallizer extends RecipeSerializerBuilder.AutoRecipe {
    public CountableIngredient input;
    public FluidStackIngredient fluid;
    public ItemStack output;
    public int duration;
    public float productivity = 0F;

    public static Function<RecipeType<RecipeCrystallizer>, RecipeSerializer<RecipeCrystallizer>> factory =
            type -> new RecipeSerializerBuilder()
                    .countableIngredient(HBMKey.INPUT)
                    .fluid(HBMKey.FLUIDS)
                    .stack(HBMKey.OUTPUT)
                    .integer(HBMKey.DURATION)
                    .fluid(HBMKey.PRODUCTIVITY)
                    .build(type, RecipeCrystallizer::new);
    public <T extends RecipeSerializerBuilder.AutoRecipe> RecipeCrystallizer(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (CountableIngredient) getValue(HBMKey.INPUT);
        this.fluid = (FluidStackIngredient) getValue(HBMKey.FLUIDS);
        this.output = (ItemStack) getValue(HBMKey.OUTPUT);
        this.duration = (int) getValue(HBMKey.DURATION);
        this.productivity = (float) getValue(HBMKey.PRODUCTIVITY);
    }

    public boolean matches(ContainerWithFluid pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0)) && pContainer.fluidHandler != null && this.fluid.test(pContainer.fluidHandler.getFluidInTank(0));
    }


    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return this.output.copy();
    }
}
