package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class ElectricFurnaceMenu extends BaseMachineMenu{
    public ElectricFurnaceMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(4),new SimpleContainerData(0));
    }
    public ElectricFurnaceMenu(int pContainerId, Inventory pPlayerInventory, Container inContainer, ContainerData containerData1) {
        super(ModMenuType.ASSEMBLER_MENU.get(), pContainerId, inContainer, containerData1);
    }
}
