package com.hbm.settings.fuel;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.util.HashMap;
import java.util.Map;

public class FuelRegistry {
    public static final Map<Item, FuelProperties> FUELS = new HashMap<>();

    static {
        // 默认值

    }
    public static void register(Item item, FuelProperties props) {
        FUELS.put(item, props);
    }

    public static FuelProperties get(ItemStack stack) {
        if (FUELS.containsKey(stack.getItem())) return FUELS.get(stack.getItem());
        else {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            return new FuelProperties(burnTime, burnTime);
        }
    }
    /**
     * 存储流体用对应的流体桶标识。
     * 如果没有记录，默认使用岩浆数据的十分之一
     * */
    public static FuelProperties get(Fluid fluid){
        if (FUELS.containsKey(fluid.getBucket())) return FUELS.get(fluid.getBucket());
        else {
            int burnTime = Items.LAVA_BUCKET.getDefaultInstance().getBurnTime(RecipeType.SMELTING) / 10;
            return new FuelProperties(burnTime, burnTime);
        }
    }

    /**
     * 燃料属性
     * - 如果燃料是物品，就是单个物品的燃烧属性
     * - 如果燃料是流体，就是一桶（1000mB）的燃烧属性
     * */
    public record FuelProperties(float heatValue, int burnTime){
    }

    /**
     * 燃料加载器
     * */
    public static class FuelDataLoader extends SimpleJsonResourceReloadListener {
        public static final Gson GSON = Deserializers.createFunctionSerializer().create();
        public static final FuelDataLoader INSTANCE = new FuelDataLoader();

        public FuelDataLoader() {
            super(GSON, "fuels");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller pProfiler) {
            FuelRegistry.FUELS.clear();

            data.forEach((id, json) -> {
                JsonObject obj = json.getAsJsonObject();

                int burn = obj.get("burn_time").getAsInt();
                int heat = obj.get("heat_value").getAsInt();
//                double eff = obj.get("efficiency").getAsDouble();

                Ingredient ingredient = Ingredient.fromJson(obj);
                for (ItemStack itemStack : ingredient.getItems()) {
                    FUELS.put(itemStack.getItem(), new FuelProperties(burn, heat));
                }
            });
        }
    }
}
