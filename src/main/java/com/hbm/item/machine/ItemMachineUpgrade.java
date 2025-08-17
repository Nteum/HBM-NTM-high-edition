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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMachineUpgrade extends Item {
    public UpgradeType type;
    public int tier = 0;
    public ItemMachineUpgrade() {
        super(new Item.Properties().stacksTo(1));
        this.type = UpgradeType.SPECIAL;
    }

    public ItemMachineUpgrade(UpgradeType type) {
        super(new Item.Properties().stacksTo(1));
        this.type = type;
    }

    public ItemMachineUpgrade(UpgradeType type, int tier) {
        this(type);
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

//        GuiScreen open = Minecraft.getMinecraft().currentScreen;
//
//        if(open != null && open instanceof GuiContainer) {
//            GuiContainer guiContainer = (GuiContainer) open;
//            Container container = guiContainer.inventorySlots;
//            if(container.inventorySlots.size() > 0) {
//                Slot first = container.getSlot(0);
//                IInventory inv = (IInventory) first.inventory;
//                if(inv instanceof IUpgradeInfoProvider) {
//                    IUpgradeInfoProvider provider = (IUpgradeInfoProvider) inv;
//                    if(provider.canProvideInfo(this.type, this.tier, bool)) {
//                        provider.provideInfo(this.type, this.tier, list, bool);
//                        return;
//                    }
//                }
//            }
//        }

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

    public static enum UpgradeType {
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
