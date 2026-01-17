package com.hbm.gui.menu;

import com.hbm.blockentity.machine.research.ResearchReactorBlockEntity;
import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResearchReactorMenu extends BaseMachineMenu {

    private static final int PLAYER_INV_X_OFFSET = 0;
    private static final int PLAYER_INV_Y_OFFSET = 56; // gui height 222px -> shift player inventory down

    public ResearchReactorMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(ResearchReactorBlockEntity.SLOT_COUNT), new SimpleContainerData(5));
    }

    public ResearchReactorMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuType.RESEARCH_REACTOR_MENU.get(), containerId, container, data);
        this.slotNum = ResearchReactorBlockEntity.SLOT_COUNT;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                int index = col + row * 3;
                int x = 53 + col * 18;
                int y = 17 + row * 18;
                this.addSlot(new Slot(container, index, x, y));
            }
        }

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
                if (!moveItemStackTo(current, 0, slotNum, false)) {
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
        if (container instanceof ResearchReactorBlockEntity reactor) {
            if (id >= 1000 && id <= 1100) {
                double pct = (id - 1000) / 100.0D;
                reactor.setTargetLevel(pct);
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }

    public int getFlux() {
        return containerData.get(0);
    }

    public int getHeat() {
        return containerData.get(1);
    }

    public int getControlPercent() {
        return containerData.get(2);
    }

    public int getTargetPercent() {
        return containerData.get(3);
    }

    public int getWaterLevel() {
        return containerData.get(4);
    }
}
