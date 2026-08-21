package com.hbm.block.interfaces;

import com.hbm.HBMLang;
import com.hbm.api.text.RichText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 说明文本提供者（集中化 + 富文本）。
 *
 * 背景：原版 hbm 的物品/方块说明文字散落在各处，且用大量 EnumChatFormatting 拼接高亮。
 * 本接口把"说明文本"集中化：
 * 1. 物品/方块实现本接口（或直接实现 addInformation），注册时通过实现本接口声明说明
 * 2. 说明文本写在 lang 文件的 "<物品id>.desc" 键中，可直接使用富文本标签（见 RichText），
 *    例如：
 *      "item.hbm.xxx.desc": "这个物品会&lt;red&gt;致命&lt;/red&gt;，需要&lt;b&gt;防护服&lt;/b&gt;"
 * 3. 渲染时 addStandardInfo 自动把 lang 文本解析为带样式的 Component 追加到 tooltip
 *
 * 相比旧版：
 * - 文本集中在 lang 文件，翻译时无需改 Java 代码
 * - RichText 标签取代 EnumChatFormatting 字符串拼接
 * - 按 SHIFT 显示详细说明的交互模式保留
 *
 * 说明键默认取 "<物品注册id>.desc"（旧约定），也可通过覆盖 descKey() 指定自定义键。
 * 建议物品/方块注册时实现本接口，使说明文本与注册集中在一起。
 */
public interface ITooltipProvider {
    default void addInformation(ItemStack stack, Player player, List<Component> list, TooltipFlag flag){
        addStandardInfo(stack, player, list, flag);
    }

    /** 覆盖以指定自定义说明键，默认使用 "<物品注册id>.desc" */
    default String descKey(ItemStack stack){
        return stack.getItem().getDescriptionId() + ".desc";
    }

    static void addStandardInfo(ItemStack stack, Player player, List<Component> list, TooltipFlag flag) {
        Item item = stack.getItem();
        if (net.minecraft.client.gui.screens.Screen.hasShiftDown()){
            // 自动寻找说明键对应的翻译，支持富文本标签
            String descKey = stack.getItem() instanceof ITooltipProvider provider ? provider.descKey(stack) : item.getDescriptionId() + ".desc";
            // 检查翻译是否存在，防止显示一串原始代码
            if (I18n.exists(descKey)) {
                list.add(RichText.parseLang(descKey));
            }
        }else {
            list.add(HBMLang.TOOLTIP_SHOW_DETAIL.translate(Component.literal("<LSHIFT>").withStyle(ChatFormatting.YELLOW)));
        }
    }

    default Rarity getRarity(ItemStack stack) {
        return Rarity.COMMON;
    }
}
