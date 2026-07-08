package com.hbm.item.env;

import com.hbm.HBMLang;
import com.hbm.item.interfaces.CreativeTabVariantItem;
import com.hbm.utils.data.NBTHelper;
import com.hbm.world.feature.BedrockOreDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class ItemBedrockOre extends Item implements CreativeTabVariantItem {
    public ItemBedrockOre(Properties pProperties) {
        super(pProperties);
    }

    public static void setDefinition(ItemStack itemStack, String definition){
        itemStack.getOrCreateTag().putString("definition", definition);
    }

    public static BedrockOreDefinition getDefinition(ItemStack itemStack){
        return BedrockOreDefinition.DEFINITIONS.get(NBTHelper.getStr(itemStack, "definition", ""));
    }

    public static int getColor(ItemStack stack){
        BedrockOreDefinition definition = ItemBedrockOre.getDefinition(stack);
        return definition != null? definition.color : -1;
    }

    @Override
    public void fillCreativeTab(BuildCreativeModeTabContentsEvent event) {
        for (BedrockOreDefinition definition : BedrockOreDefinition.DEFINITIONS.values()) {
            if (definition.getItemStack().getItem() == this){
                event.getEntries().put(definition.getItemStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        BedrockOreDefinition definition = getDefinition(pStack);
        Component name = super.getName(pStack);
        if (definition != null){
            HBMLang hbmLang = null;
            try {
                hbmLang = HBMLang.valueOf(definition.id);
            }catch (Exception e){}
            if (hbmLang != null) return hbmLang.translate().append(" ").append(name);
        }
        return name;
    }
}
