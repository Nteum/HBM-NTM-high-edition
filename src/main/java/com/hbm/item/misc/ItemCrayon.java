package com.hbm.item.misc;

import com.hbm.HBMKey;
import com.hbm.item.CreativeTabVariantItem;
import com.hbm.registries.ModItems;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class ItemCrayon extends Item implements CreativeTabVariantItem {
    public ItemCrayon(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (DyeColor color : DyeColor.values()) {
            ItemStack stack = new ItemStack(ModItems.CRAYON.get());
            stack.getOrCreateTag().putInt(HBMKey.COLOR, color.getMapColor().col);
            stack.getOrCreateTag().putInt("color_name", color.getId());
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (pStack.getOrCreateTag().contains("color_name", Tag.TAG_INT)){
            int colorName = pStack.getOrCreateTag().getInt("color_name");
            int length = DyeColor.values().length;
            if (colorName >= 0 && colorName < length) return Component.translatable(DyeColor.values()[colorName].getSerializedName()).append(" ").append(super.getName(pStack));
            else return super.getName(pStack);
        }else return super.getName(pStack);
    }
}
