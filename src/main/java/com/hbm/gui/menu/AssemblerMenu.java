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
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class AssemblerMenu extends BaseMachineMenu{
    int[][] inputSlotPos = {{8, 18},{26, 18},{8, 36},{26, 36},{8, 54},{26, 54},{8, 72},{26, 72},{8, 90},{26, 90},{8, 108},{26, 108}};
    public AssemblerMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId,pPlayerInventory,new SimpleContainer(17),new SimpleContainerData(3));
    }
    public AssemblerMenu(int pContainerId, Inventory pPlayerInventory, Container inContainer, ContainerData containerData1) {
        super(ModMenuType.ASSEMBLER_MENU.get(), pContainerId, inContainer, containerData1);
        slotNum = 13;
        //battery
        this.addSlot(new BatterySlot(container, 0, 80, 18));
        //upgrade
        this.addSlot(new UpgradeSlot(container, 1, 152, 18));
        this.addSlot(new UpgradeSlot(container, 2, 152, 36));
        this.addSlot(new UpgradeSlot(container, 3, 152, 54));
        //output
        this.addSlot(new OutputSlot(container, 4, 134, 90));
        //input
        addSlotWithPos(5,inputSlotPos);
        //player
        addPlayerSlot(pPlayerInventory,0,56);
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

    public int getEnergy(){
        return containerData.get(1);
    }
    public double getProgress(){
        return (double) containerData.get(0) /100;
    }
    public double getEnergyRate(){
        return (double) containerData.get(1) / containerData.get(2);
    }
}
