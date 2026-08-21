package com.hbm.gui.menu;

import com.hbm.blockentity.machine.SolidifierEntity;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 工业固化机菜单。槽位：0输出、1电池、2/3升级、4罐类型。
 */
public class SolidifierMenu extends BaseMachineMenu<SolidifierEntity> {
    public SolidifierMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(SolidifierEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public SolidifierMenu(int pContainerId, Inventory pPlayerInventory, SolidifierEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_solidifier"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 5;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 71, 45));
        this.addSlot(new SlotItemHandler(handler, 1, 134, 72));
        this.addSlot(new SlotItemHandler(handler, 2, 98, 36));
        this.addSlot(new SlotItemHandler(handler, 3, 98, 54));
        this.addSlot(new SlotItemHandler(handler, 4, 71, 72));
        addPlayerSlot(pPlayerInventory, 0, 54);
        this.addDataSlots(containerData1);
    }

    public SolidifierEntity getEntity(){
        return this.be;
    }

    @Override
    public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player pPlayer, int pIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()){
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (pIndex < slotNum){
                if (!this.moveItemStackTo(itemStack1, slotNum, slotNum + 36, true)){
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemStack1, 0, 1, false))
                    if (!this.moveItemStackTo(itemStack1, 2, 4, false))
                        return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
