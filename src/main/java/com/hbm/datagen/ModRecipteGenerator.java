package com.hbm.datagen;

import com.hbm.DalisFunMod;
import com.hbm.block.ModBlocks;
import com.hbm.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

// 生成配方
public class ModRecipteGenerator extends FabricRecipeProvider {
    private static RecipeExporter exporter = null;
    public ModRecipteGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        this.exporter = exporter;
        addShapelessRecipe(ModItems.nitra,1, ModBlocks.ore_uranium,ModItems.nitra_small,4);
        addShapelessRecipe(ModItems.nugget_uranium,9,ModItems.ingot_uranium,ModItems.ingot_uranium,1);
        addShapelessRecipe(ModItems.ingot_uranium,1,ModItems.ingot_uranium,ModItems.nugget_uranium,9);
        addShapelessRecipe(ModItems.billet_uranium,3,ModItems.ingot_uranium,ModItems.ingot_uranium,2);
        addShapelessRecipe(ModItems.ingot_uranium,2,ModItems.ingot_uranium,ModItems.billet_uranium,3);

//        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.nugget_uranium,9)
//                .input(ModItems.ingot_uranium)
//                .criterion(FabricRecipeProvider.hasItem(ModItems.ingot_uranium),FabricRecipeProvider.conditionsFromItem(ModItems.ingot_uranium))
//                .offerTo(exporter);
//        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.billet_uranium,3)
//                .input(ModItems.ingot_uranium,2)
//                .criterion(FabricRecipeProvider.hasItem(ModItems.ingot_uranium),FabricRecipeProvider.conditionsFromItem(ModItems.ingot_uranium))
//                .offerTo(exporter);
//        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ingot_uranium,2)
//                .input(ModItems.billet_uranium,3)
//                .criterion(FabricRecipeProvider.hasItem(ModItems.ingot_uranium),FabricRecipeProvider.conditionsFromItem(ModItems.ingot_uranium))
//                .offerTo(exporter,new Identifier(DalisFunMod.MOD_ID, itemName(ModItems.ingot_uranium) +"_from_"+itemName(ModItems.billet_uranium)));
//        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ingot_uranium)
//                .pattern("***")
//                .pattern("***")
//                .pattern("***")
//                .input('*',ModItems.nugget_uranium)
//                .criterion(FabricRecipeProvider.hasItem(ModItems.ingot_uranium),FabricRecipeProvider.conditionsFromItem(ModItems.ingot_uranium))
//                .offerTo(exporter,new Identifier(DalisFunMod.MOD_ID, itemName(ModItems.ingot_uranium)+"_from_"+itemName(ModItems.nugget_uranium)));
        RecipeProvider.offerSmelting(exporter, List.of(ModBlocks.ore_uranium),RecipeCategory.MISC,ModItems.ingot_uranium,0.045F,200,"hbm");
        RecipeProvider.offerBlasting(exporter, List.of(ModBlocks.ore_uranium),RecipeCategory.MISC,ModItems.ingot_uranium,0.045F,200,"hbm");
    }
    //添加有序配方（默认加入MISC组，并使用获得物品来解锁，通过输入物品来区分）
    private static void addShapedRecipe(ItemConvertible output,int outnum,ItemConvertible crit,String pattern, Object... input){
        Object[] aobject = input;
        Queue<TwoTuple<Character,ItemConvertible>> in = new LinkedList<>();
        StringBuilder suffix = new StringBuilder();
        for (int j = 0; j < input.length; ++j)
        {
            if (aobject[j] instanceof Character){
                in.offer(new TwoTuple<>((Character)aobject[j]));
            }else if (aobject[j] instanceof ItemConvertible){
                assert in.peek() != null;
                in.peek().b = (ItemConvertible) aobject[j];
                suffix.append("_").append(itemName((ItemConvertible) aobject[j]));
            }
        }
        ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, output, outnum);
        builder = builder.pattern(pattern.substring(0,3)).pattern(pattern.substring(3,6)).pattern(pattern.substring(6));
        while (!in.isEmpty()){
            TwoTuple<Character,ItemConvertible> tuple = in.poll();
            builder = builder.input(tuple.a,tuple.b);
        }
        builder = builder.criterion(FabricRecipeProvider.hasItem(crit), FabricRecipeProvider.conditionsFromItem(crit));
        builder.offerTo(exporter,new Identifier(DalisFunMod.MOD_ID, itemName(output)+"_from"+suffix));
    }
    //添加默认的无序配方（默认加入MISC组，并使用获得物品来解锁，通过输入物品来区分）
    private static void addShapelessRecipe(ItemConvertible output,int outnum,ItemConvertible crit,Object... input){
        Queue<TwoTuple<ItemConvertible,Integer>> in = new LinkedList<>();
        Object[] aobject = input;
        StringBuilder suffix = new StringBuilder();
        for (int j = 0; j < input.length; ++j)
        {
            if (aobject[j] instanceof ItemConvertible){
                in.offer(new TwoTuple<>((ItemConvertible)aobject[j]));
                suffix.append("_").append(itemName((ItemConvertible) aobject[j]));
            }else if (aobject[j] instanceof Integer){
                assert in.peek() != null;
                in.peek().b = (Integer) aobject[j];
            }
        }
        ShapelessRecipeJsonBuilder builder = ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outnum);
        while (!in.isEmpty()){
            TwoTuple<ItemConvertible,Integer> tuple = in.poll();
            builder = builder.input(tuple.a,tuple.b);
        }
        builder = builder.criterion(FabricRecipeProvider.hasItem(crit), FabricRecipeProvider.conditionsFromItem(crit));
        builder.offerTo(exporter,new Identifier(DalisFunMod.MOD_ID, itemName(output)+"_from"+suffix));
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
    private static String itemName(ItemConvertible item){
        if (item instanceof Item){
            return Registries.ITEM.getId((Item) item).getPath();
        }else if (item instanceof Block){
            return Registries.BLOCK.getId((Block) item).getPath();
        }
        return "";
    }
}
