package com.hbm.compat.legacy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbm.HBM;
import com.hbm.HBMKey;
import com.hbm.entity.weapon.missile.EntityMissileTier0;
import com.hbm.item.weapon.ItemMissile;
import com.hbm.registries.ModTabs;
import com.hbm.registries.WrapperRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registers legacy (1.7.10) items that are missing from the modern registry.
 * Entries are sourced from data/hbm/legacy_items.json.
 */
public final class LegacyItems {
    private static final String RESOURCE_PATH = "/data/hbm/legacy_items.json";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, LegacyItemInfo>>() {}.getType();
    private static boolean initialized = false;

    private LegacyItems() {
    }

    public static void registerLegacy() {
        if (initialized) {
            return;
        }
        initialized = true;
        Map<String, LegacyItemInfo> entries = loadEntries();
        if (entries.isEmpty()) {
            return;
        }
        for (Map.Entry<String, LegacyItemInfo> entry : entries.entrySet()) {
            registerLegacyItem(entry.getKey(), entry.getValue());
        }
    }

    private static void registerLegacyItem(String name, @Nullable LegacyItemInfo info) {
        ResourceKey<CreativeModeTab> tab = resolveTab(info != null ? info.tab() : null);
        new WrapperRegistry.ItemBuilder(name, createLegacyItem(name, info))
                .tab(tab)
                .model(HBMKey.BASIC_MODEL)
                .loc(HBMKey.ORDERLY_GEN)
                .build();
    }

    private static Supplier<? extends Item> createLegacyItem(String name, @Nullable LegacyItemInfo info) {
        String tab = info != null ? info.tab() : null;
        if ("missile".equals(tab) && isLegacyLaunchableMissile(name)) {
            return () -> createLegacyMissile(name);
        }
        return () -> new Item(new Item.Properties());
    }

    private static boolean isLegacyLaunchableMissile(String name) {
        return name.startsWith("missile_") && !name.startsWith("missile_skin_");
    }

    private static ItemMissile createLegacyMissile(String name) {
        LegacyMissileProfile profile = resolveMissileProfile(name);
        EntityMissileTier0.EntityMissileTest.Payload payload = profile.payload() == null
                ? EntityMissileTier0.EntityMissileTest.Payload.TEST
                : profile.payload();
        return new ItemMissile(
                new Item.Properties(),
                profile.formFactor(),
                profile.tier(),
                profile.formFactor() == ItemMissile.MissileFormFactor.ABM
                        ? null
                        : (level, x, y, z, target) -> EntityMissileTier0.EntityMissileTest.create(level, x, y, z, target, payload));
    }

