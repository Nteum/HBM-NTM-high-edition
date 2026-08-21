package com.hbm.Inventory;

import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.item.machine.ItemMachineUpgrade.UpgradeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 机器升级管理器（修复版）。
 *
 * 旧版 UpgradeManagerNT 的核心扫描逻辑（checkSlotsInternal）在移植时被整个注释掉，
 * 导致 getLevel 永远返回 0，升级系统实际失效。本类恢复并现代化该逻辑：
 * 1. 扫描机器物品槽中的 ItemMachineUpgrade
 * 2. 依据 IUpgradeInfoProvider.getValidUpgrades() 过滤机器可接受的升级类型
 * 3. 同类型叠加 tier，并用机器上限封顶
 * 4. 处理互斥类型（mutex）：只保留优先级最高的一种
 * 5. 槽位内容不变时跳过重扫（缓存）
 *
 * 用法：
 * 1. TE 实现 IUpgradeInfoProvider（getValidUpgrades 声明可接受的类型与上限）
 * 2. TE 创建本管理器，每 tick 调用 checkSlots
 * 3. 通过 getLevel(type) 读取等级，或通过 applyUpgradeEffects 应用倍率
 */
public class UpgradeManagerNT {

	public BlockEntity owner;
	public ItemStack[] cachedSlots;

	private UpgradeType mutexType;
	public HashMap<UpgradeType, Integer> upgrades = new HashMap<>();

	public UpgradeManagerNT(BlockEntity te) { this.owner = te; }
	@Deprecated public UpgradeManagerNT() { }

	public void checkSlots(ItemStack[] slots, int start, int end) { checkSlotsInternal(owner, slots, start, end); }
	public void checkSlots(BlockEntity te, java.util.List<ItemStack> slots, int start, int end) {
		ItemStack[] itemStacks = slots.toArray(new ItemStack[0]);
		checkSlotsInternal(te, itemStacks, start, end);
	}

	private void checkSlotsInternal(BlockEntity te, ItemStack[] slots, int start, int end) {

		if(!(te instanceof IUpgradeInfoProvider upgradable) || slots == null)
			return;

		if(end < start || start < 0 || end >= slots.length)
			return;

		ItemStack[] upgradeSlots = java.util.Arrays.copyOfRange(slots, start, end + 1);

		// 槽位内容未变化则跳过重扫
		if(java.util.Arrays.equals(upgradeSlots, cachedSlots))
			return;

		cachedSlots = upgradeSlots.clone();

		upgrades.clear();
		mutexType = null;

		Map<UpgradeType, Integer> valid = upgradable.getValidUpgrades();
		if (valid == null || valid.isEmpty()) return;

		for (int i = 0; i < upgradeSlots.length; i++) {

			ItemStack stack = upgradeSlots[i];
			if(stack == null || stack.isEmpty() || !(stack.getItem() instanceof ItemMachineUpgrade item))
				continue;

			// 机器不接受该类型则跳过
			if (!valid.containsKey(item.type)) continue;

			if (item.type.mutex) {
				// 互斥升级：只保留优先级最高的一种（mutex 标记在 UpgradeType 上）
				if (mutexType == null) {
					upgrades.put(item.type, 1);
					mutexType = item.type;
				} else if (item.type.ordinal() > mutexType.ordinal()) {
					upgrades.remove(mutexType);
					upgrades.put(item.type, 1);
					mutexType = item.type;
				}
			} else {
				Integer levelBefore = upgrades.get(item.type);
				int upgradeLevel = (levelBefore == null ? 0 : levelBefore);
				upgradeLevel += item.tier;
				// 用机器上限封顶
				upgrades.put(item.type, Math.min(upgradeLevel, valid.get(item.type)));
			}
		}
	}

	public Integer getLevel(UpgradeType type) {
		return upgrades.getOrDefault(type, 0);
	}

	public int getSpeedLevel(){
		return getLevel(UpgradeType.SPEED);
	}
	public int getPowerLevel(){
		return getLevel(UpgradeType.POWER);
	}
	public int getEffectLevel(){
		return getLevel(UpgradeType.EFFECT);
	}
	public int getOverdriveLevel(){
		return getLevel(UpgradeType.OVERDRIVE);
	}
	public int getFortuneLevel(){
		return getLevel(UpgradeType.FORTUNE);
	}
	public int getAfterburnLevel(){
		return getLevel(UpgradeType.AFTERBURN);
	}

	public void clear(){
		this.upgrades.clear();
		this.cachedSlots = null;
		this.mutexType = null;
	}
}
