package com.hbm.settings.tooltip;

import com.hbm.HBMLang;
import com.hbm.item.HBMItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TooltipRegistries {
    public static final Map<Item, List<Component>> TOOLTIP_MAP = new HashMap<>();

    static {
        addTooltip(HBMItems.INGOT_NEPTUNIUM.get(), HBMLang.ITEM_INGOT_NEPTUNIUM_DESC.translate());
        addTooltip(HBMItems.DUST.get(), HBMLang.ITEM_DUST_DESC.translate());
        addTooltip(HBMItems.POWDER_FIRE.get(), HBMLang.ITEM_POWDER_FIRE_DESC.translate());
    }

    public static void addTooltip(Item item, Component ... components) {
        TOOLTIP_MAP
                .computeIfAbsent(item, k -> new ArrayList<>())
                .addAll(List.of(components));
    }

    public static List<Component> getTooltip(Item item) {
        return TOOLTIP_MAP.getOrDefault(item, List.of());
    }

    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        List<Component> lines = getTooltip(item);
        if (!lines.isEmpty()) {
            event.getToolTip().addAll(lines);
        }
    }
}
