package com.hbm.block;

import com.hbm.block.machine.MachineElectricFurnace;
import com.hbm.DalisFunMod;
import com.hbm.block.decorate.ConcreteColored;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block ore_uranium = new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE).hardness(5.0F).resistance(10.0F).requiresTool());
    public static final Block machine_electric_furnace = new MachineElectricFurnace(FabricBlockSettings.copyOf(Blocks.FURNACE));
    public static final Block battery_schrabidium = new Block(FabricBlockSettings.create());
    public static final Block lamp_tritium_blue = (Block) new RedstoneLampBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_LAMP));
    public static final Block concrete = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).resistance(140.0f));
    public static final Block concrete_colored = new ConcreteColored(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).resistance(140.0f));
    public static final Block brick_concrete = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).resistance(160f));
    public static final Block brick_concrete_mossy = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).resistance(160f));
    public static final Block brick_concrete_cracked = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).resistance(60f));
    public static final Block brick_concrete_broken = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).resistance(45f));
    public static final Block brick_concrete_marked = new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_BRICKS).resistance(15f));



    // 注册矿物
    private static void registerOre(){
        registerBlock("ore_uranium",ore_uranium);
    }
    //注册机器
    private static void registerMachine(){
        registerBlock("machine_electric_furnace",machine_electric_furnace);
        registerBlock("battery_schrabidium",battery_schrabidium);
    }
    //注册一些装饰性或者不可变的方块
    public static void registerDecorate(){
        registerBlock("lamp_tritium_blue",lamp_tritium_blue);
        registerBlock("concrete",concrete);
        registerBlock("concrete_colored",concrete_colored);
        registerBlock("brick_concrete",brick_concrete);
        registerBlock("brick_concrete_mossy",brick_concrete_mossy);
        registerBlock("brick_concrete_cracked",brick_concrete_cracked);
        registerBlock("brick_concrete_broken",brick_concrete_broken);
        registerBlock("brick_concrete_marked",brick_concrete_marked);
    }

    private static Block registerBlock(String name,Block block){
        registerBlockItem(name,block);
        return Registry.register(Registries.BLOCK,new Identifier(DalisFunMod.MOD_ID,name),block);
    }
    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM,new Identifier(DalisFunMod.MOD_ID,name),new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerBlocks(){
        registerOre();
        registerMachine();
        registerDecorate();
        DalisFunMod.LOGGER.info("Register mod blocks.");
    }
}
