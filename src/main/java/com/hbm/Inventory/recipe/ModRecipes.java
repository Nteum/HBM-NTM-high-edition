package com.hbm.Inventory.recipe;

import com.hbm.HBM;
import com.hbm.HBMKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPE = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, HBM.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HBM.MODID);

    public static final RecipeHolder<RecipePress> PRESS = register("press", RecipePress.Serializer.INSTANCE);
    public static final RecipeHolder<BlastFurnaceRecipe> BLAST = register(HBMKey.BLAST, BlastFurnaceRecipe.Serializer.INSTANCE);
    public static final RecipeHolder<AssemblerRecipe> ASSEMBLER = register(HBMKey.ASSEMBLER, AssemblerRecipe.Serializer.INSTANCE);
    public static final RecipeHolder<ChemplantRecipe> CHEMPLANT = register(HBMKey.CHEMPLANT, ChemplantRecipe.SERIALIZER);
    public static final RecipeHolder<ShredderRecipe> SHREDDER = register(HBMKey.SHREDDER, ShredderRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<PWRFuelPrinterRecipe>> PWR_FUEL_PRINTER =
            SERIALIZER.register("pwr_fuel_printer", () -> new SimpleCraftingRecipeSerializer<>(PWRFuelPrinterRecipe::new));
//    public static final RegistryObject<RecipeSerializer<BlastFurnaceRecipe>> ALLOY_SERIALIZER =
//            SERIALIZER.register(HBMKey.BLAST,()-> BlastFurnaceRecipe.Serializer.INSTANCE);
//    public static final RegistryObject<RecipeSerializer<AssemblerRecipe>> ASSEMBLER_SERIALIZER =
//            SERIALIZER.register(HBMKey.ASSEMBLER,()-> AssemblerRecipe.Serializer.INSTANCE);
//
//    public static final RegistryObject<RecipeType<BlastFurnaceRecipe>> BLAST_RECIPE = RECIPE_TYPE.register(HBMKey.BLAST,()->register(HBMKey.BLAST));
//    public static final RegistryObject<RecipeType<AssemblerRecipe>> ASSEMBLER_RECIPE = RECIPE_TYPE.register(HBMKey.ASSEMBLER,()->register(HBMKey.ASSEMBLER));

    static <T extends Recipe<Container>> RecipeType<T> register(final String pIdentifier) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return pIdentifier;
            }
        };
    }
    static <T extends Recipe<Container>> RecipeHolder<T> register(final String pIdentifier, final RecipeSerializer<T> serializer) {
        RegistryObject<RecipeType<T>> recipeType = RECIPE_TYPE.register(pIdentifier, () -> register(pIdentifier));
        RegistryObject<RecipeSerializer<T>> recipeSerializer = SERIALIZER.register(pIdentifier, () -> serializer);
        return new RecipeHolder<>(recipeType, recipeSerializer);
    }
    public record RecipeHolder<T extends Recipe<?>>(RegistryObject<RecipeType<T>> type, RegistryObject<RecipeSerializer<T>> serializer){}
}
