package com.hbm.gui.menu;

import com.hbm.blockentity.machine.ArcWelderEntity;
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
 * 电弧焊机菜单。
 * 槽位：0-2 输入、3 输出、4 电池、5 流体罐ID、6-7 升级。
 */
public class ArcWelderMenu extends BaseMachineMenu<ArcWelderEntity> {
    public ArcWelderMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(ArcWelderEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public ArcWelderMenu(int pContainerId, Inventory pPlayerInventory, ArcWelderEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_arc_welder"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 8;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 17, 36));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 36));
        this.addSlot(new SlotItemHandler(handler, 2, 53, 36));
        this.addSlot(new SlotItemHandler(handler, 3, 107, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        this.addSlot(new SlotItemHandler(handler, 4, 152, 72));
        this.addSlot(new SlotItemHandler(handler, 5, 17, 63));
        this.addSlot(new SlotItemHandler(handler, 6, 89, 63));
        this.addSlot(new SlotItemHandler(handler, 7, 107, 63));
        addPlayerSlot(pPlayerInventory, 0, 38);
        this.addDataSlots(containerData1);
    }

    public ArcWelderEntity getEntity(){
        return this.be;
    }
}
