package com.hbm.datagen;

import com.hbm.main.HBMxx;
import com.hbm.item.ModItems;
import com.hbm.recipe.BlastFurnaceRecipe;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class RecipeGen extends RecipeProvider {
    private static Consumer<FinishedRecipe> pwriter;
    public static int recipeCnt;
    public RecipeGen(PackOutput pOutput) {
        super(pOutput);
        recipeCnt = 0;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        pwriter = pWriter;
        BlastFurnaceRecipe.addDefaultRecipe(pWriter);
        addShapelessRecipe(ModItems.nugget_zirconium.get(),9,ModItems.ingot_zirconium.get(),ModItems.ingot_zirconium.get(),1);
        addShapelessRecipe(ModItems.ingot_zirconium.get(),1,ModItems.ingot_zirconium.get(),ModItems.nugget_zirconium.get(),9);
    }
    //添加有序配方（默认加入MISC组，并使用获得物品来解锁，通过输入物品来区分）
    private static void addShapedRecipe(ItemLike output, int outnum, ItemLike crit, String pattern, Object... input){
        Object[] aobject = input;
        Queue<TwoTuple<Character,ItemLike>> in = new LinkedList<>();
//        StringBuilder suffix = new StringBuilder();
        for (int j = 0; j < input.length; ++j)
        {
            if (aobject[j] instanceof Character){
                in.offer(new TwoTuple<>((Character)aobject[j]));
            }else if (aobject[j] instanceof ItemLike){
                assert in.peek() != null;
                in.peek().b = (ItemLike) aobject[j];
//                suffix.append("_").append(itemName((ItemLike) aobject[j]));
            }
        }
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, outnum);
        builder = builder.pattern(pattern.substring(0,3)).pattern(pattern.substring(3,6)).pattern(pattern.substring(6));
        while (!in.isEmpty()){
            TwoTuple<Character,ItemLike> tuple = in.poll();
            builder = builder.define(tuple.a,tuple.b);
        }
        builder = builder.unlockedBy(RecipeProvider.getHasName(crit), RecipeProvider.has(crit));
        builder.save(pwriter,new ResourceLocation(HBMxx.MODID, itemName(output)+"_"+recipeCnt++));
    }
    //添加默认的无序配方（默认加入MISC组，并使用获得物品来解锁，通过输入物品来区分）
    private static void addShapelessRecipe(ItemLike output,int outnum,ItemLike crit,Object... input){
        Queue<TwoTuple<ItemLike,Integer>> in = new LinkedList<>();
        Object[] aobject = input;
//        StringBuilder suffix = new StringBuilder();
        for (int j = 0; j < input.length; ++j)
        {
            if (aobject[j] instanceof ItemLike){
                in.offer(new TwoTuple<>((ItemLike)aobject[j]));
//                suffix.append("_").append(itemName((ItemLike) aobject[j]));
            }else if (aobject[j] instanceof Integer){
                assert in.peek() != null;
                in.peek().b = (Integer) aobject[j];
            }
        }
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, outnum);
        while (!in.isEmpty()){
            TwoTuple<ItemLike,Integer> tuple = in.poll();
            builder = builder.requires(tuple.a,tuple.b);
        }
        builder = builder.unlockedBy(RecipeProvider.getHasName(crit), RecipeProvider.has(crit));
        builder.save(pwriter,new ResourceLocation(HBMxx.MODID, itemName(output)+"_"+recipeCnt++));
    }
    private static class TwoTuple<A,B>{
        A a;
        B b;
        TwoTuple(A a){
            this.a = a;
        }
        TwoTuple(A a,B b){
            this.a = a;
            this.b = b;
        }
    }
    //获取item的无前缀注册名
    private static String itemName(ItemLike item){
        if (item instanceof Item){
            return ForgeRegistries.ITEMS.getKey(((Item) item).asItem()).getPath();
        }else if (item instanceof Block){
            return ForgeRegistries.BLOCKS.getKey((Block) item).getPath();
        }
        return "";
    }
}
