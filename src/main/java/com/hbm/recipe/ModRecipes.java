package com.hbm.recipe;

import com.hbm.main.HBMxx;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HBMxx.MODID);
    public static final RegistryObject<RecipeSerializer<BlastFurnaceRecipe>> ALLOY_SERIALIZER =
            SERIALIZER.register(BlastFurnaceRecipe.TYPE,()-> BlastFurnaceRecipe.Serializer.INSTANCE);

//    public static final DeferredRegister<RecipeType> ;

}
