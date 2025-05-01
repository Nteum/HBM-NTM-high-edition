package com.hbm.gui.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class BaseMachineMenu extends AbstractContainerMenu {
    public Container container;
    public ContainerData containerData;
    public int slotNum = 0;

    protected BaseMachineMenu(@Nullable MenuType<?> pMenuType, int pContainerId,Container inContainer, ContainerData containerData1) {
        super(pMenuType, pContainerId);
        container = inContainer;
        containerData = containerData1;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()){
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (pIndex < slotNum){
                if (!this.moveItemStackTo(itemStack1, slotNum, slotNum+36, true)){
                    return ItemStack.EMPTY;
                }
            }else {
                if (!this.moveItemStackTo(itemStack1, 0, slotNum, false)){
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    void addSlotWithPos(Container container, int StartIdx, int[][] slotPos){
        for (int i = 0; i < slotPos.length; i++) {
            this.addSlot(new Slot(container, StartIdx+i, slotPos[i][0], slotPos[i][1]));
        }
    }
    void addPlayerSlot(Inventory pPlayerInventory,int xOffset,int yOffset){
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18 + xOffset, 84 + i * 18 + yOffset));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18 + xOffset, 142 + yOffset));
        }
    }
}
