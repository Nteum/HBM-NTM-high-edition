package com.hbm.gui.menu;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.blockentity.machine.BarrelEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.gui.menu.slot.UpgradeSlot;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class BarrelMenu extends BaseMachineMenu implements ITileAccess{
    public BlockEntity be;
    public BarrelMenu(int pContainerId, Inventory pPlayerInventory) {
        this( pContainerId, pPlayerInventory, new SimpleContainer(4), new SimpleContainerData(1));
    }
    public BarrelMenu(int pContainerId, Inventory pPlayerInventory,Container inContainer, ContainerData containerData1) {
        super(ModMenuType.BARREL_MENU.get(), pContainerId, inContainer, containerData1);
        this.slotNum = 4;
        this.addSlot(new Slot(container, 0, 53 - 18, 17));
        this.addSlot(new OutputSlot(container, 1, 53 - 18, 53));
        this.addSlot(new Slot(container, 2, 125, 17));
        this.addSlot(new OutputSlot(container, 3, 125, 53));
        addPlayerSlot(pPlayerInventory,0,0);
        this.addDataSlots(containerData);
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
        ModMessages.sendToServer(new C2SSyncTileMessage(be.getBlockPos(), tag));
    }
}
