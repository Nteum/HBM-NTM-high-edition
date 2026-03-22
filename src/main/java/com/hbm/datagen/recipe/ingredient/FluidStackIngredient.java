package com.hbm.datagen.recipe.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.Inventory.recipe.RecipeHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FluidStackIngredient {
    public static final FluidStackIngredient EMPTY = new FluidStackIngredient();
    public boolean flagTag = false;         // 存放的是否为tag
    public FluidStack fluidStack = null;    // 对于存放fluidStack的情况，我们只关心它的流体类库
    public TagKey<Fluid> tagKey = null;
    public int volume = 0;                  // 所需流体的容量
    public static FluidStackIngredient of(FluidStack fluidStack){
        FluidStackIngredient value = new FluidStackIngredient();
        value.fluidStack = fluidStack;
        value.volume = fluidStack.getAmount();
        return value;
    }
    public static FluidStackIngredient of(TagKey<Fluid> pTag){
        return of(pTag,1000);
    }
    public static FluidStackIngredient of(TagKey<Fluid> pTag, int count){
        FluidStackIngredient value = new FluidStackIngredient();
        value.flagTag = true;
        value.tagKey = pTag;
        value.volume = count;
        return value;
    }
    public boolean isEmpty(){
        return fluidStack == null &&  tagKey==null;
    }

    public Collection<FluidStack> getFluids() {
        if (!flagTag){
            return Collections.singleton(this.fluidStack);
        }else {
            List<FluidStack> list = Lists.newArrayList();
            // 根据key获得流体用的是BuiltInRegistries.FLUID吗？
            for(Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tagKey)) {
                list.add(new FluidStack(holder.get(), this.volume));
            }
            return list;
        }
    }

    public int getVolume(){
        return this.volume;
    }
    public boolean test(FluidStack pStack){
        if (pStack==null)return false;
        else {
            if (!this.flagTag){
                return this.fluidStack.isFluidEqual(pStack);
            }else {
                for (Holder<Fluid> fluidHolder : BuiltInRegistries.FLUID.getTagOrEmpty(this.tagKey)) {
                    if (pStack.getFluid().isSame(fluidHolder.value()))
                        return true;
                }
            }
            return false;
        }
    }

    public JsonObject toJson() {
        JsonObject jsonobject = new JsonObject();
        if (!flagTag){
            jsonobject.addProperty(HBMKey.FLUIDS, BuiltInRegistries.FLUID.getKey(this.fluidStack.getFluid()).toString());
        }else {
            jsonobject.addProperty(HBMKey.TAG, this.tagKey.location().toString());
        }
        jsonobject.addProperty(HBMKey.VOLUME, this.volume);
        return jsonobject;
    }
    public static FluidStackIngredient fromJson(JsonObject json){
        if (json.has(HBMKey.FLUIDS) && json.has(HBMKey.TAG)) {
            throw new JsonParseException("An ingredient entry is either a tag or an fluidStack, not both");
        } else if (json.has(HBMKey.FLUIDS)) {
            Fluid fluid = RecipeHelper.fluidFromJson(json);
            int count = json.get(HBMKey.VOLUME).getAsInt();
            return FluidStackIngredient.of(new FluidStack(fluid,count));
        } else if (json.has(HBMKey.TAG)) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(json, HBMKey.TAG));
            TagKey<Fluid> tagkey = TagKey.create(Registries.FLUID, resourcelocation);
            int count = json.get(HBMKey.VOLUME).getAsInt();
            return FluidStackIngredient.of(tagkey,count);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }
    public static void toNetwork(FriendlyByteBuf buffer, FluidStackIngredient ingredient){
        buffer.writeBoolean(ingredient.flagTag);
        if (ingredient.flagTag){
            // 只存储
            buffer.writeResourceLocation(ingredient.tagKey.location());
        }else {
            buffer.writeFluidStack(ingredient.fluidStack);
        }
        buffer.writeInt(ingredient.volume);
    }
    public static FluidStackIngredient fromNetwork(FriendlyByteBuf buffer){
        FluidStackIngredient ingredient = new FluidStackIngredient();
        ingredient.flagTag = buffer.readBoolean();
        if (ingredient.flagTag){
            ResourceLocation resourceLocation = buffer.readResourceLocation();
            ingredient.tagKey = new TagKey<Fluid>(Registries.FLUID, resourceLocation);
            // 通过水的registryholder判断tagkey
            if (!Fluids.WATER.builtInRegistryHolder().is(ingredient.tagKey)){
                throw new JsonParseException("An ingredient entry failed to load fluid tag");
            }
        }else {
            ingredient.fluidStack = buffer.readFluidStack();
        }
        ingredient.volume = buffer.readInt();
        return ingredient;
    }
}
