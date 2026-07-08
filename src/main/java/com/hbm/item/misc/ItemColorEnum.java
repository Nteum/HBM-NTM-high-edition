package com.hbm.item.misc;

import com.hbm.HBMKey;
import com.hbm.item.interfaces.CreativeTabVariantItem;
import com.hbm.registries.ModItems;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class ItemColorEnum extends Item implements CreativeTabVariantItem {
    public ItemColorEnum(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (DyeColor color : DyeColor.values()) {
            ItemStack stack = new ItemStack(ModItems.CRAYON.get());
            stack.getOrCreateTag().putInt(HBMKey.COLOR, color.getMapColor().col);
            stack.getOrCreateTag().putInt("color_id", color.getId());
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        if (pStack.getOrCreateTag().contains("color_id", Tag.TAG_INT)){
            int colorName = pStack.getOrCreateTag().getInt("color_id");
            int length = DyeColor.values().length;
            if (colorName >= 0 && colorName < length) return Component.translatable(DyeColor.values()[colorName].getSerializedName()).append(" ").append(super.getName(pStack));
            else return super.getName(pStack);
        }else return super.getName(pStack);
    }

    public static ItemStack makeStack(Item item, DyeColor color, int num){
        ItemStack stack = new ItemStack(item, num);
        if (item instanceof ItemColorEnum){
            stack.getOrCreateTag().putInt("color_id", color.getId());
        }
        return stack;
    }
}
