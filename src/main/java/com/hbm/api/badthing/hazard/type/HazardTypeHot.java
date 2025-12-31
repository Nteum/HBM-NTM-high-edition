package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import com.hbm.config.Config528;
import com.hbm.config.ConfigGeneral;
import com.hbm.config.RadiationConfig;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModItems;;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
/** 高温伤害 */
public class HazardTypeHot extends HazardTypeBase {
	//计算高温伤害并使玩家起火
	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(RadiationConfig.disableHot)
			return;
		
		boolean reacher = false;
		
		if(target instanceof Player && !Config528.enable528)
			reacher = ((Player) target).getInventory().countItem(ModItems.reacher.get()) > 0;
		
		if(!reacher && !target.isInFluidType() && level > 0)
			target.setSecondsOnFire((int) Math.ceil(level));
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		
		level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);
		
		if(level > 0)
			list.add(ChatFormatting.GOLD + "[" + Component.translatable("trait.hot") + "]");
	}

}
