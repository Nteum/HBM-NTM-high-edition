package com.hbm.recipe;

import com.hbm.HBM;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeType {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, HBM.MODID);

    public static final RegistryObject<RecipeType<BlastFurnaceRecipe>> BLAST = RECIPE_TYPE.register("blast_hbm",()->register("blast_hbm"));
    public static final RegistryObject<RecipeType<AssemblerRecipe>> ASSEMBLER = RECIPE_TYPE.register("assembler",()->register("assembler"));

    static <T extends Recipe<?>> RecipeType<T> register(final String pIdentifier) {
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return pIdentifier;
            }
        };
    }
}
