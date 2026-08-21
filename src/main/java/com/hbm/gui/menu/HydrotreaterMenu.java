package com.hbm.gui.menu;

import com.hbm.blockentity.machine.HydrotreaterEntity;
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
 * 加氢装置菜单。11 槽：0电池/1-2油/3-4氢/5-6脱硫油/7-8酸气/9罐类型/10催化剂。
 */
public class HydrotreaterMenu extends BaseMachineMenu<HydrotreaterEntity> {
    public HydrotreaterMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(HydrotreaterEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public HydrotreaterMenu(int pContainerId, Inventory pPlayerInventory, HydrotreaterEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_hydrotreater"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 11;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 17, 90));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 90));
        this.addSlot(new SlotItemHandler(handler, 2, 35, 108));
        this.addSlot(new SlotItemHandler(handler, 3, 53, 90));
        this.addSlot(new SlotItemHandler(handler, 4, 53, 108));
        this.addSlot(new SlotItemHandler(handler, 5, 125, 90));
        this.addSlot(new SlotItemHandler(handler, 6, 125, 108));
        this.addSlot(new SlotItemHandler(handler, 7, 143, 90));
        this.addSlot(new SlotItemHandler(handler, 8, 143, 108));
        this.addSlot(new SlotItemHandler(handler, 9, 17, 108));
        this.addSlot(new SlotItemHandler(handler, 10, 89, 36));
        addPlayerSlot(pPlayerInventory, 0, 88);
        this.addDataSlots(containerData1);
    }

    public HydrotreaterEntity getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 0, slotNum, false))
                    return ItemStack.EMPTY;
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
