package com.test;

import com.hbm.HBM;
import com.hbm.recipe.AssemblerRecipe;
import com.hbm.recipe.ModRecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.server.ServerStartingEvent;

import java.util.List;

/** 测试一些配方的生成或加载
 * 测试配方可以选 ServerStartingEvent event 事件，此时大部分server需要的东西已经加载完成了。
 * 获取RecipeManager可以成功读取配方。
 * */
public class RecipeTest {
    public static void countableIngredientLoad(ServerStartingEvent event){
        List<AssemblerRecipe> allRecipesFor = event.getServer().getRecipeManager().getAllRecipesFor(ModRecipeType.ASSEMBLER_RECIPE.get());
        for (AssemblerRecipe assemblerRecipe : allRecipesFor) {
            HBM.LOGGER.info(assemblerRecipe.getId().toString());
        }
    }
}
