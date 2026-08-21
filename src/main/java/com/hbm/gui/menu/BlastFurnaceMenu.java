package com.hbm.gui.menu;

import com.hbm.blockentity.machine.BlastFurnaceEntity;
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
 * 高炉菜单。
 * 槽位：0 燃料、1-2 输入、3-4 输出。
 */
public class BlastFurnaceMenu extends BaseMachineMenu<BlastFurnaceEntity> {
    public BlastFurnaceMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(BlastFurnaceEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public BlastFurnaceMenu(int pContainerId, Inventory pPlayerInventory, BlastFurnaceEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_blast_furnace"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 5;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 80, 81));
        this.addSlot(new SlotItemHandler(handler, 1, 80, 27));
        this.addSlot(new SlotItemHandler(handler, 2, 80, 45));
        this.addSlot(new SlotItemHandler(handler, 3, 134, 72) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        this.addSlot(new SlotItemHandler(handler, 4, 134, 90) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });
        addPlayerSlot(pPlayerInventory, 0, 56);
        this.addDataSlots(containerData1);
    }

    public BlastFurnaceEntity getEntity(){ return this.be; }
}
