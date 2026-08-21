package com.hbm.gui.menu;

import com.hbm.blockentity.machine.TileEntityMachineElectricFurnace;
import com.hbm.core.menu.MenuBase;
import com.hbm.gui.menu.slot.OutputSlotItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuFurnaceElectric extends MenuBase<TileEntityMachineElectricFurnace> {
    public MenuFurnaceElectric(int pContainerId, Inventory playerInventory, TileEntityMachineElectricFurnace blockEntity, ContainerData containerData1) {
        super(pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        this.addSlot(new SlotItemHandler(items, 0, 152, 54));
        this.addSlot(new SlotItemHandler(items, 1, 20, 35));
        this.addSlot(new OutputSlotItemHandler(items, 2, 80, 35));
        //Upgrades
        this.addSlot(new SlotItemHandler(items, 3, 111, 34));
        addPlayerSlot(playerInventory, 0, 20);
    }

    public MenuFurnaceElectric(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (TileEntityMachineElectricFurnace) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(buf.readInt()));
    }

    public int getPower(){
        return this.containerData.get(0);
    }

    public int getMaxProgress(){
        return this.containerData.get(1);
    }

    public int getProgress(){
        return this.containerData.get(2);
    }
}
