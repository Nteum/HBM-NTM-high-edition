package com.hbm.gui.menu;

import com.hbm.blockentity.machine.CryoDistillEntity;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * 低温蒸馏器菜单。
 * 槽位：0 电池、1-6/8-9 桶出入、7 罐ID。
 */
public class CryoDistillMenu extends BaseMachineMenu<CryoDistillEntity> {
    public CryoDistillMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(CryoDistillEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public CryoDistillMenu(int pContainerId, Inventory pPlayerInventory, CryoDistillEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_cryo_distill"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 10;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 26, 90));
        this.addSlot(new SlotItemHandler(handler, 1, 80, 90));
        this.addSlot(new SlotItemHandler(handler, 2, 80, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 98, 90));
        this.addSlot(new SlotItemHandler(handler, 4, 98, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 5, 116, 90));
        this.addSlot(new SlotItemHandler(handler, 6, 116, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 7, 26, 108));
        this.addSlot(new SlotItemHandler(handler, 8, 134, 90));
        this.addSlot(new SlotItemHandler(handler, 9, 134, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addPlayerSlot(pPlayerInventory, 0, 72);
        this.addDataSlots(containerData1);
    }

    public CryoDistillEntity getEntity(){ return this.be; }
}
