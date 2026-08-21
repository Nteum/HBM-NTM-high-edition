package com.hbm.registries;
import com.hbm.HBM;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// 用于处理字符串、注册表，resourcelocation等等的工具。
public class RegistryHelper {

    /**
     * 根据内部名称生成英文名
     */
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

    /**
     * 生成某个东西相应的RL
     */
    public static ResourceLocation rl(String path){
        return ResourceLocation.fromNamespaceAndPath(HBM.MODID, path);
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

    /**
     * 特定东西的序列化和反序列化
     */
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

    /** 关于世界类型方面的判断 */
    public static boolean worldIsSuperFlat(ServerLevel level){
        return level.isFlat();
    }

    public static boolean worldCanSpawnLightingBolt(Level level, BlockPos pos){
        // 1. 获取位置所在的生物群系
        Biome biome = level.getBiome(pos).value();

        // 2. 检查生物群系是否有降水（下雨或下雪）
        //    原版中，下雪也算有降水，但闪电只会在下雨时出现，因此还需要检查温度
        if (!biome.hasPrecipitation()) {
            return false;
        }

        // 3. 检查生物群系的温度是否高到足以让降水变成雨，而不是雪
        //    在 1.20.1 中，温度低于 0.15 时降水会变成雪，闪电不会在雪天出现
        //    使用 getBaseTemperature() 获取生物群系的基础温度
        if (biome.getBaseTemperature() < 0.15f) {
            return false;
        }

        // 4. 检查该位置是否暴露在天空下（即头顶没有遮挡物）
        //    使用 Level.canSeeSky() 方法
        if (!level.canSeeSky(pos)) {
            return false;
        }

        // 所有条件满足，可以生成闪电
        return true;
    }


}