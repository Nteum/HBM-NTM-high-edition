package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.blockentity.machine.tokamak.TokamakControllerBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

/**
 * 托卡马克控制器菜单。
 * 槽位定义：
 * 0-氘燃料 1-氚燃料 2-冷却剂 3-预留控制槽 4-副产物 5-电池充电槽
 */
public class TokamakMenu extends BaseMachineMenu {

    public TokamakMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(6), new SimpleContainerData(6));
    }

    public TokamakMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.TOKAMAK_MENU.get(), containerId, container, data);
        slotNum = 6;
        // 燃料/冷却槽
        this.addSlot(new Slot(container, 0, 26, 24)); // D
        this.addSlot(new Slot(container, 1, 44, 24)); // T
        this.addSlot(new Slot(container, 2, 62, 24)); // Coolant
        this.addSlot(new Slot(container, 3, 26, 54)); // Control item
        this.addSlot(new Slot(container, 4, 44, 54)); // Byproduct
        this.addSlot(new Slot(container, 5, 62, 54)); // Power dummy

        addPlayerSlot(playerInventory, 0, 0);
        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            stack = current.copy();
            if (index < slotNum) {
                if (!moveItemStackTo(current, slotNum, slotNum + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (current.is(ModItems.CELL_DEUTERIUM.get())) {
                    if (!moveItemStackTo(current, 0, 1, false)) return ItemStack.EMPTY;
                } else if (current.is(ModItems.CELL_TRITIUM.get())) {
                    if (!moveItemStackTo(current, 1, 2, false)) return ItemStack.EMPTY;
                } else if (isChargeable(current)) {
                    if (!moveItemStackTo(current, 5, 6, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(current, 2, 5, false)) return ItemStack.EMPTY;
                }
            }
            if (current.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    private static boolean isChargeable(final ItemStack stack) {
        return stack.is(ModTags.Items.BATTERY)
                || stack.getCapability(HBMCaps.LONG_ENERGY).isPresent()
                || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.container instanceof TokamakControllerBlockEntity controller) {
            controller.setManualRunning(id == 1);
            return true;
        }
        return super.clickMenuButton(player, id);
    }
}
