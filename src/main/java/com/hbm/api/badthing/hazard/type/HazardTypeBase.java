package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/** 所有异常类型的基类 */
public abstract class HazardTypeBase {
	
	/**
	 * 更新hazard状态
	 * Does the thing. Called by HazardEntry.applyHazard
	 * @param target the holder
	 * @param level the final level after calculating all the modifiers
	 * @param stack stack that is being updated
	 */
	public abstract void onUpdate(LivingEntity target, float level, ItemStack stack);

	/**
	 * 更新凋落物实体的状态
	 * Updates the hazard for dropped items. Used for things like explosive and hydroactive items.
	 * @param item
	 * @param level
	 */
	public abstract void updateEntity(ItemEntity item, float level);
	
	/**
	 * 添加物品的tooltip
	 * Adds item tooltip info. Called by Item.addInformation
	 * @param player
	 * @param list
	 * @param level the base level, mods are passed separately
	 * @param stack
	 * @param modifiers
	 */
	@OnlyIn(Dist.CLIENT)
	public abstract void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers);
}
