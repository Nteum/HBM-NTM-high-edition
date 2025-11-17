package com.hbm.datagen.loot;

import com.google.common.collect.Iterables;
import com.hbm.block.HBMBlockComponent;
import com.hbm.block.HBMMachine;
import com.hbm.registries.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
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
//        generateMachineLoot();
        ModBlocks.lootSupport(this);
        HBMMachine.lootable(this);
        HBMBlockComponent.lootable(this);
        //机器
        this.dropSelf(ModBlocks.machine_difurnace.get());
        this.dropSelf(ModBlocks.machine_electric_furnace.get());
        this.dropSelf(ModBlocks.machine_boiler.get());
        this.dropSelf(ModBlocks.machine_electric_boiler.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());
        this.dropSelf(ModBlocks.machine_press.get());
        this.dropSelf(ModBlocks.machine_shredder.get());
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
        this.dropSelf(ModBlocks.machine_rbmk_base.get());
        this.dropSelf(ModBlocks.machine_rbmk_heater.get());
        this.dropSelf(ModBlocks.RED_CABLE.get());
        this.dropSelf(ModBlocks.conveyor.get());
        this.dropSelf(ModBlocks.bomb_boy.get());
        this.dropSelf(ModBlocks.bomb_fat_man.get());
        this.dropSelf(ModBlocks.bomb_custom.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());

        //饰品
        this.dropSelf(ModBlocks.TEST12.get());
    }
//    public void generateMachineLoot(){
//        this.dropSelf(HBMMachine.CHEMPLANT.get());
//        this.dropSelf(HBMMachine.PLASTIC_BARREL.get());
//        this.dropSelf(HBMMachine.CORRODED_BARREL.get());
//        this.dropSelf(HBMMachine.IRON_BARREL.get());
//        this.dropSelf(HBMMachine.STEEL_BARREL.get());
//        this.dropSelf(HBMMachine.TCALLOY_BARREL.get());
//        this.dropSelf(HBMMachine.ANTIMATTER_BARREL.get());
//        this.dropSelf(HBMMachine.FLUID_PIPE.get());
//    }

    @Override
    public void dropSelf(Block pBlock) {
        super.dropSelf(pBlock);
    }

    @Override
    public void dropOther(Block pBlock, ItemLike pItem) {
        super.dropOther(pBlock, pItem);
    }

    @Override
    public void add(Block pBlock, LootTable.Builder pBuilder){
        super.add(pBlock, pBuilder);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
        return Iterables.transform(ModBlocks.BLOCKS.getEntries(), RegistryObject::get);
    }
}
