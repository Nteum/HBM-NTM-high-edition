package com.hbm.gui.menu;

import com.hbm.blockentity.machine.RefineryEntity;
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
 * 炼油厂菜单。13 槽：0电池/1-2输入/3-4重油/5-6石脑油/7-8轻油/9-10石油气/11罐类型。
 */
public class RefineryMenu extends BaseMachineMenu<RefineryEntity> {
    public RefineryMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(RefineryEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public RefineryMenu(int pContainerId, Inventory pPlayerInventory, RefineryEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_refinery"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 12;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 158, 108));
        this.addSlot(new SlotItemHandler(handler, 1, 12, 90));
        this.addSlot(new SlotItemHandler(handler, 2, 12, 108));
        this.addSlot(new SlotItemHandler(handler, 3, 64, 90));
        this.addSlot(new SlotItemHandler(handler, 4, 64, 108));
        this.addSlot(new SlotItemHandler(handler, 5, 82, 90));
        this.addSlot(new SlotItemHandler(handler, 6, 82, 108));
        this.addSlot(new SlotItemHandler(handler, 7, 100, 90));
        this.addSlot(new SlotItemHandler(handler, 8, 100, 108));
        this.addSlot(new SlotItemHandler(handler, 9, 118, 90));
        this.addSlot(new SlotItemHandler(handler, 10, 118, 108));
        this.addSlot(new SlotItemHandler(handler, 11, 12, 72));
        addPlayerSlot(pPlayerInventory, 6, 88);
        this.addDataSlots(containerData1);
    }

    public RefineryEntity getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 0, 2, false))
                    if (!this.moveItemStackTo(itemStack1, 11, 12, false))
                        return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
