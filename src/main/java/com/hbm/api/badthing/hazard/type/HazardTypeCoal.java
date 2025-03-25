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
/** 灾害：煤粉病 */
public class HazardTypeCoal extends HazardTypeBase {
	//影响HbmLivingProps中的尘肺病系数
	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(RadiationConfig.disableCoal)
			return;
		
//		if(!ArmorRegistry.hasProtection(target, 3, HazardClass.PARTICLE_COARSE)) {
//			HbmLivingProps.incrementBlackLung(target, (int) Math.min(level * stack.getCount(), 10));
//		} else {
//			if(target.getRandom().nextInt(Math.max(65 - stack.getCount(), 1)) == 0) {
//				ArmorUtil.damageGasMaskFilter(target, (int) level);
//			}
//		}
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.DARK_GRAY + "[" + Component.translatable("trait.coal") + "]");
	}

}
