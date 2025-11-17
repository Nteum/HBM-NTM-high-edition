package com.hbm.gui.recipebook;

import com.hbm.registries.ModBlocks;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;

public class HBMRecipeBooks {
    public static void addReferenceEnum(){
        RecipeBookCategories.create("ASSEMBLER",new ItemStack(ModBlocks.ASSEMBLER.get()));

    }
}
