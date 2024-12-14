package com.hbm.datagen.loot;

import com.google.common.collect.Iterables;
import com.hbm.registries.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class BlockLootGen extends BlockLootSubProvider {
    protected BlockLootGen(Set<Item> pExplosionResistant, FeatureFlagSet pEnabledFeatures) {
        super(pExplosionResistant, pEnabledFeatures);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.URANIUM_ORE.get());
        this.dropSelf(ModBlocks.machine_difurnace.get());
        this.dropSelf(ModBlocks.machine_boiler.get());
        this.dropSelf(ModBlocks.machine_electric_boiler.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());
        this.dropSelf(ModBlocks.machine_battery.get());
        this.dropSelf(ModBlocks.machine_lithium_battery.get());
        this.dropSelf(ModBlocks.machine_schrabidium_battery.get());
        this.dropSelf(ModBlocks.machine_dineutronium_battery.get());

        this.dropOther(ModBlocks.WAST_EARTH.get(), Blocks.DIRT);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
        return Iterables.transform(ModBlocks.BLOCKS.getEntries(), RegistryObject::get);
    }
}
