package com.hbm.registries;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.HBMMachine;
import com.hbm.block.base.BlockBase;
import com.hbm.block.decoriate.BlockOre;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.*;
import com.hbm.block.logistic.BlockCable;
import com.hbm.block.machine.*;
import com.hbm.block.logistic.BlockConveyor;
import com.hbm.block.weapon.*;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.item.ItemBlockCustomModel;
import com.hbm.item.tool.BatteryBlockItem;
import com.hbm.block.base.DummibleBlock;
import com.hbm.registries.WrapperRegistry.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.hbm.HBM.MODID;

public class ModBlocks {
    //方块注册表
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final List<WrappedBlockRegistry> blockList = new ArrayList<>();
    static {
        HBMMachine.register(BLOCKS);
        HBMBlockComponent.register(BLOCKS);
    }
    // 矿石
    public static final RegistryObject<Block> ORE_URANIUM = block("ore_uranium", ()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)), HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);

    // 机器
    public static final RegistryObject<Block> PWR_CONTROLLER = ModBlocks.add("pwr_controller", ()->new BlockPWRController(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_FRONT_SIDE,HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> DIFURNACE = machine("furnace_blast", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> FURNACE_ELECTRIC = machine("furnace_electric", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> BOILER = machine("boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> BOILER_ELECTRIC = machine("boiler_electric", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> BOILER_NUCLEAR = machine("boiler_nuclear", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> PRESS = new BlockBuilder("press", ()->new BlockPress(Properties.of())).model(HBMKey.MODEL_HORIZONTAL_WITH_FILE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(press -> new ItemBlockCustomModel(press, new Item.Properties(), 3).setOffset(0.5, 0, 0.5)).build();
    public static final RegistryObject<Block> PRESS_PREHEATER = machine("press_preheater", ()->new BlockBase(Properties.of()), HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> ANVIL_IRON = new BlockBuilder("anvil_iron",()->new BlockAnvil(Properties.of())).model((block, blockstateGen) -> blockstateGen.horizontalBlockWithItem(block)).tab(ModTabs.MACHINE.getKey()).loc("Tier 1 (iron) Anvil").build();
    public static final RegistryObject<Block> ANVIL_DESH = new BlockBuilder("anvil_desh",()->new BlockAnvil(Properties.of())).model((block, blockstateGen) -> blockstateGen.horizontalBlockWithItem(block)).tab(ModTabs.MACHINE.getKey()).loc("Tier 2 (desh) Anvil").build();
    public static final RegistryObject<Block> ANVIL_BISMUTH = new BlockBuilder("anvil_bismuth",()->new BlockAnvil(Properties.of())).model((block, blockstateGen) -> blockstateGen.horizontalBlockWithItem(block)).tab(ModTabs.MACHINE.getKey()).loc("Tier 3 (bismuth) Anvil").build();
    public static final RegistryObject<Block> CRACKING_TOWER = new BlockBuilder("cracking_tower",()->new BlockCrackingTower(Properties.of())).model((block, blockstateGen) -> blockstateGen.horizontalBlockWithItem(block)).tab(ModTabs.MACHINE.getKey()).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).build();
    public static final RegistryObject<Block> BATTERY = new BlockBuilder("battery", ()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block, new Item.Properties())).loot(HBMKey.DROP_SELF).loc("Energy Storage Block").build();
    public static final RegistryObject<Block> BATTERY_LITHIUM = new BlockBuilder("battery_block_lithium", ()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block, new Item.Properties())).loot(HBMKey.DROP_SELF).loc("Li-Ion Energy Storage Block").build();
    public static final RegistryObject<Block> BATTERY_SCHRABIDIUM = new BlockBuilder("battery_schrabidium", ()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block, new Item.Properties())).loot(HBMKey.DROP_SELF).loc("Schrabidium Energy Storage Block").build();
    public static final RegistryObject<Block> BATTERY_DINEUTRONIUM = new BlockBuilder("battery_dineutronium", ()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block, new Item.Properties())).loot(HBMKey.DROP_SELF).loc("Spark Energy Storage Block").build();
    public static final RegistryObject<Block> ASSEMBLER = new BlockBuilder("assembler", ()->new BlockAssembler(Properties.copy(Blocks.IRON_BLOCK))).model(HBMKey.MODEL_HORIZONTAL_WITH_FILE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(block -> new ItemBlockCustomModel(block, new Item.Properties(), 4).setOffset(0.5, 0.5, 0.5)).build();
    public static final RegistryObject<Block> CRUCIBLE = new BlockBuilder("crucible",()->new BlockCrucible(Properties.copy(Blocks.BRICKS))).model((block, blockstateGen) -> blockstateGen.horizontalBlockWithItem(block)).tab(ModTabs.MACHINE.getKey()).loc(HBMKey.ORDERLY_GEN).loot(HBMKey.DROP_SELF).build();

    // nuke
    public static final RegistryObject<Block> BOMB_FAT_MAN = add("bomb_fat_man", () -> new NukeFat(Properties.copy(Blocks.STONE), 200), ModTabs.NUKE.getKey(), HBMKey.MODEL_EXISTING_FILE, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> BOMB_BOY = add("bomb_boy",()->new NukeBoy(Properties.copy(Blocks.STONE),120), ModTabs.NUKE.getKey(), HBMKey.MODEL_EXISTING_FILE, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMKey.DROP_SELF);
    public static final RegistryObject<Block> BOMB_CUSTOM = add("bomb_custom", () -> new NukeCustom(Properties.copy(Blocks.STONE), 350), ModTabs.NUKE.getKey(), HBMKey.MODEL_EXISTING_FILE, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMKey.DROP_SELF);
    //电力
    public static final RegistryObject<Block> RED_CABLE = registerBlockWithItem("red_cable",()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)));
    //输送带
    public static final RegistryObject<Block> conveyor = registerBlockWithItem("conveyor",()->new BlockConveyor(Properties.of()));
    //炸弹
//    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(),120));
//    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(),200));
//    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(Properties.of(),350));
    //发射台

    //流体
//    public static final RegistryObject<LiquidBlock> irradiated_water = BLOCKS.register("irradiated_water", ()->new LiquidBlock(ModFluids.IRRADIATED_WATER_SOURCE_BLOCK,Properties.copy(Blocks.WATER).noLootTable()));
//    public static final RegistryObject<LiquidBlock> irradiated_polluted = BLOCKS.register("irradiated_polluted", ()->new LiquidBlock(ModFluids.IRRADIATED_POLLUTED_SOURCE_BLOCK,Properties.copy(Blocks.WATER).noLootTable()));
//    public static final RegistryObject<LiquidBlock> sulfuric_acid = BLOCKS.register("sulfuric_acid", ()->new LiquidBlock(ModFluids.SULFURIC_ACID_SOURCE_BLOCK,Properties.copy(Blocks.WATER).noLootTable()));

    //逻辑物
    public static final RegistryObject<Block> DUMMIBLE = registerBlockWithItem("dummible",()->new DummibleBlock(Properties.copy(Blocks.STONE).noLootTable()));
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(Properties.of()));
    // glyphid
    public static final RegistryObject<Block> GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);

    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> state.getValue(BlockStateProperties.LIT)?value:0;
    }

