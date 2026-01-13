package com.hbm.compat.jei;

import com.hbm.HBM;
import com.hbm.Inventory.recipe.AssemblerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.registries.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@JeiPlugin
public class JEIHBMPlugin implements IModPlugin {
    // 这个类必须有一个无参数的构造函数！
    public JEIHBMPlugin() {
    }
    @Override
    public ResourceLocation getPluginUid() {
        // 返回一个唯一的标识符，通常使用你的 modid
        return HBM.rl("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new JEIAssemblerCategory(guiHelper));
        registration.addRecipeCategories(new JEIICFPelletCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        // 获取所有已注册的你的自定义配方
        List<AssemblerRecipe> yourRecipes = recipeManager.getAllRecipesFor(ModRecipes.ASSEMBLER.type().get());

        // 将这些配方添加到JEI，并指定它们属于你的配方类别
        registration.addRecipes(JEIAssemblerCategory.TYPE, yourRecipes);
        registration.addRecipes(JEIICFPelletCategory.TYPE, JEIICFPelletCategory.createRecipes());

        registration.addIngredientInfo(new ItemStack(ModBlocks.machine_icf.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.hbm.machine_icf.info1"),
                Component.translatable("jei.hbm.machine_icf.info2"),
                Component.translatable("jei.hbm.machine_icf.info3"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.machine_icf_controller.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.hbm.machine_icf_controller.info1"),
                Component.translatable("jei.hbm.machine_icf_controller.info2"),
                Component.translatable("jei.hbm.machine_icf_controller.info3"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.machine_icf_press.get()), VanillaTypes.ITEM_STACK,
                Component.translatable("jei.hbm.machine_icf_press.info1"),
                Component.translatable("jei.hbm.machine_icf_press.info2"),
                Component.translatable("jei.hbm.machine_icf_press.info3"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 将你的机器方块物品添加为配方催化剂
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.machine_assembler.get()), JEIAssemblerCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.machine_icf_press.get()), JEIICFPelletCategory.TYPE);
    }
}
