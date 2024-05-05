package com.hbm.item.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;

public class ArmorUtils {
    //检测玩家身上的盔甲是否都是同一套
    public static boolean checkSuit(PlayerEntity player, ArmorMaterial material){
        for (ItemStack armorItem : player.getArmorItems()) {
            if (!(armorItem.getItem() instanceof ArmorItem) || ((ArmorItem)armorItem.getItem()).getMaterial() != material)return false;
        }
        return true;
    }
}
