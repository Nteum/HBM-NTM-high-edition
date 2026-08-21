package com.hbm.gui.menu;

import com.hbm.blockentity.machine.FunnelEntityBE;
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
 * 组合漏斗菜单。槽位：0-8 输入、9-17 输出。
 */
public class FunnelMenu extends BaseMachineMenu<FunnelEntityBE> {
    public FunnelMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(FunnelEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(1));
    }
    public FunnelMenu(int pContainerId, Inventory pPlayerInventory, FunnelEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_funnel"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 18;
        var handler = be.getItemHandler();
        for (int i = 0; i < 9; i++) this.addSlot(new SlotItemHandler(handler, i, 8 + 18 * i, 18));
        for (int i = 0; i < 9; i++) this.addSlot(new SlotItemHandler(handler, i + 9, 8 + 18 * i, 54));
        addPlayerSlot(pPlayerInventory, 0, 32);
        this.addDataSlots(containerData1);
    }

    public FunnelEntityBE getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 0, 9, false)){
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
