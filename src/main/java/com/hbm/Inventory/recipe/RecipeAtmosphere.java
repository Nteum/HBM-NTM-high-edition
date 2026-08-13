package com.hbm.Inventory.recipe;

import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.base.RecipeSerializerBuilder;
import com.hbm.core.contents.fluid.FluidHelper;
import com.hbm.core.contents.fluid.HbmFluidType;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
import com.hbm.datagen.recipe.ingredient.FluidStackIngredient;
import com.hbm.space.dim.trait.CBT_Atmosphere;
import com.hbm.space.util.AstronomyUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

public class RecipeAtmosphere extends RecipeSerializerBuilder.AutoRecipe {
    public FluidStack[] input;
    public FluidStack output;
    public static Function<RecipeType<RecipeAtmosphere>, RecipeSerializer<RecipeAtmosphere>> factory =
            type -> new RecipeSerializerBuilder().list(HBMKey.INPUT, RecipeSerializerBuilder.TYPE_FLUID_STACK).fluid(HBMKey.OUTPUT).build(type, RecipeAtmosphere::new);
    public <T extends RecipeSerializerBuilder.AutoRecipe> RecipeAtmosphere(ResourceLocation id, RecipeType<T> type, RecipeSerializer<T> serializer) {
        super(id, type, serializer);
    }

    @Override
    protected void onDataLoaded() {
        super.onDataLoaded();
        this.input = ((List<FluidStack>) getValue(HBMKey.INPUT)).toArray(FluidStack[]::new);
        this.output = (FluidStack) getValue(HBMKey.OUTPUT);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return super.matches(pContainer, pLevel);
    }

    public boolean matches(Level level, CBT_Atmosphere atmosphere, int scale){
        // Because atmochem runs infrequently, scale to react all it can immediately
        for (FluidStack recipeFluid : this.input) {
            boolean hasInput = false;
            for (CBT_Atmosphere.FluidEntry entry : atmosphere.fluids) {
                if (entry.fluid == recipeFluid.getFluid()
                        && entry.pressure * AstronomyUtil.MB_PER_ATM >= recipeFluid.getAmount() * scale) {
                    hasInput = true;
                    break;
                }
            }
            if (!hasInput) return false;
        }

        for (FluidStack recipeFluid : this.input) {
            FluidHelper.capture(level, recipeFluid.getFluid(), recipeFluid.getAmount() * scale);
        }
        FluidHelper.release(level, output.getFluid(), output.getAmount() * scale);
        return true;
    }

    @Override
    public <T> T assemble(Container pContainer, Level pLevel) {
        return super.assemble(pContainer, pLevel);
    }
}
