package com.hbm.gui.menu;

import com.hbm.blockentity.machine.EPressEntityBE;
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
 * 电动锻压机菜单。
 * 槽位：0 电池、1 模板、2 输入、3 输出、4 升级
 */
public class EPressMenu extends BaseMachineMenu<EPressEntityBE> {
    public EPressMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(EPressEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(2));
    }
    public EPressMenu(int pContainerId, Inventory pPlayerInventory, EPressEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_epress"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 5;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 152, 54));
        this.addSlot(new SlotItemHandler(handler, 1, 19, 15));
        this.addSlot(new SlotItemHandler(handler, 2, 19, 51));
        this.addSlot(new SlotItemHandler(handler, 3, 79, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 4, 111, 32));
        addPlayerSlot(pPlayerInventory, 0, 20);
        this.addDataSlots(containerData1);
    }

    public EPressEntityBE getEntity(){
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
                if (!this.moveItemStackTo(itemStack1, 1, 3, false)){
                    return ItemStack.EMPTY;
                }
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
