package com.hbm.api.badthing.hazard.type;

import com.hbm.api.badthing.ContaminationUtil;
import com.hbm.api.badthing.hazard.modifier.HazardModifier;
import com.hbm.config.Config528;
import com.hbm.registries.ModItems;
;
import com.hbm.utils.math.BobMth;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
/** 异常类型：辐射 */
public class HazardTypeRadiation extends HazardTypeBase {
	//计算某物品带来的辐射，并污染用户
	@Override
	public void onUpdate(LivingEntity target, float level, ItemStack stack) {
		
		boolean reacher = false;
		
		if(target instanceof Player)
			reacher = ((Player) target).getInventory().countItem(ModItems.reacher.get()) > 0;
		
		level *= stack.getCount();
		
		if(level > 0) {
			float rad = level / 20F;
			
			if(Config528.enable528 && reacher) {
				rad = (float) (rad / 49F);	//More realistic function for 528: x / distance^2
			} else if(reacher) {
				rad = (float) BobMth.squirt(rad); //Reworked radiation function: sqrt(x+1/(x+2)^2)-1/(x+2)
			}											
			
			ContaminationUtil.contaminate(target, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad);
		}
	}

	@Override
	public void updateEntity(ItemEntity item, float level) { }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addHazardInformation(Player player, List list, float level, ItemStack stack, List<HazardModifier> modifiers) {
		
		level = HazardModifier.evalAllModifiers(stack, player, level, modifiers);
		
		if(level < 1e-5)
			return;
		
		list.add(ChatFormatting.GREEN + "[" + Component.translatable("trait.radioactive") + "]");
		String rad = "" + (Math.floor(level* 1000) / 1000);
		list.add(ChatFormatting.YELLOW + (rad + "RAD/s"));
		
		if(!stack.isEmpty()) {
			list.add(ChatFormatting.YELLOW + "Stack: " + ((Math.floor(level * 1000 * stack.getCount()) / 1000) + "RAD/s"));
		}
	}

}
