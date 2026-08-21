package com.hbm.gui.menu;

import com.hbm.blockentity.machine.RTGEntityBE;
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
 * RTG 发电机菜单。
 * 槽位：0-14 共 15 个 RTG 燃料棒槽位。
 */
public class RTGMenu extends BaseMachineMenu<RTGEntityBE> {
    public RTGMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(RTGEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(2));
    }
    public RTGMenu(int pContainerId, Inventory pPlayerInventory, RTGEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_rtg"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 15;
        int[] xs = new int[] { 16, 34, 52, 70, 88 };
        int[] ys = new int[] { 18, 36, 54 };
        int i = 0;
        for (int y : ys) {
            for (int x : xs) {
                this.addSlot(new SlotItemHandler(be.getItemHandler(), i, x, y));
                i++;
            }
        }
        addPlayerSlot(pPlayerInventory, 0, 22);
        this.addDataSlots(containerData1);
    }

    public RTGEntityBE getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 0, slotNum, false)){
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
