package com.hbm.compat.legacy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbm.HBM;
import com.hbm.HBMKey;
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
import java.util.Map;

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
        new WrapperRegistry.ItemBuilder(name, () -> new Item(new Item.Properties()))
                .tab(tab)
                .model(HBMKey.BASIC_MODEL)
                .loc(HBMKey.ORDERLY_GEN)
                .build();
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
}
