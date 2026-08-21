package com.hbm.gui.menu;

import com.hbm.blockentity.machine.AshpitEntityBE;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 灰烬收集器菜单。
 * 槽位：0-4 共 5 个灰烬槽位（只能取出）。
 */
public class AshpitMenu extends BaseMachineMenu<AshpitEntityBE> {
    public AshpitMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(AshpitEntityBE.class, Minecraft.getInstance().level, buf.readBlockPos()));
    }
    public AshpitMenu(int pContainerId, Inventory pPlayerInventory, AshpitEntityBE be) {
        super(HBMMenus.getById("machine_ashpit"), pContainerId, pPlayerInventory, be, new SimpleContainerData(0));
        slotNum = 5;
        for(int i = 0; i < 5; i++) {
            final int slotIndex = i;
            this.addSlot(new SlotItemHandler(be.getItemHandler(), i, 44 + i * 18, 27) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
        addPlayerSlot(pPlayerInventory, 0, 2);
    }

    public AshpitEntityBE getEntity(){
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
            }
            if (itemStack1.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}
