package com.hbm.blockentity.interfaces;

import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IUpgradeInfoProvider {

	/** If any of the automated display stuff should be applied for this upgrade. A level of 0 is used by the GUI's indicator, as opposed to the item tooltips */
    boolean canProvideInfo(UpgradeType type, int level);
	void provideInfo(UpgradeType type, int level, List<Component> pTooltipComponents);
	Map<UpgradeType, Integer> getValidUpgrades();

	static Component getStandardLabel(Block block) {
		return Component.literal(">>>").append(block.getName()).append("<<<").withStyle(ChatFormatting.GREEN);
	}

	public static Map<UpgradeType, Integer> getUpgradeNow(Map<UpgradeType, Integer> upgradeBefore, IUpgradeInfoProvider provider, IItemHandler slots, int start, int end){
		if (upgradeBefore == null) upgradeBefore = new HashMap<>();
		upgradeBefore.clear();
		for (int i = start; i < end; i++) {
			ItemStack stackInSlot = slots.getStackInSlot(start);
			if (stackInSlot.getItem() instanceof ItemMachineUpgrade upgrade){
				UpgradeType type = upgrade.type;
				int tier = upgrade.tier;
				if (provider.getValidUpgrades().containsKey(type) && (!upgradeBefore.containsKey(type)) || upgradeBefore.get(type) < tier && tier <= provider.getValidUpgrades().get(type))
					upgradeBefore.put(type, tier);
			}
		}
		return upgradeBefore;
	}

	String KEY_ACID = "upgrade.acid";
	String KEY_BURN = "upgrade.burn";
	String KEY_CONSUMPTION = "upgrade.consumption";
	String KEY_COOLANT_CONSUMPTION = "upgrade.coolantConsumption";
	String KEY_DELAY = "upgrade.delay";
	String KEY_SPEED = "upgrade.speed";
	String KEY_EFFICIENCY = "upgrade.efficiency";
	String KEY_PRODUCTIVITY = "upgrade.productivity";
	String KEY_FORTUNE = "upgrade.fortune";
	String KEY_RANGE = "upgrade.range";
}
