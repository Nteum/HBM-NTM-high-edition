package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.blockentity.machine.icf.ICFReactorBlockEntity;

import com.hbm.item.icf.ItemICFPellet;
import com.hbm.registries.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;

public class ICFMenu extends BaseMachineMenu {

    public static final int SLOT_COUNT = 12;
    private static final int PLAYER_INV_X_OFFSET = 36;
    private static final int PLAYER_INV_Y_OFFSET = 56; // gui_icf.png uses a 222px canvas (vanilla 166px -> +56 offset)

    public ICFMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(4));
    }

    public ICFMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuType.ICF_MENU.get(), containerId, container, data);
        this.slotNum = SLOT_COUNT;
        // Input pellets
        for (int i = 0; i < 5; i++) {
            this.addSlot(new Slot(container, i, 80 + i * 18, 18));
        }
        // Active pellet slot
        this.addSlot(new Slot(container, ICFReactorBlockEntity.SLOT_ACTIVE, 116, 54));
        // Output slots
        for (int i = 0; i < 5; i++) {
            this.addSlot(new OutputSlot(container, ICFReactorBlockEntity.SLOT_OUTPUT_START + i, 80 + i * 18, 90));
        }
        // Coolant IO slot
        this.addSlot(new Slot(container, ICFReactorBlockEntity.SLOT_COOLANT, 44, 90));
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
                if (current.is(ModItems.icf_pellet.get())) {
                    if (!moveItemStackTo(current, 0, ICFReactorBlockEntity.SLOT_ACTIVE, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (current.getItem() instanceof BucketItem) {
                    if (!moveItemStackTo(current, ICFReactorBlockEntity.SLOT_COOLANT, ICFReactorBlockEntity.SLOT_COOLANT + 1, false)) {
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

    public int getHeat() {
        return containerData.get(0);
    }

    public int getCoolant() {
        return containerData.get(1);
    }

    public int getHotCoolant() {
        return containerData.get(2);
    }

    public int getLaser() {
        return containerData.get(3);
    }
}
