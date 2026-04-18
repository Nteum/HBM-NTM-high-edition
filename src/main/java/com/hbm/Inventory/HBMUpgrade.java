package com.hbm.Inventory;

import com.hbm.HBMKey;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.registries.ModItems;
import net.minecraft.world.item.ItemStack;

/**
 * 提供一些升级功能
 * */
public class HBMUpgrade {
    public static ItemStack setTier(ItemStack upgrade, int tier){
        if (upgrade.is(ModItems.UPGRADE_BASE.get()) && !(upgrade.getItem() instanceof ItemMachineUpgrade)) return upgrade;
        upgrade.getOrCreateTag().putInt(HBMKey.UPGRADE, tier);
        return upgrade;
    }

    public static int getTier(ItemStack upgrade){
        if (upgrade.is(ModItems.UPGRADE_BASE.get()) || !(upgrade.getItem() instanceof ItemMachineUpgrade)) return 0;
        int tier = ((ItemMachineUpgrade) upgrade.getItem()).tier;
        if (tier > 0) return tier;
        else return upgrade.getOrCreateTag().getInt(HBMKey.UPGRADE);
    }
}
