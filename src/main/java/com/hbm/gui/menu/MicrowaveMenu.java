package com.hbm.gui.menu;

import com.hbm.blockentity.machine.MicrowaveEntityBE;
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
 * 微波炉菜单。
 * 槽位：0输入、1输出、2电池
 */
public class MicrowaveMenu extends BaseMachineMenu<MicrowaveEntityBE> {
    public MicrowaveMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(MicrowaveEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public MicrowaveMenu(int pContainerId, Inventory pPlayerInventory, MicrowaveEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_microwave"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 3;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 80, 35));
        this.addSlot(new SlotItemHandler(handler, 1, 140, 35));
        this.addSlot(new SlotItemHandler(handler, 2, 8, 53));
        addPlayerSlot(pPlayerInventory, 0, 0);
        this.addDataSlots(containerData1);
    }

    public MicrowaveEntityBE getEntity(){
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
            }else {
                if (!this.moveItemStackTo(itemStack1, 0, 1, true))
                    if (!this.moveItemStackTo(itemStack1, 2, 3, true))
                        return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
