package com.hbm.gui.menu;

import com.hbm.blockentity.machine.CokerEntity;
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
 * 焦化装置菜单。槽位：0罐类型、1输出。
 */
public class CokerMenu extends BaseMachineMenu<CokerEntity> {
    public CokerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(CokerEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public CokerMenu(int pContainerId, Inventory pPlayerInventory, CokerEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_coker"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 2;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 35, 72));
        this.addSlot(new SlotItemHandler(handler, 1, 97, 27));
        addPlayerSlot(pPlayerInventory, 0, 54);
        this.addDataSlots(containerData1);
    }

    public CokerEntity getEntity(){
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
                    return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
