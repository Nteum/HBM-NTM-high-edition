package com.hbm.item;


import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.JigsawBlock;

public interface ISatChip {
	
	static int getFreqS(ItemStack stack) {
		if(stack != null && stack.getItem() instanceof ISatChip) {
			return ((ISatChip) stack.getItem()).getFreq(stack);
		}

		return 0;
	}
	
	static void setFreqS(ItemStack stack, int freq) {
		if(stack != null && stack.getItem() instanceof ISatChip) {
			((ISatChip) stack.getItem()).setFreq(stack, freq);
		}
	}

	default int getFreq(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("freq", Tag.TAG_INT)) return stack.getTag().getInt("freq");
		return 0;
	}
	
	default void setFreq(ItemStack stack, int freq) {
		stack.getOrCreateTag().putInt("freq", freq);
	}
}
