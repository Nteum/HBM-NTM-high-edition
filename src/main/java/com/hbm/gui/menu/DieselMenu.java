package com.hbm.gui.menu;

import com.hbm.blockentity.machine.DieselEntityBE;
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
 * 柴油发电机菜单。槽位：0燃料桶、1空桶、2电池、3罐类型。
 */
public class DieselMenu extends BaseMachineMenu<DieselEntityBE> {
    public DieselMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(DieselEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public DieselMenu(int pContainerId, Inventory pPlayerInventory, DieselEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_diesel"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 4;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 35, 90));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 108));
        this.addSlot(new SlotItemHandler(handler, 2, 134, 72));
        this.addSlot(new SlotItemHandler(handler, 3, 35, 54));
        addPlayerSlot(pPlayerInventory, 0, 54);
        this.addDataSlots(containerData1);
    }

    public DieselEntityBE getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 0, 4, false))
                    return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
