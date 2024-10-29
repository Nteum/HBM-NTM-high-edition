package com.hbm.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
/**
 * 本mod中添加了一些个性化元素的物品总类
 * */
public class ItemCustomized extends Item {
    public ItemCustomized(Properties pProperties) {
        super(pProperties);
    }
    /**
     * 为物品添加tooltip
     * */
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
