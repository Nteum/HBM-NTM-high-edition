package com.hbm.registries;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hbm.registries.ModTags.Items.*;
import static com.hbm.registries.ModTags.HBMMatter;

public class HBMMatters {
    private static final List<HBMMatter> ALL_MATTERS = new ArrayList<>();
    // 反向查找表：根据 Item 找到对应的 Matter
    private static final Map<Item, HBMMatter> ITEM_TO_MATTER = new HashMap<>();
    // 根据 Item 找到对应的形状（是锭、是粉还是粒）
    private static final Map<Item, TagKey<Item>> ITEM_TO_SHAPE = new HashMap<>();

    // 材料列表
    //Base metals
    public static final HBMMatter TITANIUM = register(new HBMMatter("titanium", 0xF7F3F2, 0x4F4C4B, 0xA99E79).shapes(Tags.Items.INGOTS, FRAGMENT, DUST, PLATE, DENSEWIRE, CASTPLATE, WELDEDPLATE, SHELL, Tags.Items.STORAGE_BLOCKS).gen(entry -> entry.addKeyOut(METAL)).toFluid());
    //Vanilla and vanilla-like
    public static final HBMMatter WOOD = register(new HBMMatter("wood",0x896727, 0x281E0B, 0x896727).shapes(STOCK, GRIP).gen(entry -> entry.addKeyOut(NORMAL).addKeyIn(ItemTags.LOGS, Tags.Items.BARRELS_WOODEN)));
    //Radioactive

    public static HBMMatter register(HBMMatter matter){
        ALL_MATTERS.add(matter);
        return matter;
    }
    public static void buildCache() {
        ITEM_TO_MATTER.clear();
        // 遍历你之前注册的所有 HBMMatter
        for (HBMMatter matter : ALL_MATTERS) {
            for (Map.Entry<TagKey<Item>, TagKey<Item>> entry : matter.shapes.entrySet()) {
                TagKey<Item> shapeType = entry.getKey(); // 例如 Tags.Items.INGOTS
                TagKey<Item> specificTag = entry.getValue(); // 例如 forge:ingots/titanium

                // 关键：获取这个 Tag 下的所有物品并建立映射
                ForgeRegistries.ITEMS.tags().getTag(specificTag).forEach(holder -> {
                    Item item = holder.asItem();
                    ITEM_TO_MATTER.put(item, matter);
                    ITEM_TO_SHAPE.put(item, shapeType);
                });
            }
        }
    }

    public static boolean canSmelt(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();
        HBMMatter matter = ITEM_TO_MATTER.get(item);

        // 如果查到了 Matter，说明它是你定义的材料之一
        if (matter != null) {
            return matter.canMolten();
        }

        return false;
    }

    public static FluidStack getMoltenMatter(ItemStack stack){
        if (stack.isEmpty()) return FluidStack.EMPTY;
        Item item = stack.getItem();
        HBMMatter matter = ITEM_TO_MATTER.get(item);
        ModTags.MatterFormat matterFormat = MATTER_FORMATS.get(ITEM_TO_SHAPE.get(item));
        if (matter != null && matter.canMolten() && matterFormat != null) {
            return new FluidStack(matter.fluid(), matterFormat.quantity);
        }
        return FluidStack.EMPTY;
    }
}
