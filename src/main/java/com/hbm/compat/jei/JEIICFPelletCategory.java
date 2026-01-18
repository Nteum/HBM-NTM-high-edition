package com.hbm.compat.jei;

import com.hbm.HBM;

import com.hbm.item.icf.ItemICFPellet;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JEIICFPelletCategory extends AbstractRecipeCategory<JEIICFPelletCategory.PelletRecipe> {

    public static final RecipeType<PelletRecipe> TYPE = RecipeType.create(HBM.MODID, "icf_pellet", PelletRecipe.class);
    private static final ResourceLocation TEXTURE = HBM.rl("textures/gui/processing/gui_icf_press.png");
    private final IDrawable background;

    public JEIICFPelletCategory(IGuiHelper guiHelper) {
        super(TYPE, Component.translatable("jei.hbm.icf_press"), guiHelper.createDrawableItemLike(ModBlocks.machine_icf_press.get()), 176, 186);
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 176, 186);
    }

    @SuppressWarnings("removal")
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PelletRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 98, 17)
                .addItemStack(new ItemStack(ModItems.icf_pellet_empty.get()))
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.INPUT, 62, 53)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.leftInputs)
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.INPUT, 134, 53)
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.rightInputs)
                .setStandardSlotBackground();
        if (recipe.requiresMuon) {
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 17)
                    .addItemStack(new ItemStack(ModItems.PARTICLE_MUON.get()))
                    .setStandardSlotBackground();
        } else {
            builder.addSlot(RecipeIngredientRole.CATALYST, 8, 17)
                    .addItemStack(new ItemStack(ModItems.PARTICLE_EMPTY.get()))
                    .setStandardSlotBackground();
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 53)
                .addItemStack(recipe.result.copy())
                .setOutputSlotBackground();
    }

    public static List<PelletRecipe> createRecipes() {
        List<PelletRecipe> list = new ArrayList<>();
        addPair(list, ItemICFPellet.FuelType.DEUTERIUM, ItemICFPellet.FuelType.TRITIUM, false);
        addPair(list, ItemICFPellet.FuelType.HELIUM3, ItemICFPellet.FuelType.HELIUM4, false);
        addPair(list, ItemICFPellet.FuelType.LITHIUM, ItemICFPellet.FuelType.OXYGEN, false);
        addPair(list, ItemICFPellet.FuelType.BERYLLIUM, ItemICFPellet.FuelType.CALCIUM, true);
        addPair(list, ItemICFPellet.FuelType.SODIUM, ItemICFPellet.FuelType.CHLORINE, true);
        return list;
    }

    private static void addPair(List<PelletRecipe> list, ItemICFPellet.FuelType one, ItemICFPellet.FuelType two, boolean muon) {
        if (!ItemICFPellet.isSupportedFuel(one) || !ItemICFPellet.isSupportedFuel(two)) {
            return;
        }
        List<ItemStack> left = ItemICFPellet.getFuelItems(one);
        List<ItemStack> right = ItemICFPellet.getFuelItems(two);
        if (left.isEmpty()) {
            left = ItemICFPellet.getFluidDisplays(one);
        }
        if (right.isEmpty()) {
            right = ItemICFPellet.getFluidDisplays(two);
        }
        if (left.isEmpty() || right.isEmpty()) {
            return;
        }
        list.add(new PelletRecipe(left, right, ItemICFPellet.createStack(one, two, muon), muon));
    }

    public record PelletRecipe(List<ItemStack> leftInputs, List<ItemStack> rightInputs, ItemStack result, boolean requiresMuon) {
    }
}
