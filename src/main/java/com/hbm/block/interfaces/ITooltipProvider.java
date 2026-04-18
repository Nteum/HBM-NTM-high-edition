package com.hbm.block.interfaces;

import com.hbm.HBMLang;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public interface ITooltipProvider {
    default void addInformation(ItemStack stack, Player player, List<Component> list, TooltipFlag flag){
        addStandardInfo(stack, player, list, flag);
    }

    static void addStandardInfo(ItemStack stack, Player player, List<Component> list, TooltipFlag flag) {
        Item item = stack.getItem();
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340)){
            // 自动寻找 ".desc" 后缀的翻译
            String descKey = item.getDescriptionId() + ".desc";
            // 检查翻译是否存在，防止显示一串原始代码
            if (I18n.exists(descKey)) {
                list.add(Component.translatable(descKey));
            }
        }else {
            list.add(HBMLang.TOOLTIP_SHOW_DETAIL.translate(Component.literal("<LSHIFT>").withStyle(ChatFormatting.YELLOW)));
        }
    }

    default Rarity getRarity(ItemStack stack) {
        return Rarity.COMMON;
    }
}
