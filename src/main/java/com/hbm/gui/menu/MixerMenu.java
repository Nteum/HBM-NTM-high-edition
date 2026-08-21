package com.hbm.gui.menu;

import com.hbm.blockentity.machine.MixerEntity;
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
 * 混合机菜单。
 * 槽位：0 电池、1 固体输入、2 流体罐ID、3-4 升级。
 */
public class MixerMenu extends BaseMachineMenu<MixerEntity> {
    public MixerMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(MixerEntity.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(4));
    }
    public MixerMenu(int pContainerId, Inventory pPlayerInventory, MixerEntity be, ContainerData containerData1) {
        super(HBMMenus.getById("machine_mixer"), pContainerId, pPlayerInventory, be, containerData1);
        slotNum = 5;
        var handler = be.getItemHandler();
        this.addSlot(new SlotItemHandler(handler, 0, 12, 72));
        this.addSlot(new SlotItemHandler(handler, 1, 52, 72));
        this.addSlot(new SlotItemHandler(handler, 2, 126, 72));
        this.addSlot(new SlotItemHandler(handler, 3, 148, 18));
        this.addSlot(new SlotItemHandler(handler, 4, 148, 36));
        addPlayerSlot(pPlayerInventory, 0, 38);
        this.addDataSlots(containerData1);
    }

    public MixerEntity getEntity(){ return this.be; }
}
