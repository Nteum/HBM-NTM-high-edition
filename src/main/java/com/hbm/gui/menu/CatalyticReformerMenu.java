package com.hbm.gui.menu;

import com.hbm.blockentity.machine.CatalyticReformerEntity;
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
 * 催化重整器菜单。
 * 槽位：0 电池、1-8 桶出入、9 罐ID、10 催化剂。
 */
public class CatalyticReformerMenu extends BaseMachineMenu<CatalyticReformerEntity> {
    public CatalyticReformerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(CatalyticReformerEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(0));
    }
    public CatalyticReformerMenu(int pContainerId, Inventory pPlayerInventory, CatalyticReformerEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_catalytic_reformer"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 11;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 17, 90));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 90));
        this.addSlot(new SlotItemHandler(handler, 2, 35, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 3, 107, 90));
        this.addSlot(new SlotItemHandler(handler, 4, 107, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 5, 125, 90));
        this.addSlot(new SlotItemHandler(handler, 6, 125, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 7, 143, 90));
        this.addSlot(new SlotItemHandler(handler, 8, 143, 108) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 9, 17, 108));
        this.addSlot(new SlotItemHandler(handler, 10, 71, 36));
        addPlayerSlot(pPlayerInventory, 0, 72);
        this.addDataSlots(containerData1);
    }

    public CatalyticReformerEntity getEntity(){ return this.be; }
}
