package com.hbm.blockentity.interfaces;

import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.List;

public interface IUpgradeInfoProvider {

	/** If any of the automated display stuff should be applied for this upgrade. A level of 0 is used by the GUI's indicator, as opposed to the item tooltips */
	public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo);
	public void provideInfo(UpgradeType type, int level, List<Component> pTooltipComponents, boolean extendedInfo);
	public HashMap<UpgradeType, Integer> getValidUpgrades();

	public static Component getStandardLabel(Block block) {
		return Component.literal(">>>").append(block.getName()).append("<<<").withStyle(ChatFormatting.GREEN);
//		return EnumChatFormatting.GREEN.YELLOW + ">>> " + I18nUtil.resolveKey(block.getUnlocalizedName() + ".name") + " <<<";
	}

	public static final String KEY_ACID = "upgrade.acid";
	public static final String KEY_BURN = "upgrade.burn";
	public static final String KEY_CONSUMPTION = "upgrade.consumption";
	public static final String KEY_COOLANT_CONSUMPTION = "upgrade.coolantConsumption";
	public static final String KEY_DELAY = "upgrade.delay";
	public static final String KEY_SPEED = "upgrade.speed";
	public static final String KEY_EFFICIENCY = "upgrade.efficiency";
	public static final String KEY_PRODUCTIVITY = "upgrade.productivity";
	public static final String KEY_FORTUNE = "upgrade.fortune";
	public static final String KEY_RANGE = "upgrade.range";
}
