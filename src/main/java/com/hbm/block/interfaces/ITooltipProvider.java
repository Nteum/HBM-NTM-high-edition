package com.hbm.block.interfaces;

import com.hbm.registries.ModKeyMapping;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public interface ITooltipProvider {

	void addInformation(ItemStack stack, Player player, List list, boolean ext);

	default void addStandardInfo(ItemStack stack, Player player, List<Component> list, boolean ext) {
		if (ModKeyMapping.dashKey.isDown()){
			list.add(Component.translatable(stack.getDescriptionId() + ".desc"));
		}else {
			list.add(Component.literal("Hold <").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
					.append(Component.literal("LSHIFT").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC))
					.append(Component.literal("> to display more info").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)));
		}
//		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
//			for(String s : I18nUtil.resolveKeyArray(((Block)this).getUnlocalizedName() + ".desc")) list.add(EnumChatFormatting.YELLOW + s);
//		} else {
//			list.add(EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC +"Hold <" +
//					EnumChatFormatting.YELLOW + "" + EnumChatFormatting.ITALIC + "LSHIFT" +
//					EnumChatFormatting.DARK_GRAY + "" + EnumChatFormatting.ITALIC + "> to display more info");
//		}
	}

	default Rarity getRarity(ItemStack stack) {
		return Rarity.COMMON;
	}
}
