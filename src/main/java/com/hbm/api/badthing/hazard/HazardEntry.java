package com.hbm.api.badthing.hazard;

import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import com.hbm.api.badthing.hazard.type.HazardTypeBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
/** 存储单个异常的项 */
public class HazardEntry {

	HazardTypeBase type;	//异常类型
	float baseLevel;		//异常的程度
	
	/*
	 * Modifiers are evaluated in the order they're being applied to the entry.
	 */
	List<HazardModifier> mods = new ArrayList();
	
	public HazardEntry(HazardTypeBase type) {
		this(type, 1F);
	}
	
	public HazardEntry(HazardTypeBase type, float level) {
		this.type = type;
		this.baseLevel = level;
	}
	
	public HazardEntry addMod(HazardModifier mod) {
		this.mods.add(mod);
		return this;
	}
	
	public void applyHazard(ItemStack stack, LivingEntity entity) {
		type.onUpdate(entity, HazardModifier.evalAllModifiers(stack, entity, baseLevel, mods), stack);
	}
	
	public HazardTypeBase getType() {
		return this.type;
	}
	
	public HazardEntry clone() {
		return clone(1F);
	}
	
	public HazardEntry clone(float mult) {
		HazardEntry clone = new HazardEntry(type, baseLevel * mult);
		clone.mods = this.mods;
		return clone;
	}
}
