package com.hbm.gui.menu;

import com.hbm.block.machine.MachineOreSlopper;
import com.hbm.blockentity.machine.TileMinerLarge;
import com.hbm.blockentity.machine.TileOreSloppper;
import com.hbm.gui.ModMenuType;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class MenuOreSlopper extends BaseMachineMenu<TileOreSloppper> {
    public MenuOreSlopper(int pContainerId, Inventory playerInventory, TileOreSloppper blockEntity, ContainerData containerData1) {
        super(ModMenuType.typesMaps.get("menu_" + MachineOreSlopper.name).get(), pContainerId, playerInventory, blockEntity, containerData1);
    }
    public MenuOreSlopper(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, WorldUtils.getTileEntity(TileOreSloppper.class, Minecraft.getInstance().level, buf.readBlockPos()), new SimpleContainerData(3));
    }
}
