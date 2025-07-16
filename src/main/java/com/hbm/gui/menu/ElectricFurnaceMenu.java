package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.gui.menu.slot.UpgradeSlot;
import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ElectricFurnaceMenu extends BaseMachineMenu{
    public ElectricFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(4),new SimpleContainerData(4));
    }
    public ElectricFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container inContainer, ContainerData containerData1) {
        super(ModMenuType.ELECTRIC_FURNACE_MENU.get(), pContainerId, inContainer, containerData1);
        slotNum = 4;
        this.addSlot(new Slot(container, 0, 56, 17));
        this.addSlot(new BatterySlot(container, 1, 56, 53));
        this.addSlot(new UpgradeSlot(container, 2, 116, 35));
        this.addSlot(new OutputSlot(container, 3, 147, 34));
        addPlayerSlot(pPlayerInventory, 0, 0);
        this.addDataSlots(containerData);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()){
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            //电池
            if (pIndex>=slotNum && itemStack1.is(ModTags.Items.CHARGEABLE)){
                if (!this.moveItemStackTo(itemStack1, 0, slotNum, false)){
                    return ItemStack.EMPTY;
                }
                //升级控件
            }else if (pIndex>=slotNum && itemStack1.is(ModTags.Items.UPGRADE)){
                if (!this.moveItemStackTo(itemStack1, 1, slotNum, false)){
                    return ItemStack.EMPTY;
                }
            }else {
                return super.quickMoveStack(pPlayer, pIndex);
            }
            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemStack;
    }
}
