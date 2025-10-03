package com.hbm.item.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface IDamageHandler {

	public void handleDamage(LivingHurtEvent event, ItemStack stack);
}
