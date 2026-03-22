package com.hbm.gui.screen.page.recipe;

import com.hbm.Inventory.recipe.RecipeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class RecipePageVanilla<C extends Container, T extends Recipe<C>> extends RecipePage<T>{
    private RecipeType<?> recipeType;

    public void init(Minecraft minecraft, ResourceLocation bg, int imageWidth, int imageHeight, int width, int height, int xOffset, RecipeType<T> recipeType){
        this.init(minecraft, bg, imageWidth, imageHeight, width, height, xOffset, recipeType, recipe -> true);
    }
    public void init(Minecraft minecraft, ResourceLocation bg, int imageWidth, int imageHeight, int width, int height, int xOffset, RecipeType<T> recipeType, Predicate<T> filter){
        this.recipeType = recipeType;
        List<T> list = minecraft.level.getRecipeManager().getAllRecipesFor(recipeType).stream().toList();
        this.init(minecraft, bg, imageWidth, imageHeight, width, height, xOffset, list, filter);
    }

    @Override
    public List<Component> genTooltip(T recipe, RegistryAccess reg) {
        return RecipeHelper.genTooltip(recipe, reg);
    }

    @Override
    public void renderRecipe(GuiGraphics pGuiGraphics, T recipe, int posX, int posY) {
        ItemStack resultItem = recipe.getResultItem(this.registryAccess);
        pGuiGraphics.renderItem(resultItem.copyWithCount(1), posX, posY);
    }

    @Override
    boolean filterRecipeName(T recipe, String query) {
        var name = recipe.getResultItem(this.registryAccess).getHoverName().getString().toLowerCase();
        return name.contains(query);
    }
}