    private static LegacyMissileProfile resolveMissileProfile(String name) {
        String id = name.toLowerCase(Locale.ROOT);
        return switch (id) {
            case "missile_anti_ballistic" -> profile(ItemMissile.MissileFormFactor.ABM, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.GENERIC);

            // Tier 0 / micro frame
            case "missile_test" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.TEST);
            case "missile_micro" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.MICRO);
            case "missile_emp" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.EMP);
            case "missile_schrabidium" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.SCHRABIDIUM);
            case "missile_bhole" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.BHOLE);
            case "missile_taint" -> profile(ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0, EntityMissileTier0.EntityMissileTest.Payload.TAINT);

            // Tier 1 / v2 frame
            case "missile_generic", "missile_custom", "missile_assembly" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.GENERIC);
            case "missile_decoy" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.DECOY);
            case "missile_incendiary" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.INCENDIARY);
            case "missile_cluster" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.CLUSTER);
            case "missile_buster" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.BUSTER);
            case "missile_kit" -> profile(ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1, EntityMissileTier0.EntityMissileTest.Payload.REJUVENATION);

            // Tier 2 / strong frame
            case "missile_strong" -> profile(ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.STRONG);
            case "missile_emp_strong" -> profile(ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.EMP_STRONG);
            case "missile_incendiary_strong" -> profile(ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.INCENDIARY_STRONG);
            case "missile_cluster_strong" -> profile(ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.CLUSTER_STRONG);
            case "missile_buster_strong" -> profile(ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.BUSTER_STRONG);

            // Tier 3 / huge frame
            case "missile_burst" -> profile(ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3, EntityMissileTier0.EntityMissileTest.Payload.BURST);
            case "missile_inferno" -> profile(ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3, EntityMissileTier0.EntityMissileTest.Payload.INFERNO);
            case "missile_rain" -> profile(ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3, EntityMissileTier0.EntityMissileTest.Payload.RAIN);
            case "missile_drill" -> profile(ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3, EntityMissileTier0.EntityMissileTest.Payload.DRILL);

            // Tier 4 / atlas frame
            case "missile_nuclear" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.NUCLEAR);
            case "missile_nuclear_cluster" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.NUCLEAR_CLUSTER);
            case "missile_doomsday" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.DOOMSDAY);
            case "missile_doomsday_rusted" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.DOOMSDAY_RUSTED);
            case "missile_volcano" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.VOLCANO);
            case "missile_shuttle" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.SHUTTLE);
            case "missile_soyuz", "missile_soyuz_lander" -> profile(ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4, EntityMissileTier0.EntityMissileTest.Payload.SOYUZ);

            // Special
            case "missile_stealth" -> profile(ItemMissile.MissileFormFactor.OTHER, ItemMissile.MissileTier.TIER2, EntityMissileTier0.EntityMissileTest.Payload.STEALTH);

            default -> {
                ItemMissile.MissileFormFactor formFactor = id.contains("strong")
                        ? ItemMissile.MissileFormFactor.STRONG
                        : ItemMissile.MissileFormFactor.V2;
                ItemMissile.MissileTier tier = formFactor == ItemMissile.MissileFormFactor.STRONG
                        ? ItemMissile.MissileTier.TIER2
                        : ItemMissile.MissileTier.TIER1;
                yield profile(formFactor, tier, EntityMissileTier0.EntityMissileTest.Payload.GENERIC);
            }
        };
    }

    private static LegacyMissileProfile profile(
            ItemMissile.MissileFormFactor formFactor,
            ItemMissile.MissileTier tier,
            EntityMissileTier0.EntityMissileTest.Payload payload
    ) {
        return new LegacyMissileProfile(formFactor, tier, payload);
    }

    private static ResourceKey<CreativeModeTab> resolveTab(@Nullable String tabKey) {
        if (tabKey == null) {
            return ModTabs.PARTS.getKey();
        }
        return switch (tabKey) {
            case "control" -> ModTabs.CONTROL.getKey();
            case "template" -> ModTabs.TEMPLATE.getKey();
            case "blocks" -> ModTabs.BLOCKS.getKey();
            case "machine" -> ModTabs.MACHINE.getKey();
            case "nuke" -> ModTabs.NUKE.getKey();
            case "missile" -> ModTabs.MISSILE.getKey();
            case "weapon" -> ModTabs.WEAPON.getKey();
            case "consumable" -> ModTabs.CONSUMABLE.getKey();
            default -> ModTabs.PARTS.getKey();
        };
    }

    private static Map<String, LegacyItemInfo> loadEntries() {
        try (InputStream stream = LegacyItems.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                HBM.LOGGER.warn("Legacy item list {} not found; skipping legacy item registration", RESOURCE_PATH);
                return Collections.emptyMap();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                Map<String, LegacyItemInfo> data = GSON.fromJson(reader, MAP_TYPE);
                return data != null ? data : Collections.emptyMap();
            }
        } catch (Exception ex) {
            HBM.LOGGER.error("Failed to read legacy item list {}", RESOURCE_PATH, ex);
            return Collections.emptyMap();
        }
    }

    private record LegacyItemInfo(String tab, String texture) {
    }

    private record LegacyMissileProfile(
            ItemMissile.MissileFormFactor formFactor,
            ItemMissile.MissileTier tier,
            EntityMissileTier0.EntityMissileTest.Payload payload
    ) {
    }
}
