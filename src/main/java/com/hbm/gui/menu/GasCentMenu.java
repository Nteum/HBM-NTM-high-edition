package com.hbm.gui.menu;

import com.hbm.blockentity.machine.GasCentEntity;
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
 * 气体离心机菜单。
 * 槽位：0-3 输出、4 电池、5 罐ID、6 升级。
 */
public class GasCentMenu extends BaseMachineMenu<GasCentEntity> {
    public GasCentMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(GasCentEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public GasCentMenu(int pContainerId, Inventory pPlayerInventory, GasCentEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_gascent"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 7;
        var handler = be.getItemHandler();
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                int index = j + i * 2;
                this.addSlot(new SlotItemHandler(handler, index, 71 + j * 18, 53 + i * 18) {
                    @Override public boolean mayPlace(ItemStack stack) { return false; }
                });
            }
        }
        this.addSlot(new SlotItemHandler(handler, 4, 182, 71));
        this.addSlot(new SlotItemHandler(handler, 5, 91, 15));
        this.addSlot(new SlotItemHandler(handler, 6, 69, 15));
        addPlayerSlot(pPlayerInventory, 0, 38);
        this.addDataSlots(containerData1);
    }

    public GasCentEntity getEntity(){ return this.be; }
}
