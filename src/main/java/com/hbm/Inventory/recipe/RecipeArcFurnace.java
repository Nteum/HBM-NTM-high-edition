package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

public class RecipeArcFurnace extends RecipeSerializerBuilder.AutoRecipe {
    Ingredient input;
    public ItemStack outputSolid;
    public FluidStack[] outputFluid;
    public static Function<RecipeType<RecipeArcFurnace>, RecipeSerializer<RecipeArcFurnace>> factory =
            type -> new RecipeSerializerBuilder()
                    .ingredient(HBMKey.INPUT)
                    .stack(HBMKey.OUTPUT_ITEM)
                    .list(HBMKey.OUTPUT_FLUID, RecipeSerializerBuilder.TYPE_FLUID_STACK).build(type, RecipeArcFurnace::new);
    public <T extends RecipeSerializerBuilder.AutoRecipe> RecipeArcFurnace(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (Ingredient) getValue(HBMKey.INPUT);
        this.outputSolid = (ItemStack) getValue(HBMKey.OUTPUT_ITEM);
        this.outputFluid = ((List<FluidStack>) getValue(HBMKey.OUTPUT_FLUID)).toArray(FluidStack[]::new);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0));
    }
}
