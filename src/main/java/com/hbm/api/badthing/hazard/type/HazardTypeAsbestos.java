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

public class HazardTypeAsbestos extends HazardTypeBase {

	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(RadiationConfig.disableAsbestos)
			return;
		
//		if(!ArmorRegistry.hasProtection(target, 3, HazardClass.PARTICLE_FINE))
//			HbmLivingProps.incrementAsbestos(target, (int) Math.min(level, 10));
//		else
//			ArmorUtil.damageGasMaskFilter(target, (int) level);
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.WHITE + "[" + Component.translatable("trait.asbestos") + "]");
	}
}
