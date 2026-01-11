package com.hbm.registries;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.HBMMachine;
import com.hbm.block.base.BlockBase;
import com.hbm.block.base.DummibleBlock;
import com.hbm.block.decoriate.BlockMolten;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.GlyphidBlock;
import com.hbm.block.env.GlyphidSpawner;
import com.hbm.block.logistic.BlockCable;
import com.hbm.block.logistic.BlockConveyor;
import com.hbm.block.machine.*;
import com.hbm.block.machine.rbmk.*;
import com.hbm.block.machine.tokamak.*;
import com.hbm.block.weapon.NukeBoy;
import com.hbm.block.weapon.NukeCustom;
import com.hbm.block.weapon.NukeFat;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.item.blockitem.IronCrateItem;
import com.hbm.item.blockitem.SteelCrateItem;
import com.hbm.item.tool.BatteryBlockItem;
import com.hbm.registries.WrapperRegistry.BlockBuilder;
import com.hbm.registries.WrapperRegistry.WrappedBlockRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
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
    //机械
    public static final RegistryObject<Block> machine_difurnace = registerBlockWithItem("machine_difurnace", ()->new BlockDifurnace(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_furnace = registerBlockWithItem("machine_electric_furnace", ()->new BlockElectricFurnace(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_boiler = registerBlockWithItem("machine_boiler", ()->new BlockBoiler(Properties.of().lightLevel(litEmission(13))));
    public static final RegistryObject<Block> machine_electric_boiler = registerBlockWithItem("machine_electric_boiler", ()->new BlockElectricBoiler(Properties.of().lightLevel(litEmission(14))));
    public static final RegistryObject<Block> machine_nuclear_boiler = registerBlockWithItem("machine_nuclear_boiler", ()->new BlockNuclearBoiler(Properties.of().lightLevel(litEmission(15))));
    public static final RegistryObject<Block> machine_press = registerBlockWithItem("machine_press", ()->new BlockPress(Properties.of()));
    public static final RegistryObject<Block> machine_shredder = registerBlockWithItem("machine_shredder", ()->new BlockShredder(Properties.of()));
    public static final RegistryObject<Block> machine_wood_burner = registerBlockWithItem("machine_wood_burner",
            () -> new WoodBurnerBlock(Properties.of().strength(3.0F).sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(WoodBurnerBlock.LIT) ? 13 : 0)));
    // Tokamak 聚变堆组件
    public static final RegistryObject<Block> tokamak_controller = registerBlockWithItem("tokamak_controller", ()->new TokamakControllerBlock(Properties.of().strength(5.0F).lightLevel(state -> 8)));
    public static final RegistryObject<Block> tokamak_casing = registerBlockWithItem("tokamak_casing", ()->new TokamakCasingBlock(Properties.of().strength(6.0F).explosionResistance(18.0F)));
    public static final RegistryObject<Block> tokamak_coil = registerBlockWithItem("tokamak_coil", ()->new TokamakCoilBlock(Properties.of().strength(5.0F).lightLevel(state -> state.getValue(TokamakCoilBlock.STRENGTH) * 2)));
    public static final RegistryObject<Block> tokamak_heater = registerBlockWithItem("tokamak_heater", ()->new TokamakHeaterBlock(Properties.of().strength(5.0F).lightLevel(state -> state.getValue(TokamakHeaterBlock.ACTIVE) ? 12 : 0)));
    public static final RegistryObject<Block> tokamak_injector = registerBlockWithItem("tokamak_injector", ()->new TokamakInjectorBlock(Properties.of().strength(4.0F)));
    public static final RegistryObject<Block> tokamak_port = registerBlockWithItem("tokamak_port", ()->new TokamakPortBlock(Properties.of().strength(4.0F)));
    public static final RegistryObject<Block> machine_battery = registerBattery("machine_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.BASIC));
    public static final RegistryObject<Block> machine_lithium_battery = registerBattery("machine_lithium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.LITHIUM));
    public static final RegistryObject<Block> machine_schrabidium_battery = registerBattery("machine_schrabidium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.SCHRABIDIUM));
    public static final RegistryObject<Block> machine_dineutronium_battery = registerBattery("machine_dineutronium_battery",()->new BlockBattery(Properties.of(), BlockBattery.BatteryType.DINEUTRONIUM));
    public static final RegistryObject<Block> anvil_iron = registerBlockWithItem("anvil_iron",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> anvil_desh = registerBlockWithItem("anvil_desh",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> anvil_bismuth = registerBlockWithItem("anvil_bismuth",()->new BlockAnvil(Properties.of()));
    public static final RegistryObject<Block> machine_cracking_tower = registerBlockWithItem("machine_cracking_tower",()->new BlockCrackingTower(Properties.of()));
    public static final RegistryObject<Block> machine_assembler = registerBlockWithItem("machine_assembler",()->new BlockAssembler(Properties.of()));
    public static final RegistryObject<Block> machine_crucible = registerBlockWithItem("machine_crucible",()->new BlockCrucible(Properties.of()));
    public static final RegistryObject<Block> machine_rbmk_base = registerBlockWithItem("machine_rbmk_base", () -> new BlockRBMKBase(Properties.of().strength(6.0F).explosionResistance(30.0F)));
    public static final RegistryObject<Block> machine_rbmk_heater = registerBlockWithItem("machine_rbmk_heater", () -> new BlockRBMKHeater(Properties.of().strength(4.0F).explosionResistance(12.0F).lightLevel(state -> state.getValue(BlockRBMKHeater.LIT) ? 8 : 0)));
    public static final RegistryObject<Block> machine_rbmk_fuel_channel = registerBlockWithItem("machine_rbmk_fuel_channel", () -> new BlockRBMKFuelChannel(Properties.of().strength(4.0F).explosionResistance(12.0F)));
    public static final RegistryObject<Block> machine_rbmk_control_rod = registerBlockWithItem("machine_rbmk_control_rod", () -> new BlockRBMKControlRod(Properties.of().strength(4.0F).explosionResistance(12.0F)));
    public static final RegistryObject<Block> machine_rbmk_console = registerBlockWithItem("machine_rbmk_console", () -> new BlockRBMKPeripheral(Properties.of().strength(4.0F).explosionResistance(16.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.CONSOLE));
    public static final RegistryObject<Block> machine_rbmk_element = registerBlockWithItem("machine_rbmk_element", () -> new BlockRBMKPeripheral(Properties.of().strength(4.0F).explosionResistance(16.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.ELEMENT));
    public static final RegistryObject<Block> machine_rbmk_reflector = registerBlockWithItem("machine_rbmk_reflector", () -> new BlockRBMKPeripheral(Properties.of().strength(5.0F).explosionResistance(20.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.REFLECTOR));
    public static final RegistryObject<Block> machine_rbmk_debris = registerBlockWithItem("machine_rbmk_debris", () -> new BlockRBMKPeripheral(Properties.of().strength(3.0F).explosionResistance(8.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.DEBRIS));
    public static final RegistryObject<Block> machine_rbmk_crane_console = registerBlockWithItem("machine_rbmk_crane_console", () -> new BlockRBMKPeripheral(Properties.of().strength(4.0F).explosionResistance(12.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.CRANE_CONSOLE));
    public static final RegistryObject<Block> machine_rbmk_autoloader = registerBlockWithItem("machine_rbmk_autoloader", () -> new BlockRBMKPeripheral(Properties.of().strength(4.0F).explosionResistance(12.0F), com.hbm.reactor.rbmk.RBMKPeripheralType.AUTOLOADER));
    //模型部分（仅仅用于加载模型渲染，而不会在游戏单独出现，名称以part开头）
    public static final RegistryObject<Block> part_press_head = BLOCKS.register("part_press_head",()->new Block(Properties.of().noLootTable()));
    //电力
    public static final RegistryObject<Block> RED_CABLE = registerBlockWithItem("red_cable",()->new BlockCable(Properties.copy(Blocks.STONE_BRICK_WALL)));
    //输送带
    public static final RegistryObject<Block> conveyor = registerBlockWithItem("conveyor",()->new BlockConveyor(Properties.of()));
    public static final RegistryObject<Block> crate_iron =
            new BlockBuilder("crate_iron", () -> new IronCrateBlock(Properties.of().strength(3.0F).sound(SoundType.WOOD)))
                    .tab(ModCreativeModeTab.HBM_MACHINE.getKey())
                    .item(block -> new IronCrateItem(block, new Item.Properties().stacksTo(1)))
                    .build();
    public static final RegistryObject<Block> crate_steel =
            new BlockBuilder("crate_steel", () -> new SteelCrateBlock(Properties.of().strength(4.0F).sound(SoundType.METAL)))
                    .tab(ModCreativeModeTab.HBM_MACHINE.getKey())
                    .item(block -> new SteelCrateItem(block, new Item.Properties().stacksTo(1))).loc(HBMKey.REVERSE_GEN)
                    .build();
    //炸弹
    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(),120));
    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(),200));
    public static final RegistryObject<Block> bomb_custom = registerBlockWithItem("bomb_custom",()->new NukeCustom(Properties.of(),350));
    //发射台
    //逻辑物
    public static final RegistryObject<Block> DUMMIBLE = registerBlockWithItem("dummible",()->new DummibleBlock(Properties.copy(Blocks.STONE).noLootTable()));
    //装饰
    public static final RegistryObject<Block> TEST12 = registerBlockWithItem("test12",()->new BlockTest12(Properties.of()));
    // glyphid
    public static final RegistryObject<Block> GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModCreativeModeTab.HBM_BLOCK.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModCreativeModeTab.HBM_BLOCK.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    // 陨石
    public static final RegistryObject<Block> BLOCK_METEOR = add("block_meteor", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Meteorite Block");
    public static final RegistryObject<Block> BLOCK_METEOR_COBBLE = add("block_meteor_cobble", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Meteorite Cobblestone", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> BLOCK_METEOR_BROKEN = add("block_meteor_broken", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> BLOCK_METEOR_MOLTEN = add("block_meteor_molten", () -> new BlockMolten(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Hot Meteorite Cobblestone", HBMKey.DROP_NONE);
    public static final RegistryObject<Block> BLOCK_METEOR_TREASURE = add("block_meteor_treasure", () -> new Block(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, "Broken Meteorite Block", HBMKey.DROP_STANDALONE);
    public static final RegistryObject<Block> METEOR_POLISHED = add("meteor_polished", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Polished Meteor Block");
    public static final RegistryObject<Block> METEOR_BRICK = block("meteor_brick", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)));
    public static final RegistryObject<Block> METEOR_BRICK_MOSSY = add("meteor_brick_mossy", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Mossy Meteor Bricks");
    public static final RegistryObject<Block> METEOR_BRICK_CRACKED = add("meteor_brick_cracked", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Cracked Meteor Bricks");
    public static final RegistryObject<Block> METEOR_BRICK_CHISELED = add("meteor_brick_chiseled", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), "Chiseled Meteor Bricks");
    public static final RegistryObject<Block> METEOR_PILLAR = add("meteor_pillar", () -> new RotatedPillarBlock(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_PILLAR, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
//    public static final RegistryObject<Block> ORE_METEOR_IRON = add("ore_meteor_iron", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> ORE_METEOR_COPPER = add("ore_meteor_copper", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> ORE_METEOR_ALUMINUM = add("ore_meteor_aluminum", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> ORE_METEOR_RAREEARTH = add("ore_meteor_rareearth", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_STANDALONE);
//    public static final RegistryObject<Block> ORE_METEOR_IRON_COBALT = add("ore_meteor_iron_cobalt", () -> new BlockBase(Properties.copy(Blocks.STONE).strength(15, 360)), ModTabs.BLOCKS.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.REVERSE_GEN, HBMKey.DROP_STANDALONE);
    /**
     * 航天版方块
     * */
    public static final RegistryObject<Block> moon_rock = block("moon_rock", ()->new Block(Properties.of().sound(SoundType.STONE).strength(1.5f, 10f)));
    public static final RegistryObject<Block> moon_turf = block("moon_turf", ()->new FallingBlock(Properties.of().sound(SoundType.SAND).strength(0.5f)));
    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> {
            return state.getValue(BlockStateProperties.LIT)?value:0;
        };
    }

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

    protected static RegistryObject<Block> block(final String name, final Supplier<? extends Block> sup){
        return ModBlocks.add(name, sup, ModTabs.BLOCKS.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    }

    protected static RegistryObject<Block> machine(final String name, final Supplier<? extends Block> sup){
        return ModBlocks.add(name, sup, ModTabs.MACHINE.getKey(), HBMKey.MODEL_CUBE_ALL, HBMKey.ORDERLY_GEN, HBMKey.DROP_SELF);
    }

    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genNameWay){
        return add(name, sup, tabKey, HBMKey.MODEL_CUBE_ALL, genNameWay, HBMKey.DROP_SELF);
    }
    protected static RegistryObject<Block> add(final String name, final Supplier<? extends Block> sup, ResourceKey<CreativeModeTab> tabKey, String genModelWay, String genNameWay, String lootWay){
        return new BlockBuilder(name, sup).tab(tabKey).model(genModelWay).loc(genNameWay).loot(lootWay).build();
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
}
