package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import com.hbm.config.RadiationConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
/** 灾害：致盲效果 */
public class HazardTypeBlinding extends HazardTypeBase {

	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(RadiationConfig.disableBlinding)
			return;

//		if(!ArmorRegistry.hasProtection(target, 3, HazardClass.LIGHT)) {
//			target.addPotionEffect(new PotionEffect(Potion.blindness.id, (int)Math.ceil(level), 0));
//		}
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.DARK_AQUA + "[" + Component.translatable("trait.blinding") + "]");
	}

}
