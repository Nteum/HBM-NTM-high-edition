package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.blockentity.machine.TileMachineCentrifuge;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Function;

public class RecipeCentrifuge extends RecipeSerializerBuilder.AutoRecipe {
    CountableIngredient input;
    ItemStack[] output;
    public static Function<RecipeType<RecipeCentrifuge>, RecipeSerializer<RecipeCentrifuge>> factory =
            type -> new RecipeSerializerBuilder().countableIngredient(HBMKey.INPUT).list(HBMKey.OUTPUT, RecipeSerializerBuilder.TYPE_STACK).build(type, RecipeCentrifuge::new);

    public <T extends RecipeSerializerBuilder.AutoRecipe> RecipeCentrifuge(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = (CountableIngredient) getValue(HBMKey.INPUT);
        this.output = ((List<ItemStack>) getValue(HBMKey.OUTPUT)).toArray(ItemStack[]::new);
    }

    @Override
    public Object getValue(String name) {
        return super.getValue(name);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0)) && super.matches(pContainer, pLevel);
    }

    @Override
    public ItemStack[] assemble(Container pContainer, Level pLevel) {
        return new ItemStack[]{output[0].copy(), output[1].copy(), output[2].copy(), output[3].copy()};
    }
}
