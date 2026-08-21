package com.hbm.gui.menu;

import com.hbm.blockentity.machine.TileEntityFurnaceBrick;
import com.hbm.core.menu.MenuBase;
import com.hbm.gui.menu.slot.OutputSlotItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuFurnaceBrick extends MenuBase<TileEntityFurnaceBrick> {
    public MenuFurnaceBrick(int pContainerId, Inventory playerInventory, TileEntityFurnaceBrick blockEntity, ContainerData containerData1) {
        super(pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        //input
        this.addSlot(new SlotItemHandler(items, 0, 62, 35));
        //fuel
        this.addSlot(new SlotItemHandler(items, 1, 35, 17));
        //output
        this.addSlot(new OutputSlotItemHandler(items, 2, 116, 35));
        //ash
        this.addSlot(new OutputSlotItemHandler(items, 3, 35, 53));
        addPlayerSlot(playerInventory, 0,0);
    }

    public MenuFurnaceBrick(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (TileEntityFurnaceBrick) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(buf.readInt()));
    }

    public int getBurnTime(){
        return this.containerData.get(0);
    }

    public int getMaxBurnTime(){
        return this.containerData.get(1);
    }

    public int getProgress(){
        return this.containerData.get(2);
    }
}
