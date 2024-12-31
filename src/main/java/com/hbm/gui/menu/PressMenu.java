package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 火力锻压机GUI
 * */
public class PressMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData containerData;
    public PressMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId,pPlayerInventory,new SimpleContainer(4),new SimpleContainerData(2));
    }

    public PressMenu(int pContainerId, Inventory pPlayerInventory,Container inContainer, ContainerData containerData1){
        super(ModMenuType.PRESS_MENU.get(), pContainerId);
        container = inContainer;
        containerData = containerData1;
        this.addSlot(new Slot(container,0,26,53));
        this.addSlot(new Slot(container,1,80,17));
        this.addSlot(new Slot(container,2,80,53));
        this.addSlot(new Slot(container,3,140,35));
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
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }
}
