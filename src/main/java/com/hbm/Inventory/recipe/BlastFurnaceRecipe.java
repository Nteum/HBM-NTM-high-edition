package com.hbm.Inventory.recipe;

import com.google.gson.JsonObject;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.registries.ModItems;
import com.hbm.utils.Tuple;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.hbm.datagen.recipe.RecipeGen.recipeCnt;

public class BlastFurnaceRecipe implements Recipe<Container> {
//    //配方种类名
//    public static final String TYPE = "blastfurnace_recipe";
    //配方自身的名称
    private final ResourceLocation id;
    //配方：（物品1，物品2，产出）
    public final Tuple.Triplet<ItemStack,ItemStack,ItemStack> recipeItems;
    public BlastFurnaceRecipe(){id = null;recipeItems = null;}
    public BlastFurnaceRecipe(ResourceLocation id, Tuple.Triplet<ItemStack,ItemStack,ItemStack> recipeItems){
        this.id = id;
        this.recipeItems = recipeItems;
    }
    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pLevel.isClientSide())return false;
        return recipeItems.getX().is(pContainer.getItem(1).getItem())
                && recipeItems.getY().is(pContainer.getItem(2).getItem())
                || recipeItems.getX().is(pContainer.getItem(2).getItem())
                && recipeItems.getY().is(pContainer.getItem(1).getItem());
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return recipeItems.getZ();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return recipeItems.getZ().copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BLAST.type().get();
    }

    public static class Type implements RecipeType<BlastFurnaceRecipe>{
        private Type(){}
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<BlastFurnaceRecipe>{
        public static final Serializer INSTANCE = new Serializer();
//        public static final ResourceLocation ID = HBM.rl(ModRecipes.BLAST.toString());
        //从json解码出这个recipe类型
        @Override
        public BlastFurnaceRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
//            ItemStack output = ShapedRecipe.itemStackFromJson();
//            GsonHelper.getAsJsonArray(pSerializedRecipe,"")
            String s = GsonHelper.getAsString(pJson, "group", "");
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            ItemStack input1 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "input1"));
            ItemStack input2 = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "input2"));
            return new BlastFurnaceRecipe(pRecipeId,new Tuple.Triplet<>(input1,input2,output));
        }

        //从服务器发送数据解码recipe类型，配方id不需要解码
        @Override
        public @Nullable BlastFurnaceRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack input1 = pBuffer.readItem();
            ItemStack input2 = pBuffer.readItem();
            ItemStack output = pBuffer.readItem();
            return new BlastFurnaceRecipe(pRecipeId,new Tuple.Triplet<>(input1,input2,output));
        }
        //
        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BlastFurnaceRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.recipeItems.getX());
            pBuffer.writeItem(pRecipe.recipeItems.getY());
            pBuffer.writeItem(pRecipe.recipeItems.getZ());
        }
    }
    public static class BlastFurnaceRecipeBuilder implements RecipeBuilder{
        private final Item result;
        private final int count;
        private final List<ItemStack> recipeIn = new ArrayList<>();
        private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
        @Nullable
        private String group;
        public BlastFurnaceRecipeBuilder(ItemLike presult, int pcount){
            result = presult.asItem();
            count = pcount;
        }
        public static BlastFurnaceRecipeBuilder blast(ItemLike result){
            return new BlastFurnaceRecipeBuilder(result,1);
        }
        public static BlastFurnaceRecipeBuilder blast(ItemLike result, int count){
            return new BlastFurnaceRecipeBuilder(result,count);
        }
        public BlastFurnaceRecipeBuilder input(ItemLike item,int num){
            return this.input(new ItemStack(item,num));
        }
//        public BlastFurnaceRecipeBuilder input(TagKey<Item> pTag) {
//            return this.input(Ingredient.of(pTag));
//        }
        public BlastFurnaceRecipeBuilder input(ItemStack itemStack){
            if (recipeIn.size() >= 2){
                throw new IllegalArgumentException("高炉配方最多只能输入两个物品");
            }else {
                recipeIn.add(itemStack);
                return this;
            }
        }

        /** 解锁条件：直接参考有序配方，用成就结果。HBM的熔炉配方解锁成就均设为造出熔炉本身 */
        @Override
        public RecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
            this.advancement.addCriterion(pCriterionName,pCriterionTrigger);
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String pGroupName) {
            this.group = pGroupName;
            return this;
        }
        /** 返回配方结果，如果配方结果不止一个东西怎么办？ */
        @Override
        public Item getResult() {
            return this.result;
        }

        @Override
        public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
            pFinishedRecipeConsumer.accept(new Result(pRecipeId,this.result,this.count,this.group == null?"":this.group, this.recipeIn,this.advancement));
        }

        public static class Result implements FinishedRecipe{
            private final ResourceLocation id;
            /** 输出产物的结果 */
            private final Item result;
            private final int count;
            private final String group;
            /** 输入的原料 */
            private final List<ItemStack> recipeIn;
            private final Advancement.Builder advancement;
            public Result(ResourceLocation id,Item result,int count,String group,List<ItemStack> recipeIn,Advancement.Builder pAdvancement){
                this.id = id;
                this.result = result;
                this.count = count;
                this.group = group;
                this.recipeIn = recipeIn;
                this.advancement = pAdvancement;
            }
            /** （重）将配方信息序列化到JSON中 */
            @Override
            public void serializeRecipeData(JsonObject pJson) {
                //添加type
                pJson.addProperty("type", HBM.MODID + ":" + HBMKey.BLAST);
                //添加group
                if (!this.group.isEmpty()) {
                    pJson.addProperty("group", this.group);
                }
                //添加输入物品
                JsonObject jsonobject1 = new JsonObject();
                jsonobject1.addProperty("item", BuiltInRegistries.ITEM.getKey(this.recipeIn.get(0).getItem()).toString());
                JsonObject jsonobject2 = new JsonObject();
                jsonobject2.addProperty("item", BuiltInRegistries.ITEM.getKey(this.recipeIn.get(1).getItem()).toString());
                pJson.add("input1",jsonobject1);
                pJson.add("input2",jsonobject2);
                //添加result
                JsonObject jsonobject3 = new JsonObject();
                jsonobject3.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
                if (this.count > 1) {
                    jsonobject3.addProperty("count", this.count);
                }
                pJson.add("result",jsonobject3);
            }

            @Override
            public ResourceLocation getId() {
                return this.id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return Serializer.INSTANCE;
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        }
    }

    public static void addDefaultRecipe(Consumer<FinishedRecipe> pWriter){
        registerRecipe(pWriter, Items.IRON_INGOT,Items.COAL,new ItemStack(ModItems.INGOT_STEEL.get(),1));
        registerRecipe(pWriter, Items.RAW_IRON,Items.COAL,new ItemStack(ModItems.INGOT_STEEL.get(),1));
        registerRecipe(pWriter, Blocks.IRON_ORE,Items.COAL,new ItemStack(ModItems.INGOT_STEEL.get(),2));
        registerRecipe(pWriter, Items.COPPER_INGOT,Items.REDSTONE,new ItemStack(ModItems.INGOT_RED_COPPER.get(),2));
        registerRecipe(pWriter, ModItems.INGOT_STEEL.get(),ModItems.INGOT_RED_COPPER.get(),new ItemStack(ModItems.INGOT_ADVANCED_ALLOY.get(),2));
    }
    public static void registerRecipe(Consumer<FinishedRecipe> pwriter, ItemLike input1,ItemLike input2,ItemStack output){
        BlastFurnaceRecipe.BlastFurnaceRecipeBuilder.blast(output.getItem(),output.getCount())
                .input(input1,1).input(input2,1)
                .save(pwriter,new ResourceLocation(HBM.MODID,HBMKey.BLAST + "_" + recipeCnt++));
    }

}
