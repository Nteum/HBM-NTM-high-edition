package com.hbm.gui.menu;

import com.hbm.blockentity.machine.BERtgFurnace;
import com.hbm.core.menu.MenuBase;
import com.hbm.gui.menu.slot.OutputSlotItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuRtgFurnace extends MenuBase<BERtgFurnace> {
    public MenuRtgFurnace(int pContainerId, Inventory playerInventory, BERtgFurnace blockEntity, ContainerData containerData1) {
        super(pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        this.addSlot(new SlotItemHandler(items, 0, 56, 17));
        this.addSlot(new SlotItemHandler(items, 1, 38, 53));
        this.addSlot(new SlotItemHandler(items, 2, 56, 53));
        this.addSlot(new SlotItemHandler(items, 3, 74, 53));
        this.addSlot(new OutputSlotItemHandler(items, 4, 116, 35));
        addPlayerSlot(playerInventory, 0, 0);
    }

    public MenuRtgFurnace(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (BERtgFurnace) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(buf.readInt()));
    }

    public boolean hasHeat(){
        return this.containerData.get(0) != 0;
    }

    public int getCookTime(){
        return this.containerData.get(1);
    }
}
