package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.blockentity.machine.PWRControllerBlockEntity;
import com.hbm.item.pwr.ItemPWRFuel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class PWRMenu extends BaseMachineMenu {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_COOLANT = 2;
    public static final int DATA_COUNT = 14;

    public PWRMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(DATA_COUNT));
    }

    public PWRMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.PWR_MENU.get(), containerId, container, data);
        slotNum = 3;
        this.addSlot(new Slot(container, SLOT_INPUT, 53, 5));
        this.addSlot(new OutputSlot(container, SLOT_OUTPUT, 89, 32));
        this.addSlot(new Slot(container, SLOT_COOLANT, 8, 59));
        addPlayerSlot(playerInventory, 0, 22);
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
                if (current.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (!moveItemStackTo(current, SLOT_COOLANT, SLOT_COOLANT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (ItemPWRFuel.isFreshFuel(current)) {
                    if (!moveItemStackTo(current, SLOT_INPUT, SLOT_INPUT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.container instanceof PWRControllerBlockEntity controller) {
            controller.setRodTarget(id);
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public int getCoreHeat() {
        return containerData.get(0);
    }

    public int getCoreHeatCapacity() {
        return containerData.get(1);
    }

    public int getHullHeat() {
        return containerData.get(2);
    }

    public int getHullHeatCapacity() {
        return containerData.get(3);
    }

    public int getFluxScaled() {
        return containerData.get(4);
    }

    public int getProgress() {
        return containerData.get(5);
    }

    public int getProcessTime() {
        return containerData.get(6);
    }

    public int getRodLevelScaled() {
        return containerData.get(7);
    }

    public int getRodTargetScaled() {
        return containerData.get(8);
    }

    public int getFuelType() {
        return containerData.get(9);
    }

    public int getFuelAmount() {
        return containerData.get(10);
    }

    public int getRodCount() {
        return containerData.get(11);
    }

    public int getCoolantAmount() {
        return containerData.get(12);
    }

    public int getHotCoolantAmount() {
        return containerData.get(13);
    }
}
