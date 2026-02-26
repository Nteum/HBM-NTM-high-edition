package com.hbm.datagen.recipe;

import com.hbm.HBM;
import com.hbm.Inventory.recipe.BlastFurnaceRecipe;
import com.hbm.datagen.recipe.provider.*;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModItems;;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;

public class RecipeGen extends RecipeProvider {
    private static Consumer<FinishedRecipe> pwriter;
    private final ExistingFileHelper existingFileHelper;
    private final List<ISubRecipeProvider> compatProviders = new ArrayList<>();
    public static int recipeCnt;
    public RecipeGen(PackOutput pOutput, ExistingFileHelper existingFileHelper, String modid) {
        super(pOutput);
        recipeCnt = 0;
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        pwriter = pWriter;
        getSubRecipeProviders().forEach(subRecipeProvider -> subRecipeProvider.addRecipes(pwriter));

        BlastFurnaceRecipe.addDefaultRecipe(pWriter);
        addShapelessRecipe(ModItems.NUGGET_ZIRCONIUM.get(),9, ModItems.INGOT_ZIRCONIUM.get(),ModItems.INGOT_ZIRCONIUM.get(),1);
        addShapelessRecipe(ModItems.INGOT_ZIRCONIUM.get(),1,ModItems.INGOT_ZIRCONIUM.get(),ModItems.NUGGET_ZIRCONIUM.get(),9);

        simpleCookingRecipe(pWriter, "smoking", RecipeSerializer.SMOKING_RECIPE, 100, ModItems.GLYPHID_MEAT.get(), ModItems.GLYPHID_MEAT_GRILLED.get(), 0.35f);
    }
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return List.of(
                new AssemblerRecipeProvider(),
                new ChemplantRecipeProvider(),
                new ShredderRecipeProvider(),
                new MachineBlockRecipeProvider(),
                new PressRecipeProvider()
        );
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
        builder.save(pwriter,new ResourceLocation(HBM.MODID, itemName(output)+"_"+recipeCnt++));
    }
    //添加默认的无序配方（默认加入MISC组，并使用获得物品来解锁，通过输入物品来区分）
    private static void addShapelessRecipe(ItemLike output,int outnum,ItemLike crit,Object... input){
        Queue<TwoTuple<ItemLike,Integer>> in = new LinkedList<>();
        Object[] aobject = input;
        for (int j = 0; j < input.length; ++j)
        {
            if (aobject[j] instanceof ItemLike){
                in.offer(new TwoTuple<>((ItemLike)aobject[j]));
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
        builder.save(pwriter,new ResourceLocation(HBM.MODID, itemName(output)+"_"+recipeCnt++));
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
