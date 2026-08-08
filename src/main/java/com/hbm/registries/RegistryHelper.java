package com.hbm.registries;
import com.hbm.HBM;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// 用于处理字符串、注册表，resourcelocation等等的工具。
public class RegistryHelper {
    public static ResourceLocation rl(String path){
        return ResourceLocation.fromNamespaceAndPath(HBM.MODID, path);
    }
    public static String generateOrderlyName(String name){
        return Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0,1).toUpperCase() + s.substring(1)).reduce("",(r, id) -> r + (r.isEmpty() ? "": " ") + id);
    }
    public static String generateReversedName(String name){
        List<String> strings = new java.util.ArrayList<>(Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList());
        Collections.reverse(strings);
        return strings.subList(1, strings.size()).stream().reduce(strings.get(0), (s, s1) -> s + " " + s1);
    }

    public static String generateOrderlyExceptFirstName(String name){
        List<String> strings = Arrays.stream(name.split("_|\\.")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).toList();
        return strings.subList(1, strings.size()).stream().reduce("",(r, id) -> r + (r.isEmpty() ? "": " ") + id) + " " + strings.get(0);
    }

    public static ResourceLocation itemRL(Item item){
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation blockRL(Block block){
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation prefix(ResourceLocation rl, String prefix){
        return rl.getPath().startsWith(prefix) ? rl : rl.withPrefix(prefix);
    }

    public static ResourceLocation suffix(ResourceLocation rl, String suffix){
        return rl.getPath().startsWith(suffix) ? rl : rl.withSuffix(suffix);
    }

    public static <T>boolean containIdx(T[] array, int idx){
        return array == null || idx < 0 || array.length <= idx;
    }

    public static boolean contain(int[] array, int value){
        if (array == null) return false;
        for (int i : array) {
            if (value == array[i]) return true;
        }
        return false;
    }

    public static String getOrBlank(String[] array, int idx){
        return containIdx(array, idx) ? "" : array[idx];
    }

    public static ResourceLocation getOrDefault(ResourceLocation[] array, int idx, ResourceLocation defaultValue){
        return containIdx(array, idx) ? defaultValue : array[idx];
    }

    public static String fluidKey(Fluid fluid){
        return ForgeRegistries.FLUIDS.getKey(fluid).toString();
    }

    public static Fluid fluid(String s){
        return ForgeRegistries.FLUIDS.getValue(ResourceLocation.parse(s));
    }

    public static FluidType fluidType(ResourceLocation resourceLocation){
        return ForgeRegistries.FLUID_TYPES.get().getValue(resourceLocation);
    }

    public static ResourceLocation fluidType(FluidType fluidType){
        return ForgeRegistries.FLUID_TYPES.get().getKey(fluidType);
    }
}