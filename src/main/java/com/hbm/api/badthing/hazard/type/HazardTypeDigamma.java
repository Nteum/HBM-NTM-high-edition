package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
/** 灾害：迪伽马 */
public class HazardTypeDigamma extends HazardTypeBase {
	//直接影响迪伽马数据
	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		ContaminationUtil.applyDigammaData(target, level / 20F);
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		
		level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);
		
		float d = (float)(Math.floor(level * 10000F)) / 10F;
		list.add(ChatFormatting.RED + "[" + Component.translatable("trait.digamma") + "]");
		list.add(ChatFormatting.DARK_RED + "" + d + "mDRX/s");
		
		if(stack.getCount() > 1) {
			list.add(ChatFormatting.DARK_RED + "Stack: " + ((Math.floor(level * 10000F * stack.getCount()) / 10F) + "mDRX/s"));
		}
	}

}
