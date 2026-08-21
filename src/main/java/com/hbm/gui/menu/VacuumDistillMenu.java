package com.hbm.gui.menu;

import com.hbm.blockentity.machine.VacuumDistillEntity;
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
 * 真空蒸馏塔菜单。
 * 槽位：0 电池、1-10 桶出入、11 罐ID。
 */
public class VacuumDistillMenu extends BaseMachineMenu<VacuumDistillEntity> {
    public VacuumDistillMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(VacuumDistillEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public VacuumDistillMenu(int pContainerId, Inventory pPlayerInventory, VacuumDistillEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_vacuum_distill"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 12;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 26, 90));
        this.addSlot(new SlotItemHandler(handler, 1, 44, 90));
        this.addSlot(new SlotItemHandler(handler, 2, 44, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 80, 90));
        this.addSlot(new SlotItemHandler(handler, 4, 80, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 5, 98, 90));
        this.addSlot(new SlotItemHandler(handler, 6, 98, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 7, 116, 90));
        this.addSlot(new SlotItemHandler(handler, 8, 116, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 9, 134, 90));
        this.addSlot(new SlotItemHandler(handler, 10, 134, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 11, 26, 108));
        addPlayerSlot(pPlayerInventory, 0, 72);
        this.addDataSlots(containerData1);
    }

    public VacuumDistillEntity getEntity(){ return this.be; }
}
