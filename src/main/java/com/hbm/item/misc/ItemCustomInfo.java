package com.hbm.item.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCustomInfo extends Item {
    Component describe;
    public ItemCustomInfo(Properties pProperties) {
        this(pProperties, Component.empty());
    }

    public ItemCustomInfo(Properties pProperties, Component describe) {
        super(pProperties);
        this.describe = describe;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(describe);
    }
}
