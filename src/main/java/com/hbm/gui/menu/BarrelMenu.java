package com.hbm.gui.menu;

import com.hbm.HBMKey;
import com.hbm.blockentity.machine.BarrelEntityBE;
import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.core.menu.MenuBase;
import com.hbm.gui.HBMMenus;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.core.network.HBMNetwork;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BarrelMenu extends MenuBase<BarrelEntityBE> implements ITileAccess{
    public BlockEntity be;
    public BarrelMenu(int pContainerId, Inventory pPlayerInventory,BarrelEntityBE inContainer, ContainerData containerData1) {
        super(pContainerId, pPlayerInventory, inContainer, containerData1);
        this.slotNum = 4;
        this.addSlot(new Slot(container, 0, 53 - 18, 17));
        this.addSlot(new OutputSlot(container, 1, 53 - 18, 53));
        this.addSlot(new Slot(container, 2, 125, 17));
        this.addSlot(new OutputSlot(container, 3, 125, 53));
        addPlayerSlot(pPlayerInventory,0,0);
        this.addDataSlots(containerData);
    }
    public BarrelMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (BarrelEntityBE) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(1));
    }

    public int getMode(){
        return this.containerData.get(0);
    }
    public int changeMode(){
        int newMode = (getMode()+1)%4;
        this.containerData.set(0,newMode);
        return newMode;
    }

    @Override
    public void setTile(BlockEntity blockEntity) {
        this.be = blockEntity;
    }
    public void syncTile(){
        CompoundTag tag = new CompoundTag();
        tag.putInt(HBMKey.MODE, getMode());
        HBMNetwork.sendToServer(new C2SSyncTileMessage(be.getBlockPos(), tag));
    }
}
