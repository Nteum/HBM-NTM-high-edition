package com.hbm.gui.menu;

import com.hbm.block.machine.MachineArcFurnace;
import com.hbm.blockentity.machine.TileArcFurnace;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuArcFurnace extends BaseMachineMenu<TileArcFurnace> {
    public MenuArcFurnace(int pContainerId, Inventory playerInventory, TileArcFurnace blockEntity, ContainerData containerData1) {
        super(HBMMenus.getById(MachineArcFurnace.id), pContainerId, playerInventory, blockEntity, containerData1);
        ItemStackHandler items = blockEntity.getItems();
        //Electrodes
        for(int i = 0; i < 3; i++) this.addSlot(new SlotItemHandler(items, i, 62 + i * 18, 22));
        //Battery
        this.addSlot(new SlotItemHandler(items, 3, 8, 108));
        //Upgrade
        this.addSlot(new SlotItemHandler(items, 4, 152, 108));
        //Inputs
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 5; j++)
                this.addSlot(new SlotItemHandler(items, 5 + j + i * 5, 44 + j * 18, 54 + i * 18));
        //IO
        for(int i = 0; i < 5; i++)
            this.addSlot(new SlotItemHandler(items, i + 25, 44 + i * 18, 129));
        //player
        addPlayerSlot(playerInventory, 0, 90);
    }

    public MenuArcFurnace(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileArcFurnace.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(TileArcFurnace.CONTAINER_DATA_COUNT));
    }
}
