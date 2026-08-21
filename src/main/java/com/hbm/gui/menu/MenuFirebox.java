package com.hbm.gui.menu;

import com.hbm.blockentity.machine.DifurnaceEntity;
import com.hbm.blockentity.machine.TileFireboxBase;
import com.hbm.core.menu.MenuBase;
import com.hbm.gui.HBMMenus;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuFirebox extends MenuBase<TileFireboxBase> {
    public TileFireboxBase be;
    public MenuFirebox(int pContainerId, Inventory pPlayerInventory, TileFireboxBase tile, ContainerData containerData) {
        super(pContainerId, pPlayerInventory, tile, containerData);
        be = tile;
        IItemHandler handler = tile.getItemHandler();
        // 燃料槽
        this.slotNum = 2;
        this.addSlot(new SlotItemHandler(handler, 0,44, 27));
        this.addSlot(new SlotItemHandler(handler, 1,62, 27));
        // 玩家物品槽
        addPlayerSlot(pPlayerInventory, 0,2);
    }

    public MenuFirebox(int id, Inventory playerInventory, FriendlyByteBuf buf) {
        this(id, playerInventory, (TileFireboxBase) Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()), new SimpleContainerData(buf.readInt()));
    }

    public int getMaxBurnTime(){
        return this.containerData.get(0);
    }
    public int getBurnTime(){
        return this.containerData.get(1);
    }
    public int getBurnHeat(){
        return this.containerData.get(2);
    }
    public int getHeatEnergy(){
        return this.containerData.get(3);
    }

    public int getMaxHeat(){
        return this.be.getMaxHeat();
    }

    public boolean isBurn(){
        return !(this.containerData.get(4) == 0);
    }
}
