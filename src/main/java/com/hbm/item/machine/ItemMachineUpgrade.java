package com.hbm.item.machine;

import com.hbm.HBMLang;
import com.hbm.blockentity.interfaces.IUpgradeInfoProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMachineUpgrade extends Item {
    public UpgradeType type;
    public int tier = 0;
    public ItemMachineUpgrade() {
        this(UpgradeType.SPECIAL);
    }

    public ItemMachineUpgrade(Properties properties){
        this(properties, UpgradeType.SPECIAL, 1);
    }

    public ItemMachineUpgrade(UpgradeType type) {
        this(type, 1);
    }

    public ItemMachineUpgrade(UpgradeType type, int tier) {
        this(new Item.Properties().stacksTo(1), type, tier);
    }

    public ItemMachineUpgrade(Properties properties, UpgradeType type, int tier) {
        super(properties);
        this.type = type;
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        Component toAdd;
        switch (this.type){
            case RADIUS -> toAdd = Component.translatable(HBMLang.UPGRADE_RADIUS.key()).withStyle(ChatFormatting.RED);
            case HEALTH -> toAdd = Component.translatable(HBMLang.UPGRADE_HEALTH.key()).withStyle(ChatFormatting.RED);
            case LM_SMELTER -> toAdd = Component.translatable(HBMLang.UPGRADE_SMELTER.key()).withStyle(ChatFormatting.RED);
            case LM_SHREDDER -> toAdd = Component.translatable(HBMLang.UPGRADE_SHREDDER.key()).withStyle(ChatFormatting.RED);
            case LM_CENTRIFUGE -> toAdd = Component.translatable(HBMLang.UPGRADE_CENTRIFUGE.key()).withStyle(ChatFormatting.RED);
            case LM_CRYSTALLIZER -> toAdd = Component.translatable(HBMLang.UPGRADE_CRYSTALLIZER.key()).withStyle(ChatFormatting.RED);
            case LM_SCREM -> toAdd = Component.translatable(HBMLang.UPGRADE_SCREAM.key()).withStyle(ChatFormatting.RED);
            case NULLIFIER -> toAdd = Component.translatable(HBMLang.UPGRADE_NULLIFIER.key()).withStyle(ChatFormatting.RED);
            case GC_SPEED -> toAdd = Component.translatable(HBMLang.UPGRADE_GC_SPEED.key()).withStyle(ChatFormatting.RED);
            default -> toAdd = Component.empty();
        }
        pTooltipComponents.add(toAdd);
    }

    public enum UpgradeType {
        SPEED,
        EFFECT,
        POWER,
        FORTUNE,
        AFTERBURN,
        OVERDRIVE,
        SPECIAL,
        LM_DESROYER,
        LM_SCREM,
        LM_SMELTER(true),
        LM_SHREDDER(true),
        LM_CENTRIFUGE(true),
        LM_CRYSTALLIZER(true),
        GS_SPEED,
        //
        RADIUS,HEALTH,NULLIFIER,GC_SPEED;

        public boolean mutex = false;

        private UpgradeType() { }

        private UpgradeType(boolean mutex) {
            this.mutex = mutex;
        }
    }
}
