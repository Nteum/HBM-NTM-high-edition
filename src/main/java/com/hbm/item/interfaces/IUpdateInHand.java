package com.hbm.item.interfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// 放在手里才触发的物品
public interface IUpdateInHand {
    void onUpdate(ItemStack stack, Level level, Entity player);
}
