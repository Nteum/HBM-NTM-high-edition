package com.hbm.Inventory;


import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Arrays;
import java.util.HashMap;
// bob检查升级的类，感觉写的很奇怪，哪个mutex型的升级到底是干什么的？我看不明白，不知道怎么改，暂时按照原样。
/*
 Steps for use:
 1. TE implements IUpgradeInfoProvider
 2. TE creates a new instance of UpgradeManagerNT
 3. Upgrades and their levels can then be pulled from there.
 */

/**
 * Upgrade system, now with caching!
 * @author BallOfEnergy1
 */
public class UpgradeManagerNT {

	public BlockEntity owner;
	public ItemStack[] cachedSlots;

	private UpgradeType mutexType;
	public HashMap<UpgradeType, Integer> upgrades = new HashMap<>();

	public UpgradeManagerNT(BlockEntity te) { this.owner = te; }
	@Deprecated public UpgradeManagerNT() { }
	
	public void checkSlots(ItemStack[] slots, int start, int end) { checkSlotsInternal(owner, slots, start, end); }
	public void checkSlots(BlockEntity te, NonNullList<ItemStack> slots, int start, int end) {
		ItemStack[] itemStacks = slots.stream().toList().toArray(new ItemStack[0]);
		checkSlotsInternal(te, itemStacks, start, end);
	}

	private void checkSlotsInternal(BlockEntity te, ItemStack[] slots, int start, int end) {

		if(!(te instanceof IUpgradeInfoProvider) || slots == null)
			return;

		ItemStack[] upgradeSlots = Arrays.copyOfRange(slots, start, end + 1);

		if(Arrays.equals(upgradeSlots, cachedSlots))
			return;

		cachedSlots = upgradeSlots.clone();

		upgrades.clear();

		for (int i = 0; i <= end - start; i++) {

			if(upgradeSlots[i] != null && upgradeSlots[i].getItem() instanceof ItemMachineUpgrade) {

				ItemMachineUpgrade item = (ItemMachineUpgrade) upgradeSlots[i].getItem();
				IUpgradeInfoProvider upgradable = (IUpgradeInfoProvider) te;

				if(upgradable.getValidUpgrades() == null)
					return;

				if (upgradable.getValidUpgrades().containsKey(item.type)) { // Check if upgrade can even be accepted by the machine.
					if (item.type.mutex) {
						if (mutexType == null) {
							upgrades.put(item.type, 1);
							mutexType = item.type;
						} else if(item.type.ordinal() > mutexType.ordinal()) {
							upgrades.remove(mutexType);
							upgrades.put(item.type, 1);
							mutexType = item.type;
						}
					} else {

						Integer levelBefore = upgrades.get(item.type);
						int upgradeLevel = (levelBefore == null ? 0 : levelBefore);
						upgradeLevel += item.tier;
						// Add additional check to make sure it doesn't go over the max.
						upgrades.put(item.type, Math.min(upgradeLevel, upgradable.getValidUpgrades().get(item.type)));
					}
				}
			}
		}
	}

	public Integer getLevel(UpgradeType type) {
		return upgrades.getOrDefault(type, 0);
	}
}
