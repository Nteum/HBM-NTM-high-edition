package com.hbm.item.armor;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public interface IAttackHandler {

	public void handleAttack(LivingAttackEvent event, ItemStack armor);
}
