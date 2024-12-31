package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class DifurnaceMenu  extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData containerData;

    public DifurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId,pPlayerInventory,new SimpleContainer(4),new SimpleContainerData(2));
    }
    public DifurnaceMenu(int pContainerId, Inventory pPlayerInventory,Container inContainer, ContainerData containerData1){
        super(ModMenuType.DIFURNACE_MENU.get(), pContainerId);
        container = inContainer;
        containerData = containerData1;
        this.addSlot(new Slot(container,0,8,36));
        this.addSlot(new Slot(container,1,80,18));
        this.addSlot(new Slot(container,2,80,54));
        this.addSlot(new Slot(container,3,134,36));
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(containerData1);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }
    public int getProgress(){return this.containerData.get(0);}

    public int getFuel(){return this.containerData.get(1);}
}
