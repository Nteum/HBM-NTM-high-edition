package com.hbm.compat.jei;

import com.hbm.HBM;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.registries.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class JEIAssemblerCategory extends AbstractRecipeCategory<AssemblerRecipe> {
    // 定义一个RecipeType，用于标识你的配方类型
    public static final RecipeType<AssemblerRecipe> TYPE = RecipeType.create(HBM.MODID, ModRecipes.ASSEMBLER.type().getId().getPath(), AssemblerRecipe.class);
    private final IGuiHelper guiHelper;

    public JEIAssemblerCategory(IGuiHelper guiHelper) {
        super(TYPE, Component.translatable(HBMLang.CONTAINER_ASSEMBLER.key()), guiHelper.createDrawableItemLike(ModBlocks.machine_assembler.get()),164, 64);
        this.guiHelper = guiHelper;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblerRecipe recipe, IFocusGroup focuses) {
        // 在此方法中定义配方的输入和输出位置
        int[][] inputSlotPositions = JEIUtils.getInputSlotPositions(12);
        for (int i = 0; i < inputSlotPositions.length; i++) {
            builder.addInputSlot(inputSlotPositions[i][0] + 24, inputSlotPositions[i][1])
                    .addIngredients(VanillaTypes.ITEM_STACK, JEIUtils.ingredient2list.apply(recipe.ingredients, i)).setStandardSlotBackground();
        }
        int[][] outputSlotPositions = JEIUtils.getOutputSlotPositions(1);
        builder.addOutputSlot(outputSlotPositions[0][0] + 24, outputSlotPositions[0][1]).addIngredient(VanillaTypes.ITEM_STACK, recipe.result).setOutputSlotBackground();
    }

    @Override
    public void draw(AssemblerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        IDrawableStatic recipeArrow = guiHelper.getRecipeArrow();
        recipeArrow.draw(guiGraphics, 48 + 18+ 24 + (54-recipeArrow.getWidth()) / 2, (getHeight() - recipeArrow.getHeight()) / 2);
    }
}
