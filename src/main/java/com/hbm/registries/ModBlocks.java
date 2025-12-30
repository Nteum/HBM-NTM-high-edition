package com.hbm.registries;

import com.hbm.HBMKey;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMBlockProperties;
import com.hbm.block.HBMMachine;
import com.hbm.block.decoriate.BlockTest12;
import com.hbm.block.env.*;
import com.hbm.block.logistic.BlockCable;
import com.hbm.block.machine.*;
import com.hbm.block.machine.tokamak.*;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKFuelChannel;
import com.hbm.block.machine.rbmk.BlockRBMKHeater;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheral;
import com.hbm.block.logistic.BlockConveyor;
import com.hbm.block.weapon.*;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.item.HBMItems;
import com.hbm.item.blockitem.IronCrateItem;
import com.hbm.item.blockitem.SteelCrateItem;
import com.hbm.datagen.LanguageProvider;
import com.hbm.datagen.loot.BlockLootGen;
import com.hbm.datagen.model.BlockStateGen;
import com.hbm.datagen.model.ItemModelGen;
import com.hbm.item.tool.BatteryBlockItem;
import com.hbm.block.base.DummibleBlock;
import com.hbm.registries.WrapperRegistry.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
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
                    .item(block -> new SteelCrateItem(block, new Item.Properties().stacksTo(1)))
                    .build();
    //炸弹
    public static final RegistryObject<Block> bomb_boy = registerBlockWithItem("bomb_boy",()->new NukeBoy(Properties.of(),120));
    public static final RegistryObject<Block> bomb_fat_man = registerBlockWithItem("bomb_fat_man",()->new NukeFat(Properties.of(),200));
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
    public static final RegistryObject<Block> GLYPHID_BLOCK = add("glyphid_block", ()->new GlyphidBlock(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModCreativeModeTab.HBM_BLOCK.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);
    public static final RegistryObject<Block> GLYPHID_SPAWNER = add("glyphid_spawner", ()->new GlyphidSpawner(Properties.copy(Blocks.STONE).pushReaction(PushReaction.IGNORE).explosionResistance(0.5f)), ModCreativeModeTab.HBM_BLOCK.getKey(), HBMKey.MODEL_STANDALONE, HBMKey.ORDERLY_GEN, HBMKey.DROP_NONE);

    public static ToIntFunction<BlockState> litEmission(int value){
        return state -> {
            return state.getValue(BlockStateProperties.LIT)?value:0;
        };
    }

//    public static void registerItem(){
//        //主要用于注册一些特殊模型的物品
//        ModItems.ITEMS.register("bomb_fat_man",()->new NukeFat.NukeItem(bomb_fat_man.get(),new Item.Properties()));
//    }
    public static RegistryObject<Block> registerBattery(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        HBMItems.ITEMS.register(name,()->new BatteryBlockItem(block.get(),new Item.Properties()));
        return block;
    }
    public static RegistryObject<Block> registerBlockWithItem(final String name, final Supplier<? extends Block> blocksup){
        RegistryObject<Block> block = BLOCKS.register(name,blocksup);
        HBMItems.ITEMS.register(name,()->new BlockItem(block.get(),new Item.Properties()));
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
