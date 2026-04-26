package com.hbm.datagen.loot;

import com.google.common.collect.Iterables;

import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 原版方块掉落在VanillaBlockLoot
 * */
public class BlockLootGen extends BlockLootSubProvider {


    private final Set<Block> handledBlocks = new HashSet<>();

    public BlockLootGen() {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
//        generateMachineLoot();
        ModBlocks.lootSupport(this);
//        HBMMachine.lootable(this);
//        HBMBlockComponent.lootable(this);
        //机器
        this.dropSelf(ModBlocks.machine_difurnace.get());
        this.dropSelf(ModBlocks.machine_electric_furnace.get());
        this.dropSelf(ModBlocks.machine_boiler.get());
        this.dropSelf(ModBlocks.machine_electric_boiler.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());
        this.dropSelf(ModBlocks.machine_press.get());
        this.dropSelf(ModBlocks.machine_shredder.get());
        this.dropSelf(ModBlocks.machine_wood_burner.get());
        this.dropSelf(ModBlocks.machine_battery.get());
        this.dropSelf(ModBlocks.machine_lithium_battery.get());
        this.dropSelf(ModBlocks.machine_schrabidium_battery.get());
        this.dropSelf(ModBlocks.machine_dineutronium_battery.get());
        this.dropSelf(ModBlocks.anvil_iron.get());
        this.dropSelf(ModBlocks.anvil_desh.get());
        this.dropSelf(ModBlocks.anvil_bismuth.get());
        this.dropSelf(ModBlocks.machine_cracking_tower.get());
        this.dropSelf(ModBlocks.machine_condenser.get());
        this.dropSelf(ModBlocks.machine_cooling_tower.get());
        this.dropSelf(ModBlocks.machine_assembler.get());
        this.dropSelf(ModBlocks.machine_crucible.get());
        this.dropSelf(ModBlocks.machine_rbmk_base.get());
        this.dropSelf(ModBlocks.machine_rbmk_heater.get());
        this.dropSelf(ModBlocks.machine_rbmk_fuel_channel.get());
        this.dropSelf(ModBlocks.machine_rbmk_control_rod.get());
        this.dropSelf(ModBlocks.machine_rbmk_console.get());
        this.dropSelf(ModBlocks.machine_rbmk_element.get());
        this.dropSelf(ModBlocks.machine_rbmk_reflector.get());
        this.dropSelf(ModBlocks.machine_rbmk_debris.get());
        this.dropSelf(ModBlocks.machine_rbmk_crane_console.get());
        this.dropSelf(ModBlocks.machine_rbmk_autoloader.get());
        this.dropSelf(ModBlocks.tokamak_controller.get());
        this.dropSelf(ModBlocks.tokamak_casing.get());
        this.dropSelf(ModBlocks.tokamak_coil.get());
        this.dropSelf(ModBlocks.tokamak_heater.get());
        this.dropSelf(ModBlocks.tokamak_injector.get());
        this.dropSelf(ModBlocks.tokamak_port.get());
        this.dropSelf(ModBlocks.machine_icf.get());
        this.dropSelf(ModBlocks.machine_icf_controller.get());
        this.dropSelf(ModBlocks.machine_icf_press.get());
        this.dropSelf(ModBlocks.machine_research_reactor.get());
        this.dropSelf(ModBlocks.machine_reactor_breeding.get());

        this.dropSelf(ModBlocks.pwr_controller.get());
        this.dropSelf(ModBlocks.pwr_casing.get());
        this.dropSelf(ModBlocks.pwr_port.get());
        this.dropSelf(ModBlocks.pwr_reflector.get());
        this.dropSelf(ModBlocks.pwr_fuel_block.get());
        this.dropSelf(ModBlocks.pwr_control.get());
        this.dropSelf(ModBlocks.pwr_channel.get());
        this.dropSelf(ModBlocks.pwr_heatex.get());
        this.dropSelf(ModBlocks.pwr_heatsink.get());
        this.dropSelf(ModBlocks.pwr_neutron_source.get());
        // Blocks with noLootTable are excluded in getKnownBlocks().
        this.dropSelf(ModBlocks.machine_zirnox.get());

        this.dropSelf(ModBlocks.crate_iron.get());
        this.dropSelf(ModBlocks.crate_steel.get());
        this.dropSelf(ModBlocks.bomb_boy.get());
        this.dropSelf(ModBlocks.bomb_fat_man.get());
        this.dropSelf(ModBlocks.bomb_custom.get());
        this.dropSelf(ModBlocks.machine_nuclear_boiler.get());

        //饰品
        this.dropSelf(ModBlocks.TEST12.get());
        // 单独定义凋落物的方块
        dropStandalone();
        fillMissingLootTables();

        this.map.remove(BuiltInLootTables.EMPTY);   // 删除为空的键，为了避免后续处理报错
    }

    public void dropStandalone(){
        this.dropOther(ModBlocks.BLOCK_METEOR_COBBLE.get(), ModItems.FRAGMENT_METEORITE.get());
        this.dropOther(ModBlocks.BLOCK_METEOR_BROKEN.get(), ModItems.FRAGMENT_METEORITE.get());
        this.add(ModBlocks.BLOCK_METEOR_TREASURE.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1, 4))
                .add(LootItem.lootTableItem(ModItems.INGOT_ZIRCONIUM.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.INGOT_NIOBIUM.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.INGOT_COBALT.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.INGOT_BORON.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.INGOT_STARMETAL.get()).setWeight(5))
                .add(LootItem.lootTableItem(ModItems.CRYSTAL_GOLD.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.CIRCUIT_BASIC.get()).setWeight(10))
                .add(LootItem.lootTableItem(ModItems.EGG_GLYPHID.get()).setWeight(1))
        ));
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
        handledBlocks.add(pBlock);
        super.dropSelf(pBlock);
    }

    @Override
    public void dropOther(Block pBlock, ItemLike pItem) {
        handledBlocks.add(pBlock);
        super.dropOther(pBlock, pItem);
    }

    @Override
    public void add(Block pBlock, LootTable.Builder pBuilder){
        handledBlocks.add(pBlock);
        super.add(pBlock, pBuilder);
    }

    private void fillMissingLootTables() {
        for (RegistryObject<Block> entry : ModBlocks.BLOCKS.getEntries()) {
            Block block = entry.get();
            if (handledBlocks.contains(block)) {
                continue;
            }
            if (BuiltInLootTables.EMPTY.equals(block.getLootTable())) {
                continue;
            }
            this.dropSelf(block);
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // 模组自定义的方块战利品表必须覆盖此方法，以绕过对原版方块战利品表的检查（此处返回该模组的所有方块）
        return Iterables.filter(
                Iterables.transform(ModBlocks.BLOCKS.getEntries(), RegistryObject::get),
                block -> !BuiltInLootTables.EMPTY.equals(block.getLootTable())
        );
    }
}
