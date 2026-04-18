package com.hbm.gui.menu;

import com.hbm.blockentity.logistic.TileConveyorInserter;
import com.hbm.gui.ModMenuType;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuConveyorInserter extends BaseMachineMenu<TileConveyorInserter>{
    public MenuConveyorInserter(int pContainerId, Inventory playerInventory, TileConveyorInserter blockEntity, ContainerData containerData1) {
        super(ModMenuType.MENU_CONVEYOR_INSERTER.get(), pContainerId, playerInventory, blockEntity, containerData1);
        this.slotNum = TileConveyorInserter.SLOT_NUM;
        ItemStackHandler items = blockEntity.getItems();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 7; j++) {
                this.addSlot(new SlotItemHandler(items, j + i * 7, 8 + j * 18, 17 + i * 18));
            }
        }
        addPlayerSlot(playerInventory, 0, 19);
    }
    public MenuConveyorInserter(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileConveyorInserter.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(1));
    }
}
