package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import com.hbm.config.RadiationConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


import java.util.List;
/** 灾害：爆炸 */
public class HazardTypeExplosive extends HazardTypeBase {
	//由于旧版爆炸函数有所改变，所以实际上爆炸是有区别的.
	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {

		if(RadiationConfig.disableExplosive)
			return;

		if(!target.level().isClientSide && target.isOnFire() && stack.getCount() > 0) {
			stack.setCount(0);
			target.level().explode(null, target.getX(), target.getY() + target.getEyeHeight() - target.getMyRidingOffset(), target.getZ(), 2.5F, Level.ExplosionInteraction.NONE);
		}
	}

	@Override
	public void updateEntity(ItemEntity item, float level) {
		
		if(RadiationConfig.disableExplosive)
			return;
		
		if(item.isOnFire()) {
			item.discard();
			item.level().explode(null, item.getX(), item.getY() + item.getEyeHeight() * 0.5, item.getZ(), 2.5F, Level.ExplosionInteraction.NONE);
		}
	}

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.RED + "[" + Component.translatable("trait.explosive") + "]");
	}
}
