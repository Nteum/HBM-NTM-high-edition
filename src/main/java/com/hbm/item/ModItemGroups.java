package com.hbm.item;

import com.hbm.DalisFunMod;
import com.hbm.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class ModItemGroups {
    //金属锭、粒、线材，机器组件
    public static final ItemGroup partsTab = Registry.register(Registries.ITEM_GROUP,
            new Identifier(DalisFunMod.MOD_ID,"hbm_parts"),
            FabricItemGroup.builder().icon(()->new ItemStack(ModItems.ingot_uranium))
                    .displayName(Text.translatable("itemGroup."+ DalisFunMod.MOD_ID+".hbm_parts"))
                    .entries(((displayContext, entries) -> {
                        // 金属锭
                        Field[] fields = ModItems.class.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.getType() == Item.class) {
                                try {
                                    entries.add((ItemConvertible) field.get(null));
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }))
                    .build());
    //矿物、材料方块等。
    public static final ItemGroup blockTab = Registry.register(Registries.ITEM_GROUP,
            new Identifier(DalisFunMod.MOD_ID,"hbm_blocks"),
            FabricItemGroup.builder().icon(()->new ItemStack(ModItems.ingot_uranium))
                    .displayName(Text.translatable("itemGroup."+ DalisFunMod.MOD_ID+".hbm_blocks"))
                    .entries(((displayContext, entries) -> {
                        // 矿物
                        entries.add(ModBlocks.ore_uranium);
                        //机器
                        entries.add(ModBlocks.machine_electric_furnace);
                        entries.add(ModBlocks.battery_schrabidium);
                        //家具和饰品
                        entries.add(ModBlocks.lamp_tritium_blue);
                        entries.add(ModBlocks.concrete);
                        entries.add(ModBlocks.concrete_colored);
                        entries.add(ModBlocks.brick_concrete);
                        entries.add(ModBlocks.brick_concrete_mossy);
                        entries.add(ModBlocks.brick_concrete_cracked);
                        entries.add(ModBlocks.brick_concrete_broken);
                        entries.add(ModBlocks.brick_concrete_marked);
                    }))
                    .build());
    public static void registerModItemGroups(){
        // 注意：注册名不允许用大写字母
        DalisFunMod.LOGGER.info("Register Mod Groups");
    }
}
