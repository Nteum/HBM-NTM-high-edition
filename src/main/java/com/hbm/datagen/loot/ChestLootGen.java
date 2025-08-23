package com.hbm.datagen.loot;

import com.hbm.HBM;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * 所有奖励箱的
 * */
public class ChestLootGen extends VanillaChestLoot {
    public static final ResourceLocation GENERIC = HBM.rl("chests/generic");
    public static final ResourceLocation ANTENNA = HBM.rl("chests/antenna");
    public static final ResourceLocation EXPENSIVE = HBM.rl("chests/expensive");
    public static final ResourceLocation NUKE_TRASH = HBM.rl("chests/nuke_trash");
    public static final ResourceLocation NUKE_MISC = HBM.rl("chests/nuke_misc");
    public static final ResourceLocation VERTIBIRD = HBM.rl("chests/vertibird");
    public static final ResourceLocation SPACESHIP = HBM.rl("chests/spaceship");
    public static final ResourceLocation SUPPLES = HBM.rl("chests/supples");
    public static final ResourceLocation WEAPONS = HBM.rl("chests/weapons");
    public static final ResourceLocation AMMO = HBM.rl("chests/ammo");
    public static final ResourceLocation MACHINE_PARTS = HBM.rl("chests/machine_parts");
    public static final ResourceLocation NUKE_FUEL = HBM.rl("chests/nuke_fuel");
    public static final ResourceLocation SILO = HBM.rl("chests/silo");
    public static final ResourceLocation OFFICE_TRASH = HBM.rl("chests/office_trash");
    public static final ResourceLocation FILING_CABINET = HBM.rl("chests/filing_cabinet");
    public static final ResourceLocation SOLID_FUEL = HBM.rl("chests/solid_fuel");
    public static final ResourceLocation VAULT_LAB = HBM.rl("chests/vault_lab");
    public static final ResourceLocation VAULT_LOCKERS = HBM.rl("chests/vault_lockers");
    public static final ResourceLocation METEOR_SAFE = HBM.rl("chests/meteor_safe");
    public static final ResourceLocation OIL_RIG = HBM.rl("chests/oil_rig");
    public static final ResourceLocation RTG = HBM.rl("chests/rtg");
    public static final ResourceLocation REPAIR_MATERIALS = HBM.rl("chests/repair_materials");
    public static final ResourceLocation PILE_HIVE = HBM.rl("chests/pile_hive");
    public static final ResourceLocation PILE_BONES = HBM.rl("chests/pile_bones");
    public static final ResourceLocation PILE_CAPS = HBM.rl("chests/pile_caps");
    public static final ResourceLocation PILE_MED_SYRINGE = HBM.rl("chests/pile_med_syringe");
    public static final ResourceLocation PILE_MED_PILLS = HBM.rl("chests/pile_med_pills");
    public static final ResourceLocation PILE_MAKESHIFT_GUN = HBM.rl("chests/pile_makeshift_gun");
    public static final ResourceLocation PILE_MAKESHIFT_WRENCH = HBM.rl("chests/pile_makeshift_wrench");
    public static final ResourceLocation PILE_MAKESHIFT_PLATES = HBM.rl("chests/pile_makeshift_plates");
    public static final ResourceLocation PILE_MAKESHIFT_WIRE = HBM.rl("chests/pile_makeshift_wire");
    public static final ResourceLocation PILE_NUKE_STORAGE = HBM.rl("chests/pile_nuke_storage");
    public static final ResourceLocation RED_PEDESTAL = HBM.rl("chests/red_pedestal");
    public static final ResourceLocation BLACK_SLAB = HBM.rl("chests/black_slab");
    public static final ResourceLocation BLACK_PART = HBM.rl("chests/black_part");
    public static final ResourceLocation SAT_MINER = HBM.rl("chests/sat_miner");
    public static final ResourceLocation SAT_LUNAR = HBM.rl("chests/sat_lunar");
    public static final ResourceLocation POWDER = HBM.rl("chests/powder");
    public static final ResourceLocation VAULT_RUSTY = HBM.rl("chests/vault_rusty");
    public static final ResourceLocation VAULT_STANDARD = HBM.rl("chests/vault_standard");
    public static final ResourceLocation VAULT_REINFORCED = HBM.rl("chests/vault_reinforced");
    public static final ResourceLocation VAULT_UNBREAKABLE = HBM.rl("chests/vault_unbreakable");
    public static final ResourceLocation METEORITE_TREASURE = HBM.rl("chests/meteorite_treasure");

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> builder) {

    }
}
