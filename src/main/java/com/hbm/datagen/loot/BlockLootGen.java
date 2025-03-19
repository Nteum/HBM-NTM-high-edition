package com.hbm.datagen.loot;

import com.google.common.collect.Iterables;
import com.hbm.registries.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.Set;

/**
 * 原版方块掉落在VanillaBlockLoot
 * */
public class BlockLootGen extends BlockLootSubProvider {

    public BlockLootGen() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        //机器
        this.dropSelf(ModBlocks.machine_difurnace.get());
        this.dropSelf(ModBlocks.machine_electric_furnace.get());
        this.dropSelf(ModBlocks.machine_boiler.get());
        this.dropSelf(ModBlocks.machine_electric_boiler.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());
        this.dropSelf(ModBlocks.machine_press.get());
        this.dropSelf(ModBlocks.machine_battery.get());
        this.dropSelf(ModBlocks.machine_lithium_battery.get());
        this.dropSelf(ModBlocks.machine_schrabidium_battery.get());
        this.dropSelf(ModBlocks.machine_dineutronium_battery.get());
        this.dropSelf(ModBlocks.anvil_iron.get());
        this.dropSelf(ModBlocks.anvil_desh.get());
        this.dropSelf(ModBlocks.anvil_bismuth.get());
        this.dropSelf(ModBlocks.machine_cracking_tower.get());
        this.dropSelf(ModBlocks.machine_assembler.get());
        this.dropSelf(ModBlocks.machine_crucible.get());
        this.dropSelf(ModBlocks.RED_CABLE.get());
        this.dropSelf(ModBlocks.conveyor.get());
        this.dropSelf(ModBlocks.bomb_fat_man.get());
        this.dropSelf(ModBlocks.bomb_custom.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());
        //矿石
        this.dropOther(ModBlocks.WAST_EARTH.get(), Blocks.DIRT);
        this.dropSelf(ModBlocks.URANIUM_ORE.get());
        this.dropSelf(ModBlocks.DEEPSLATE_URANIUM_ORE.get());
        this.dropSelf(ModBlocks.SCORCHED_URANIUM_ORE.get());
        this.dropSelf(ModBlocks.TITANIUM_ORE.get());
        this.dropSelf(ModBlocks.THORIUM_ORE.get());
        this.dropSelf(ModBlocks.NITER_ORE.get());
        this.dropSelf(ModBlocks.TUNGSTEN_ORE.get());
        this.dropSelf(ModBlocks.ALUMINIUM_ORE.get());
        this.dropSelf(ModBlocks.FLUORITE_ORE.get());
        this.dropSelf(ModBlocks.LEAD_ORE.get());
        this.dropSelf(ModBlocks.BERYLLIUM_ORE.get());
        this.dropSelf(ModBlocks.SA326_ORE.get());
        this.dropSelf(ModBlocks.ASBESTOS_BLOCK.get());
        this.dropSelf(ModBlocks.ASBESTOS_ORE.get());
        this.dropSelf(ModBlocks.BASALT_ASBESTOS_ORE.get());
        this.dropSelf(ModBlocks.OIL_ORE.get());
        this.dropSelf(ModBlocks.OIL_ORE_EMPTY.get());
        this.dropSelf(ModBlocks.OIL_ORE_SAND.get());
        this.dropSelf(ModBlocks.RARE_EARTH_ORE.get());
        this.dropSelf(ModBlocks.DEEPSLATE_RARE_EARTH_ORE.get());
        this.dropSelf(ModBlocks.LITHIUM_ORE.get());
        this.dropSelf(ModBlocks.COBALT_ORE.get());
        this.dropSelf(ModBlocks.COLTAN_ORE.get());
        this.dropSelf(ModBlocks.GENISS_GAS_ORE.get());
        this.dropSelf(ModBlocks.SMOLDER_ORE_NETHER.get());
        this.dropSelf(ModBlocks.PLUTONIUM_ORE_NETHER.get());
        this.dropSelf(ModBlocks.FIRE_ORE_NETHER.get());
        this.dropSelf(ModBlocks.TIKITE_ORE_END.get());
        this.dropSelf(ModBlocks.BEDROCK_ORE.get());
        this.dropSelf(ModBlocks.DEPTH_STONE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
        return Iterables.transform(ModBlocks.BLOCKS.getEntries(), RegistryObject::get);
    }
}
