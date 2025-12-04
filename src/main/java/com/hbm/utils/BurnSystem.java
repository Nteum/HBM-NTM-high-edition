package com.hbm.utils;

import com.hbm.HBMLang;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 模组中和燃烧相关的概念
 * */
public class BurnSystem {
    public static final int typeNum = 8;

    private static final int modLog = 0;
    private static final int modWood = 1;
    private static final int modCoal = 2;
    private static final int modLignite = 3;
    private static final int modCoke = 4;
    private static final int modSolid = 5;
    private static final int modRocket = 6;
    private static final int modBalefire = 7;

    public static double[] getMod(BlockEntityType type){
        double[] burnMod = new double[2 * typeNum];
        Arrays.fill(burnMod, 1);
        if (type == ModBlockEntityType.WOOD_BURNER.get()){
            burnMod[modLog] = 4;
            burnMod[modWood] = 2;
        }

        return burnMod;
    }
    public static int getBurnTime(ItemStack stack, BlockEntityType type){
        return stack.getBurnTime(RecipeType.BLASTING);
    }
    public static int getBurnHeat(ItemStack stack){
        return 100 * stack.getBurnTime(RecipeType.BLASTING);
    }
    public static EnumAshType getAshFromFuel(ItemStack stack) {
        if (stack.is(ModTags.Items.COKE) || stack.is(ItemTags.COALS) || stack.is(ModTags.Items.LIGNITE)) return EnumAshType.COAL;
        else if (stack.is(ItemTags.LOGS) || stack.is(ModTags.Items.WOOD) || stack.is(ModTags.Items.SAPLING)) return EnumAshType.WOOD;
        return EnumAshType.MISC;
    }
    public static List<Component> getBurnDesc(List<Component> tooltips, ItemStack item, BlockEntityType type){
        List<Component> desc = new ArrayList<>();
        desc.addAll(getTimeDesc(tooltips, type));
        desc.addAll(getHeatDesc(tooltips, type));
        return desc;
    }
    public static List<Component> getTimeDesc(List<Component>tooltips, BlockEntityType type) {
        tooltips.add(HBMLang.GUI_TOOLTIP_BURN_TIME_BONUS.translate().withStyle(ChatFormatting.GOLD));

//        addIf(list, "Logs", modTime[modLog]);
//        addIf(list, "Wood", modTime[modWood]);
//        addIf(list, "Coal", modTime[modCoal]);
//        addIf(list, "Lignite", modTime[modLignite]);
//        addIf(list, "Coke", modTime[modCoke]);
//        addIf(list, "Solid Fuel", modTime[modSolid]);
//        addIf(list, "Rocket Fuel", modTime[modRocket]);
//        addIf(list, "Balefire", modTime[modBalefire]);
//
//        if(list.size() == 1)
//            list.clear();
//
//        return list;
        return tooltips;
    }

    public static List<Component> getHeatDesc(List<Component>tooltips, BlockEntityType type) {

//        list.add(EnumChatFormatting.RED + "Burn heat bonuses:");
//
//        addIf(list, "Logs", modHeat[modLog]);
//        addIf(list, "Wood", modHeat[modWood]);
//        addIf(list, "Coal", modHeat[modCoal]);
//        addIf(list, "Lignite", modHeat[modLignite]);
//        addIf(list, "Coke", modHeat[modCoke]);
//        addIf(list, "Solid Fuel", modHeat[modSolid]);
//        addIf(list, "Rocket Fuel", modHeat[modRocket]);
//        addIf(list, "Balefire", modHeat[modBalefire]);
//
//        if(list.size() == 1)
//            list.clear();
//
//        return list;

        return tooltips;
    }

    private void addIf(List<Component> list, Component name, double mod) {
        if(mod != 1.0D) list.add(Component.translatable("").append("- ").append(name).append(": " + mod + "%"));
    }


    private static String getPercent(double mod) {
        mod -= 1D;
        String num = ((int) (mod * 100)) + "%";
//
//        if(mod < 0)
//            num = EnumChatFormatting.RED + num;
//        else
//            num = EnumChatFormatting.GREEN + "+" + num;

        return num;
    }

    public enum EnumAshType {
        WOOD(ModItems.POWDER_ASH_WOOD.get()),
        COAL(ModItems.POWDER_COAL.get()),
        MISC(ModItems.POWDER_ASH_MISC.get()),
        FLY(ModItems.POWDER_ASH_FLY.get()),
        SOOT(ModItems.POWDER_ASH_SOOT.get()),
        FULLERENE(ModItems.POWDER_ASH_FULLERENE.get())
        ;
        public Item item;
        EnumAshType(Item item){
            this.item = item;
        }
    }
}
