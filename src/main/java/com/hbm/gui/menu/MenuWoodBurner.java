package com.hbm.gui.menu;

import com.hbm.blockentity.generator.TileWoodBurner;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.SlotTakeOnly;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MenuWoodBurner extends BaseMachineMenu{
    public TileWoodBurner be;
    public MenuWoodBurner(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory, getClientBlockEntity(TileWoodBurner.class, inventory, buf), new SimpleContainerData(3));
    }
    public MenuWoodBurner(int pContainerId, Inventory inventory, TileWoodBurner be, ContainerData containerData) {
        super(ModMenuType.MENU_WOOD_BURNER.get(), pContainerId, inventory, containerData);
        this.containerData = containerData;
        this.be = be;
        IItemHandler handler = be.getItemHandler();
        slotNum = handler.getSlots();
        //Fuel
        this.addSlot(new SlotItemHandler(handler, 0, 26, 18));
        //Ashes
        this.addSlot(new SlotTakeOnly(handler, 1, 26, 54));
        //Fluid ID
//        this.addSlot(new SlotItemHandler(handler, 2, 98, 54));
        //Fluid Container
        this.addSlot(new SlotItemHandler(handler, 2, 98, 18));
        this.addSlot(new SlotTakeOnly(handler, 3, 98, 36));
        //Battery
        this.addSlot(new SlotItemHandler(handler, 4, 143, 54));
        addPlayerSlot(inventory, 0, 0);
        addDataSlots(containerData);
    }

    public int getPower(){
        return this.containerData.get(0);
    }
    public int getCapacity(){
        return this.containerData.get(1);
    }
    public int getBurntime(){
        return this.containerData.get(2);
    }
    public boolean getIsOn(){
        return this.containerData.get(4) == 0;
    }
    public boolean isLiquidBurn(){
        return this.containerData.get(5) == 0;
    }
}
