package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class LaunchPadMenu extends BaseMachineMenu{
    public LaunchPadMenu(int pContainerId, Inventory pInventory){
        this(pContainerId, pInventory, new SimpleContainer(7), new SimpleContainerData(0));
    }
    public LaunchPadMenu(int pContainerId, Inventory pInventory, Container inContainer, ContainerData containerData1) {
        super(ModMenuType.LAUNCH_PAD_MENU.get(), pContainerId, inContainer, containerData1);
        //Missile
        this.addSlot(new Slot(container, 0, 26, 36));
        //Designator
        this.addSlot(new Slot(container, 1, 26, 72));
        //Battery
        this.addSlot(new Slot(container, 2, 107, 90));
        //Fuel in
        this.addSlot(new Slot(container, 3, 125, 90));
        //Fuel out
        this.addSlot(new OutputSlot(container, 4, 125, 108));
        //Oxidizer in
        this.addSlot(new Slot(container, 5, 143, 90));
        //Oxidizer out
        this.addSlot(new OutputSlot(container, 6, 143, 108));
        //player
        addPlayerSlot(pInventory,0,70);
        this.addDataSlots(containerData);
    }


}
