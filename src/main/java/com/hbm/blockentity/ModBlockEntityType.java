package com.hbm.blockentity;

import com.hbm.block.HBMMachine;
import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.base2.TileProxyCombo;
import com.hbm.blockentity.machine.*;
import com.hbm.HBM;
import com.hbm.blockentity.base.DummibleBlockEntity;
import com.hbm.blockentity.weapon.LaunchPadEntity;
import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import com.hbm.blockentity.weapon.NukeBombCustomEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
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
            REGISTER.register("difurnace_entity",()-> BlockEntityType.Builder.of(DifurnaceEntity::new, ModBlocks.machine_difurnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<PressEntity>> PRESS_ENTITY =
            REGISTER.register("press_entity",()-> BlockEntityType.Builder.of(PressEntity::new, ModBlocks.machine_press.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombFatEntity>> NUKE_BOMB_FAT_ENTITY =
            REGISTER.register("nuke_bomb_entity",()-> BlockEntityType.Builder.of(NukeBombFatEntity::new, ModBlocks.bomb_fat_man.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombBoyEntity>> NUKE_BOMB_BOY_ENTITY =
            REGISTER.register("nuke_bomb_boy",()-> BlockEntityType.Builder.of(NukeBombBoyEntity::new, ModBlocks.bomb_boy.get()).build(null));
    public static final RegistryObject<BlockEntityType<NukeBombCustomEntity>> NUKE_BOMB_CUSTOM_ENTITY =
            REGISTER.register("nuke_bomb_custom",()-> BlockEntityType.Builder.of(NukeBombCustomEntity::new, ModBlocks.bomb_custom.get()).build(null));
    public static final RegistryObject<BlockEntityType<AssemblerEntity>> ASSEMBLER_ENTITY =
            REGISTER.register("assembler_entity",()-> BlockEntityType.Builder.of(AssemblerEntity::new, ModBlocks.machine_assembler.get()).build(null));
    public static final RegistryObject<BlockEntityType<CrucibleEntity>> CRUCIBLE_ENTITY =
            REGISTER.register("crucible_entity",()-> BlockEntityType.Builder.of(CrucibleEntity::new, ModBlocks.machine_crucible.get()).build(null));
//    public static final RegistryObject<BlockEntityType<BedRockOre.BedRockOreEntity>> BEDROCK_ORE_ENTITY =
//            REGISTER.register("bedrock_ore_entity",()-> BlockEntityType.Builder.of(BedRockOre.BedRockOreEntity::new, ModBlocks.BEDROCK_ORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DummibleBlockEntity>> DUMMIBLEBLOCK =
            REGISTER.register("dummible_block_entity",()-> BlockEntityType.Builder.of(DummibleBlockEntity::new, ModBlocks.DUMMIBLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableEntity>> CABLE_ENTITY =
            REGISTER.register("cable_entity",()-> BlockEntityType.Builder.of(CableEntity::new, ModBlocks.RED_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<PipeEntity>> PIPE_ENTITY =
            REGISTER.register("pipe_entity",()-> BlockEntityType.Builder.of(PipeEntity::new, HBMMachine.FLUID_PIPE.get()).build(null));
    public static final RegistryObject<BlockEntityType<BatteryEntity>> BATTERY_ENTITY =
            REGISTER.register("battery_entity",()-> BlockEntityType.Builder.of(BatteryEntity::new, ModBlocks.machine_battery.get(),ModBlocks.machine_lithium_battery.get(),ModBlocks.machine_dineutronium_battery.get(),ModBlocks.machine_schrabidium_battery.get()).build(null));
    public static final RegistryObject<BlockEntityType<LaunchPadEntity>> LAUNCHPAD_ENTITY =
            REGISTER.register("launchpad_entity",()-> BlockEntityType.Builder.of(LaunchPadEntity::new, ModBlocks.RED_CABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ElectricFurnaceEntity>> ELECTRIC_FURNACE_ENTITY =
            REGISTER.register("electric_furnace_entity",()-> BlockEntityType.Builder.of(ElectricFurnaceEntity::new, ModBlocks.machine_electric_furnace.get()).build(null));
    public static final RegistryObject<BlockEntityType<BoilerEntity>> BOILER_ENTITY =
            REGISTER.register("boiler_entity",()-> BlockEntityType.Builder.of(BoilerEntity::new, ModBlocks.machine_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ElectricBoilerEntity>> ELECTRIC_BOILER_ENTITY =
            REGISTER.register("electric_boiler_entity",()-> BlockEntityType.Builder.of(ElectricBoilerEntity::new, ModBlocks.machine_electric_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<NuclearBoilerEntity>> NUCLEAR_BOILER_ENTITY =
            REGISTER.register("nuclear_boiler_entity",()-> BlockEntityType.Builder.of(NuclearBoilerEntity::new, ModBlocks.machine_nuclear_boiler.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChemplantEntity>> CHEMPLANT_ENTITY =
            REGISTER.register("chemplant_entity",()-> BlockEntityType.Builder.of(ChemplantEntity::new, HBMMachine.CHEMPLANT.get()).build(null));
    public static final RegistryObject<BlockEntityType<BarrelEntity>> BARREL_ENTITY =
            REGISTER.register("barrel_entity",()-> BlockEntityType.Builder.of(BarrelEntity::new, HBMMachine.PLASTIC_BARREL.get(),HBMMachine.CORRODED_BARREL.get(),HBMMachine.IRON_BARREL.get(),HBMMachine.STEEL_BARREL.get(),HBMMachine.TCALLOY_BARREL.get(),HBMMachine.ANTIMATTER_BARREL.get()).build(null));
    public static final RegistryObject<BlockEntityType<TileProxyCombo>> PROXY_ENTITY =
            REGISTER.register("proxy_entity",()-> BlockEntityType.Builder.of(TileProxyCombo::new,
                    ModBlocks.machine_crucible.get(), ModBlocks.machine_assembler.get(), ModBlocks.machine_cracking_tower.get(), HBMMachine.CHEMPLANT.get()
//                    ForgeRegistries.BLOCKS.getValues().toArray(Block[]::new)
//                    BuiltInRegistries.BLOCK.stream().filter(block -> block.builtInRegistryHolder().is(ModTags.Blocks.MACHINE)).toArray(Block[]::new)
            ).build(null));
}
