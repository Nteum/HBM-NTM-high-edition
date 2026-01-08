package com.hbm.gui.menu;

import com.hbm.blockentity.machine.research.BreederReactorBlockEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BreederReactorMenu extends BaseMachineMenu {

    private static final int PLAYER_INV_X_OFFSET = 0;
    private static final int PLAYER_INV_Y_OFFSET = 0; // vanilla layout (GUI height 166px)

    public BreederReactorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(2), new SimpleContainerData(2));
    }

    public BreederReactorMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuType.BREEDER_REACTOR_MENU.get(), containerId, container, data);
        this.slotNum = 2;
        this.addSlot(new Slot(container, 0, 56, 35));
        this.addSlot(new OutputSlot(container, 1, 116, 35));
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
            } else {
                if (!moveItemStackTo(current, 0, 1, false)) {
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

    public int getFlux() {
        return containerData.get(0);
    }

    public int getProgressPercent() {
        return containerData.get(1);
    }
}