//    public static void registerItem(){
//        //主要用于注册一些特殊模型的物品
//        ModItems.ITEMS.register("bomb_fat_man",()->new NukeFat.NukeItem(bomb_fat_man.get(),new Item.Properties()));
//    }
    public static RegistryObject<Block> registerBattery(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BatteryBlockItem(block.get(),new Item.Properties()));
        return block;
    }
    public static RegistryObject<Block> registerBlockWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
        return block;
    }
    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);
    }

    public static void creativeTab(BuildCreativeModeTabContentsEvent event){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.creativeTabSupport(event);
        }
    }

    public static void genModel(BlockStateGen provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.modelSupport(provider);
        }
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_BLOCK.get(), HBMBlockProperties.VARIANT3);
        provider.addIntStateCubeAllBlock(ModBlocks.GLYPHID_SPAWNER.get(), HBMBlockProperties.VARIANT3);
    }
    public static void languageSupport(LanguageProvider provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.languageSupport(provider);
        }
    }

    public static void lootSupport(BlockLootGen provider){
        for (WrappedBlockRegistry blockRegistry : blockList) {
            blockRegistry.lootSupport(provider);
        }
    }

    protected static RegistryObject<Block> block(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
        return ModBlocks.add(name, sup, ModTabs.BLOCKS.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
    }

    protected static RegistryObject<Block> machine(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
        return ModBlocks.add(name, sup, ModTabs.MACHINE.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
    }

    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.BASIC_MODEL, genNameWay, HBMKey.DROP_SELF, null);
    }
    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay){
        return add(name, sup, tabKey, genModelWay, genNameWay, lootWay, null);
    }
    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay, String localizedName){
        return new BlockBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).loot(lootWay).build();
    }
}
