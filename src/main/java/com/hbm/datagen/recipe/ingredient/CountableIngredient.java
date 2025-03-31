package com.hbm.datagen.recipe.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 创建这种ingredient主要是因为原版Ingredient序列化的时候竟然不加入数量
 * forge的StrictNBTIngredient可以记录数量，但又会记录所有的nbt，并且没有tag功能
 * 我需要可以比较数量，并具有tag功能的Ingredient类
 * */
public class CountableIngredient extends AbstractIngredient {
    private final Value value;
    public static CountableIngredient of(ItemStack itemStack){
        return new CountableIngredient(Value.item(itemStack));
    }
    public static CountableIngredient of(ItemLike itemLike, int count){
        return new CountableIngredient(Value.item(new ItemStack(itemLike, count)));
    }
    public static CountableIngredient of(TagKey<Item> key){
        return new CountableIngredient(Value.tag(key));
    }
    public static CountableIngredient of(TagKey<Item> key, int count){
        return new CountableIngredient(Value.tag(key,count));
    }
    private CountableIngredient(Value value){
        this.value = value;
    }
    //=========================================
    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean test(@Nullable ItemStack pStack) {
        if (pStack==null)return false;
        else {
            if (!this.value.flagTag){
                return this.value.itemStack.is(pStack.getItem()) && this.value.getCount() <= pStack.getCount();
            }else {
                boolean flag = false;
                for (ItemStack itemStack : this.value.getItems()) {
                    if (itemStack.is(pStack.getItem())){
                        flag = true;
                        break;
                    }
                }
                return flag && this.value.tagItemNum <= pStack.getCount();
            }
        }
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        return this.value.serialize();
    }

    public static class Serializer implements IIngredientSerializer<CountableIngredient>
    {
        public static final StrictNBTIngredient.Serializer INSTANCE = new StrictNBTIngredient.Serializer();
        @Override
        public CountableIngredient parse(FriendlyByteBuf buffer) {
            Value value1 = new Value();
            boolean flag = buffer.readBoolean();
            int count = buffer.readInt();
            value1.flagTag = flag;
            if (flag){
                int byteslen = buffer.readInt();
                String string = buffer.readBytes(byteslen).toString();
                TagKey<Item> tagKey = parseTagKey(string);
                value1.tagKey = tagKey;
            }else {
                ItemStack itemStack = buffer.readItem();
                itemStack.setCount(count);
                value1.itemStack = itemStack;
            }

            return new CountableIngredient(value1);
        }

        @Override
        public CountableIngredient parse(JsonObject json) {
            if (json.has("item") && json.has("tag")) {
                throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
            } else if (json.has("item")) {
                Item item = ShapedRecipe.itemFromJson(json);
                int count = json.get("count").getAsInt();
                return CountableIngredient.of(new ItemStack(item,count));
            } else if (json.has("tag")) {
                ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
                TagKey<Item> tagkey = TagKey.create(Registries.ITEM, resourcelocation);
                int count = json.get("count").getAsInt();
                return CountableIngredient.of(tagkey,count);
            } else {
                throw new JsonParseException("An ingredient entry needs either a tag or an item");
            }
        }

        @Override
        public void write(FriendlyByteBuf buffer, CountableIngredient ingredient) {
            buffer.writeBoolean(ingredient.value.flagTag);
            buffer.writeInt(ingredient.value.getCount());
            if (ingredient.value.flagTag){
                byte[] bytes = ingredient.value.tagKey.toString().getBytes();
                buffer.writeInt(bytes.length);
                buffer.writeBytes(bytes);
            }else {
                buffer.writeItem(ingredient.value.itemStack);
            }
        }
    }
    static TagKey<Item> parseTagKey(String s){
//        StringBuilder stringBuilder = new StringBuilder(s);
//        stringBuilder.indexOf("]\\");
//        String location = s.substring(s.indexOf("]/") + 1, s.length() - 1);

        return TagKey.create(Registries.ITEM,new ResourceLocation(s.substring(s.indexOf("]/") + 2, s.length() - 1)));
    }

    public static class Value implements Ingredient.Value{
        boolean flagTag = false;    //存放的是否为tag
        ItemStack itemStack;
        TagKey<Item> tagKey;
        int tagItemNum = 0; //如果存放tag则所需数量
        public static Value item(ItemStack itemStack){
            Value value = new Value();
            value.itemStack = itemStack;
            return value;
        }
        public static Value tag(TagKey<Item> pTag){
            return tag(pTag,1);
        }
        public static Value tag(TagKey<Item> pTag, int count){
            Value value = new Value();
            value.flagTag = true;
            value.tagKey = pTag;
            value.tagItemNum = count;
            return value;
        }

        @Override
        public Collection<ItemStack> getItems() {
            if (!flagTag){
                return Collections.singleton(this.itemStack);
            }else {
                List<ItemStack> list = Lists.newArrayList();

                for(Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(this.tagKey)) {
                    list.add(new ItemStack(holder));
                }

                if (list.isEmpty()) {
                    list.add(new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER).setHoverName(net.minecraft.network.chat.Component.literal("Empty Tag: " + this.tagKey.location())));
                }
                return list;
            }
        }

        public int getCount(){
            return flagTag ? tagItemNum : itemStack.getCount();
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            if (!flagTag){
                jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.itemStack.getItem()).toString());
                jsonobject.addProperty("count", this.itemStack.getCount());
            }else {
                jsonobject.addProperty("tag", this.tagKey.location().toString());
                jsonobject.addProperty("count", this.tagItemNum);
            }
            return jsonobject;
        }
    }
}
