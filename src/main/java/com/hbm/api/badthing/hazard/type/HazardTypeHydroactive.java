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
import net.minecraft.world.level.material.Fluids;

import java.util.List;
/** 灾害：遇水反应（应该是锂粉一类的） */
public class HazardTypeHydroactive extends HazardTypeBase {

	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		if(RadiationConfig.disableHydro)
			return;
		
		if(target.isInFluidType() && stack.getCount() > 0) {
			stack.setCount(0);
			target.level().explode(null, target.getX(), target.getY() + target.getEyeHeight() - target.getNameTagOffsetY(), target.getZ(), 2.5F, Level.ExplosionInteraction.NONE);
		}
	}

	@Override
	public void updateEntity(ItemEntity item, float level) {
		
		if(RadiationConfig.disableHydro)
			return;

		if(item.isInFluidType() || item.getBlockStateOn().getFluidState().is(Fluids.WATER) || item.getBlockStateOn().getFluidState().is(Fluids.FLOWING_WATER)) {
			item.discard();
			item.level().explode(null, item.getX(), item.getY() + item.getEyeHeight() * 0.5, item.getZ(), 2.5F, Level.ExplosionInteraction.NONE);
		}
	}

	@Override
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		list.add(ChatFormatting.RED + "[" + Component.translatable("trait.hydro") + "]");
	}
}