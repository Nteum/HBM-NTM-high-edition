package com.hbm.gui.menu;

import com.hbm.blockentity.machine.BatteryEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.registries.ModTags;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BatteryMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;
    private final ContainerLevelAccess access;
    public BatteryMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(2),new SimpleContainerData(5), ContainerLevelAccess.NULL);
    }

    public BatteryMenu(int pContainerId, Inventory pPlayerInventory, Container inContainer, ContainerData containerData, ContainerLevelAccess access) {
        super(ModMenuType.BATTERY_MENU.get(), pContainerId);
        this.container = inContainer;
        this.data = containerData;
        this.access = access;
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
            if (pIndex >= 2){
                if (itemStack1.is(ModTags.Items.BATTERY)){
                    if (!this.moveItemStackTo(itemStack1, 0,1,false)){
                        return ItemStack.EMPTY;
                    }
                }else {
                    if (pIndex < 29) {
                        if (!this.moveItemStackTo(itemStack1, 29, 38, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (pIndex < 38) {
                        if (!this.moveItemStackTo(itemStack1, 2, 28, false)) {
                            return ItemStack.EMPTY;
                        }
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

    @Override
    public boolean clickMenuButton(Player pPlayer, int pId) {
        boolean flag = false;
        if (pId == 0){
            this.data.set(2, (this.data.get(2) + 1) % 4);
            flag = true;
        }
        else if (pId == 1){
            this.data.set(3, (this.data.get(3) + 1) % 4);
            flag = true;
        }
        else if (pId == 2){
            this.data.set(4, (this.data.get(4) + 1) % 3);
            flag = true;
        }
        return flag || super.clickMenuButton(pPlayer, pId);
    }

    //===============gui所需数据====================
    public int getPower(){
        return this.data.get(0);
    }
    public int getMaxPower(){return this.data.get(1);}
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
