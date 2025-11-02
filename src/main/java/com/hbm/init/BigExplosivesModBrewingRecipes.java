package net.mcreator.nuclearcraft.init;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@JeiPlugin
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModBrewingRecipes.class */
public class BigExplosivesModBrewingRecipes implements IModPlugin {
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("big_explosives:brewing_recipes");
    }

    public void registerRecipes(IRecipeRegistration registration) {
        registration.getVanillaRecipeFactory();
        List<IJeiBrewingRecipe> brewingRecipes = new ArrayList<>();
        new ItemStack(Items.f_42589_);
        new ItemStack(Items.f_42589_);
        new ArrayList();
        new ArrayList();
        registration.addRecipes(RecipeTypes.BREWING, brewingRecipes);
    }
}
