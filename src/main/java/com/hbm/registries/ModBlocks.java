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
    // 这个名字很有迷惑性，但我不知道怎么取更好的了，这只是一个分类的名字
    public static class Ores{
        public static final WrappedBlockRegistry ORE_URANIUM = add("ore_uranium", ()->new BlockOre(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup){
            return add(name, sup, HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);
        }
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
            return ModBlocks.add(name, sup, ModTabs.BLOCKS.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
        }
        public static void pat() {}
    }
    public static final WrappedBlockRegistry PRESS = new BlockBuilder("press", ()->new BlockPress(Properties.of())).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(press -> new ItemBlockCustomModel(press, new Item.Properties(), 3)).build();
    public static final WrappedBlockRegistry PRESS_PREHEATER = machine("press_preheater", ()->new BlockBase(Properties.of()), HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);
    public static class Machine{
        public static final WrappedBlockRegistry PWR_CONTROLLER = ModBlocks.add("pwr_controller", ()->new BlockPWRController(Properties.of()), ModTabs.MACHINE.getKey(), HBMKey.MODEL_FRONT_SIDE,HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
        public static final WrappedBlockRegistry DIFURNACE = add("furnace_blast", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
        public static final WrappedBlockRegistry FURNACE_ELECTRIC = add("furnace_electric", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
        public static final WrappedBlockRegistry BOILER = add("boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
        public static final WrappedBlockRegistry BOILER_ELECTRIC = add("boiler_electric", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
        public static final WrappedBlockRegistry BOILER_NUCLEAR = add("boiler_nuclear", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))), HBMKey.MODEL_DIFURNACE, HBMKey.DROP_SELF);
//        public static final WrappedBlockRegistry PRESS = new BlockBuilder("press", ()->new BlockPress(Properties.of())).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(press -> new ItemBlockCustomModel(press, new Item.Properties(), 3)).build();
//        public static final WrappedBlockRegistry BATTERY = new BlockBuilder("battery", () -> new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block,new Item.Properties())).build();
//        public static final WrappedBlockRegistry BATTERY_LITHIUM = new BlockBuilder("battery_lithium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block,new Item.Properties())).build();
//        public static final WrappedBlockRegistry BATTERY_SCHRABIDIUM = new BlockBuilder("battery_schrabidium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block,new Item.Properties())).build();
//        public static final WrappedBlockRegistry BATTERY_DINEUTRONIUM = new BlockBuilder("battery_dineutronium",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM)).model(HBMKey.MODEL_FRONT_SIDE_TOP).tab(ModTabs.MACHINE.getKey()).item(block -> new BatteryBlockItem(block,new Item.Properties())).build();
        public static final WrappedBlockRegistry ANVIL_IRON = new BlockBuilder("anvil_iron",()->new BlockAnvil(Properties.of())).model(HBMKey.MODEL_EXISTING_FILE).tab(ModTabs.MACHINE.getKey()).loc("Tier 1 (iron) Anvil").build();
        public static final WrappedBlockRegistry ANVIL_DESH = new BlockBuilder("anvil_desh",()->new BlockAnvil(Properties.of())).model(HBMKey.MODEL_EXISTING_FILE).tab(ModTabs.MACHINE.getKey()).loc("Tier 2 (desh) Anvil").build();
        public static final WrappedBlockRegistry ANVIL_BISMUTH = new BlockBuilder("anvil_bismuth",()->new BlockAnvil(Properties.of())).model(HBMKey.MODEL_EXISTING_FILE).tab(ModTabs.MACHINE.getKey()).loc("Tier 3 (bismuth) Anvil").build();
        public static final WrappedBlockRegistry CRACKING_TOWER = add("cracking_tower",()->new BlockCrackingTower(Properties.of()), HBMKey.MODEL_EXISTING_FILE, HBMKey.DROP_SELF);
//        public static final WrappedBlockRegistry ASSEMBLER = new BlockBuilder("assembler",()->new BlockAssembler(Properties.of())).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(block -> new ItemBlockCustomModel(block, new Item.Properties(), 4)).build();
//        public static final WrappedBlockRegistry CRUCIBLE = new BlockBuilder("crucible",()->new BlockCrucible(Properties.of())).model(HBMKey.MODEL_STANDALONE).loot(HBMKey.DROP_SELF).tab(ModTabs.MACHINE.getKey()).item(block -> new ItemBlockCustomModel(block, new Item.Properties(), 4)).build();
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup){
            return add(name, sup, HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);
        }
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
            return ModBlocks.add(name, sup, ModTabs.MACHINE.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
        }

        public static void pat() {}
    }
    public static class Nuke{
        public static final WrappedBlockRegistry BOMB_FAT_MAN = ModBlocks.add("bomb_fat_man", () -> new NukeFat(BlockBehaviour.Properties.copy(Blocks.STONE), 200), ModTabs.NUKE.getKey(), HBMKey.MODEL_EXISTING_FILE, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, HBMKey.DROP_SELF);
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup){
            return add(name, sup, HBMKey.MODEL_CUBE_ALL, HBMKey.DROP_SELF);
        }
        public static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
            return ModBlocks.add(name, sup, ModTabs.NUKE.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
        }
        public static void pat() {}
    }
    //机械
//    public static final RegistryObject<Block> machine_difurnace = registerBlockWithItem("machine_difurnace", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))));
//    public static final RegistryObject<Block> machine_electric_furnace = registerBlockWithItem("machine_electric_furnace", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))));
//    public static final RegistryObject<Block> machine_boiler = registerBlockWithItem("machine_boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))));
//    public static final RegistryObject<Block> machine_electric_boiler = registerBlockWithItem("machine_electric_boiler", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))));
//    public static final RegistryObject<Block> machine_nuclear_boiler = registerBlockWithItem("machine_nuclear_boiler", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))));
//    public static final RegistryObject<Block> machine_press = registerBlockWithItem("machine_press", ()->new BlockPress(Properties.of()));
    public static final RegistryObject<Block> machine_battery = registerBattery("machine_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC));
    public static final RegistryObject<Block> machine_lithium_battery = registerBattery("machine_lithium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerBattery("machine_schrabidium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerBattery("machine_dineutronium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM));
//    public static final RegistryObject<Block> anvil_iron = registerBlockWithItem("anvil_iron",()->new BlockAnvil(Properties.of()));
//    public static final RegistryObject<Block> anvil_desh = registerBlockWithItem("anvil_desh",()->new BlockAnvil(Properties.of()));
//    public static final RegistryObject<Block> anvil_bismuth = registerBlockWithItem("anvil_bismuth",()->new BlockAnvil(Properties.of()));
//    public static final RegistryObject<Block> machine_cracking_tower = registerBlockWithItem("machine_cracking_tower",()->new BlockCrackingTower(Properties.of()));
    public static final RegistryObject<Block> machine_assembler = registerBlockWithItem("machine_assembler",()->new BlockAssembler(Properties.of()));
    public static final RegistryObject<Block> machine_crucible = registerBlockWithItem("machine_crucible",()->new BlockCrucible(Properties.of()));
    //模型部分（仅仅用于加载模型渲染，而不会在游戏单独出现，名称以part开头）
    public static final RegistryObject<Block> part_press_head = BLOCKS.register("part_press_head",()->new Block(Properties.of().noLootTable()));
    //电力
    public static final RegistryObject<Block> RED_CABLE = registerBlockWithItem("red_cable",()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)));
    //输送带
    public static final RegistryObject<Block> conveyor = registerBlockWithItem("conveyor",()->new BlockConveyor(Properties.of()));
    //炸弹
    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(),120));
//    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(),200));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(Properties.of(),350));
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
    public static final WrappedBlockRegistry GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final WrappedBlockRegistry GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);

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
        Ores.pat();
        Machine.pat();
        Nuke.pat();
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

    protected static WrappedBlockRegistry machine(final String name, final Supplier<? extends Block> sup, String genModelWay, String lootWay){
        return ModBlocks.add(name, sup, ModTabs.MACHINE.getKey(), genModelWay, HBMKey.ORDERLY_GEN_EXCEPT_FIRST, lootWay);
    }

    protected static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.BASIC_MODEL, genNameWay, HBMKey.DROP_SELF, null);
    }
    protected static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay){
        return add(name, sup, tabKey, genModelWay, genNameWay, lootWay, null);
    }
    protected static WrappedBlockRegistry add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay, String localizedName){
        return new BlockBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).loot(lootWay).build();
    }
}
