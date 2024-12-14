package com.hbm.modsetting.badthing.hazard.transformers;



import com.hbm.modsetting.badthing.hazard.HazardEntry;
import net.minecraft.world.item.ItemStack;

import java.util.List;
/** 异常的修改类 */
public abstract class HazardTransformerBase {

	public abstract void transformPre(ItemStack stack, List<HazardEntry> entries);
	public abstract void transformPost(ItemStack stack, List<HazardEntry> entries);
}
