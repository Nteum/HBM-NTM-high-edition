package com.hbm.gui.menu;

import com.hbm.blockentity.machine.KeyForgeEntityBE;
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
 * 钥匙锻造台菜单。槽位：0模板、1复制目标、2随机。
 */
public class KeyForgeMenu extends BaseMachineMenu<KeyForgeEntityBE> {
    public KeyForgeMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(KeyForgeEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public KeyForgeMenu(int pContainerId, Inventory pPlayerInventory, KeyForgeEntityBE be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_keyforge"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 3;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 44, 36));
        this.addSlot(new SlotItemHandler(handler, 1, 80, 36));
        this.addSlot(new SlotItemHandler(handler, 2, 116, 36));
        addPlayerSlot(pPlayerInventory, 0, 36);
        this.addDataSlots(containerData1);
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
