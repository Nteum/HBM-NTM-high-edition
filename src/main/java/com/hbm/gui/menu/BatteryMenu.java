package com.hbm.gui.menu;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BatteryMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    public BatteryMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(2),new SimpleContainerData(5));
    }

    public BatteryMenu(int pContainerId, Inventory pPlayerInventory, Container inContainer, ContainerData containerData) {
        super(ModMenuType.BATTERY_MENU.get(), pContainerId);
        this.container = inContainer;
        this.data = containerData;
        this.addSlot(new Slot(container, 0, 26,17));
        this.addSlot(new Slot(container, 1, 26,53));
        //玩家背包槽
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
        }
        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()){
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (pIndex == 0 || pIndex == 1){
                if (itemStack1.is(ModTags.Items.BATTERY)){
                    if (!this.moveItemStackTo(itemStack1, 0,1,false)){
                        return ItemStack.EMPTY;
                    }
                }
            }else {
                if (!this.moveItemStackTo(itemStack1, 2, 38, true)){
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

//    @Override
//    public boolean clickMenuButton(Player pPlayer, int pId) {
//        return super.clickMenuButton(pPlayer, pId);
//    }

    //===============gui所需数据====================
    public int getPower(){
        return this.data.get(0);
    }
    public long getPowerRemainingScaled(long i) {
        return (getPower() * i) / this.data.get(1);
    }
    public int getRedLow(){
        return this.data.get(2);
    }
    public int getRedHeight(){
        return this.data.get(3);
    }
    public int getConnPriority(){
        return this.data.get(4);
    }
}
