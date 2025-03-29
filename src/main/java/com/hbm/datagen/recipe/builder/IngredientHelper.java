package com.hbm.datagen.recipe.builder;

import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class IngredientHelper {
    public static Ingredient of(ItemStack... pStacks) {
        return of(Arrays.stream(pStacks));
    }
    public static Ingredient of(Stream<ItemStack> pStacks) {
        return Ingredient.fromValues(pStacks.filter((p_43944_) -> {
            return !p_43944_.isEmpty();
        }).map(CountableItemValue::new));
    }
    public static class CountableItemValue implements Ingredient.Value {
        private final ItemStack item;

        public CountableItemValue(ItemStack pItem) {
            this.item = pItem;
        }

        public Collection<ItemStack> getItems() {
            return Collections.singleton(this.item);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.item.getItem()).toString());
            if (this.item.getCount() > 1) {
                jsonobject.addProperty("count", this.item.getCount());
            }
            return jsonobject;
        }
    }
}
