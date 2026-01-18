package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.blockentity.machine.icf.ICFPressBlockEntity;

import com.hbm.item.icf.ItemICFPellet;
import com.hbm.registries.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ICFPressMenu extends BaseMachineMenu {

    public static final int SLOT_COUNT = 8;
    private static final int PLAYER_INV_X_OFFSET = 0;
    private static final int PLAYER_INV_Y_OFFSET = 13; // legacy GUI height (179px) -> 13px offset for player inventory

    public ICFPressMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(3));
    }

    public ICFPressMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuType.ICF_PRESS_MENU.get(), containerId, container, data);
        this.slotNum = SLOT_COUNT;
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_EMPTY, 98, 17));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_OUTPUT, 98, 53));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_MUON, 8, 17));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_MUON_RETURN, 8, 53));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_FUEL_LEFT, 62, 53));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_FUEL_RIGHT, 134, 53));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_LEFT_BUFFER, 62, 17));
        this.addSlot(new Slot(container, ICFPressBlockEntity.SLOT_RIGHT_BUFFER, 134, 17));
        addPlayerSlot(inventory, PLAYER_INV_X_OFFSET, PLAYER_INV_Y_OFFSET);
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
            } else if (current.is(ModItems.icf_pellet_empty.get())) {
                if (!moveItemStackTo(current, ICFPressBlockEntity.SLOT_EMPTY, ICFPressBlockEntity.SLOT_EMPTY + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (current.is(ModItems.PARTICLE_MUON.get())) {
                if (!moveItemStackTo(current, ICFPressBlockEntity.SLOT_MUON, ICFPressBlockEntity.SLOT_MUON + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (ItemICFPellet.fuelFromStack(current) != null) {
                if (!moveItemStackTo(current, ICFPressBlockEntity.SLOT_LEFT_BUFFER, ICFPressBlockEntity.SLOT_RIGHT_BUFFER + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    public int getMuonCharge() {
        return containerData.get(0);
    }

    public int getLeftFluid() {
        return containerData.get(1);
    }

    public int getRightFluid() {
        return containerData.get(2);
    }
}
