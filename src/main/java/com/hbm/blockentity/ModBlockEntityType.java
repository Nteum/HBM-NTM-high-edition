package com.hbm.blockentity;

import com.hbm.block.HBMMachine;
import com.hbm.block.base.BlockDummyable;
import com.hbm.block.env.GlyphidSpawner;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.*;
import com.hbm.HBM;
import com.hbm.blockentity.base.DummibleBlockEntity;
import com.hbm.blockentity.tools.TileEntityGeiger;
import com.hbm.blockentity.weapon.*;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

public class ModBlockEntityType {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HBM.MODID);

    public static final RegistryObject<BlockEntityType<DifurnaceEntity>> DIFURNACE_ENTITY =
            REGISTER.register("difurnace_entity",()-> BlockEntityType.Builder.of(DifurnaceEntity::new, ModBlocks.DIFURNACE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricFurnaceEntity>> ELECTRIC_FURNACE_ENTITY =
            REGISTER.register("electric_furnace_entity",()-> BlockEntityType.Builder.of(ElectricFurnaceEntity::new, ModBlocks.FURNACE_ELECTRIC.get()).build(null));
    public static final RegistryObject<BlockEntityType<BoilerEntity>> BOILER_ENTITY =
            REGISTER.register("boiler_entity",()-> BlockEntityType.Builder.of(BoilerEntity::new, ModBlocks.BOILER.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricBoilerEntity>> ELECTRIC_BOILER_ENTITY =
            REGISTER.register("electric_boiler_entity",()-> BlockEntityType.Builder.of(ElectricBoilerEntity::new, ModBlocks.BOILER_ELECTRIC.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearBoilerEntity>> NUCLEAR_BOILER_ENTITY =
            REGISTER.register("nuclear_boiler_entity",()-> BlockEntityType.Builder.of(NuclearBoilerEntity::new, ModBlocks.BOILER_NUCLEAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<PressEntity>> PRESS_ENTITY =
            REGISTER.register("press_entity",()-> BlockEntityType.Builder.of(PressEntity::new, ModBlocks.PRESS.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombFatEntity>> NUKE_BOMB_FAT_ENTITY =
            REGISTER.register("nuke_bomb_fatman",()-> BlockEntityType.Builder.of(NukeBombFatEntity::new, ModBlocks.BOMB_FAT_MAN.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombBoyEntity>> NUKE_BOMB_BOY_ENTITY =
            REGISTER.register("nuke_bomb_boy",()-> BlockEntityType.Builder.of(NukeBombBoyEntity::new, ModBlocks.BOMB_BOY.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombCustomEntity>> NUKE_BOMB_CUSTOM_ENTITY =
            REGISTER.register("nuke_bomb_custom",()-> BlockEntityType.Builder.of(NukeBombCustomEntity::new, ModBlocks.BOMB_CUSTOM.get()).build(null));
    public static final RegistryObject<BlockEntityType<AssemblerEntity>> ASSEMBLER_ENTITY =
            REGISTER.register("assembler_entity",()-> BlockEntityType.Builder.of(AssemblerEntity::new, ModBlocks.ASSEMBLER.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrucibleEntity>> CRUCIBLE_ENTITY =
            REGISTER.register("crucible_entity",()-> BlockEntityType.Builder.of(CrucibleEntity::new, ModBlocks.CRUCIBLE.get()).build(null));
//    public static final RegistryObject<BlockEntityType<BedRockOre.BedRockOreEntity>> BEDROCK_ORE_ENTITY =
//            REGISTER.register("bedrock_ore_entity",()-> BlockEntityType.Builder.of(BedRockOre.BedRockOreEntity::new, ModBlocks.BEDROCK_ORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DummibleBlockEntity>> DUMMIBLEBLOCK =
            REGISTER.register("dummible_block_entity",()-> BlockEntityType.Builder.of(DummibleBlockEntity::new, ModBlocks.DUMMIBLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableEntity>> CABLE_ENTITY =
            REGISTER.register("cable_entity",()-> BlockEntityType.Builder.of(CableEntity::new, ModBlocks.RED_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PipeEntity>> PIPE_ENTITY =
            REGISTER.register("pipe_entity",()-> BlockEntityType.Builder.of(PipeEntity::new, HBMMachine.FLUID_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryEntity>> BATTERY_ENTITY =
            REGISTER.register("battery_entity",()-> BlockEntityType.Builder.of(BatteryEntity::new, ModBlocks.BATTERY.get(),ModBlocks.BATTERY_LITHIUM.get(),ModBlocks.BATTERY_SCHRABIDIUM.get(),ModBlocks.BATTERY_DINEUTRONIUM.get()).build(null));
    public static final RegistryObject<BlockEntityType<LaunchPadTileEntity>> LAUNCHPAD_ENTITY =
            REGISTER.register("launchpad_entity",()-> BlockEntityType.Builder.of(LaunchPadTileEntity::new, HBMMachine.LAUNCH_PAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChemplantEntity>> CHEMPLANT_ENTITY =
            REGISTER.register("chemplant_entity",()-> BlockEntityType.Builder.of(ChemplantEntity::new, HBMMachine.CHEMPLANT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BarrelEntity>> BARREL_ENTITY =
            REGISTER.register("barrel_entity",()-> BlockEntityType.Builder.of(BarrelEntity::new, HBMMachine.PLASTIC_BARREL.get(),HBMMachine.CORRODED_BARREL.get(),HBMMachine.IRON_BARREL.get(),HBMMachine.STEEL_BARREL.get(),HBMMachine.TCALLOY_BARREL.get(),HBMMachine.ANTIMATTER_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileProxyCombo>> PROXY_ENTITY =
            REGISTER.register("proxy_entity",()-> BlockEntityType.Builder.of(TileProxyCombo::new,
                    ModBlocks.CRUCIBLE.get(), ModBlocks.ASSEMBLER.get(), ModBlocks.CRACKING_TOWER.get(), HBMMachine.CHEMPLANT.get(), HBMMachine.LAUNCH_PAD.get(), ModBlocks.BOMB_BOY.get(), ModBlocks.BOMB_CUSTOM.get(), ModBlocks.BOMB_FAT_MAN.get()
//                    ForgeRegistries.BLOCKS.getValues().toArray(Block[]::new)
//                    BuiltInRegistries.BLOCK.stream().filter(block -> block.builtInRegistryHolder().is(ModTags.Blocks.MACHINE)).toArray(Block[]::new)
            ).build(null));
    public static final RegistryObject<BlockEntityType<TileEntityGeiger>> GEIGER_COUNTER =
            REGISTER.register("geiger_counter",()-> BlockEntityType.Builder.of(TileEntityGeiger::new, HBMMachine.GEIGER_COUNTER.get()).build(null));
    public static final RegistryObject<BlockEntityType<GlyphidSpawner.GlyphidSpawnerEntity>> GLYPHID_SPAWNER =
            REGISTER.register("glyphid_spawner",()-> BlockEntityType.Builder.of(GlyphidSpawner.GlyphidSpawnerEntity::new, ModBlocks.GLYPHID_SPAWNER.get()).build(null));
}
