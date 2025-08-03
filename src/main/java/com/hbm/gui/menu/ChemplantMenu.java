package com.hbm.gui.menu;

import com.hbm.blockentity.machine.ChemplantEntity;
import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public class ChemplantMenu extends BaseMachineMenu{
    public ChemplantMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(20), new SimpleContainerData(3));
    }

    public ChemplantMenu(int pContainerId, Inventory pInventory,  Container inContainer, ContainerData containerData) {
        super(ModMenuType.CHEMPLANT_MENU.get(), pContainerId, inContainer, containerData);
        /*
         * 0 Battery
         * 1-3 Upgrades
         * 4 Schematic
         * 5-8 Output
         * 9-10 FOut In
         * 11-12 FOut Out
         * 13-16 Input
         * 17-18 FIn In
         * 19-20 FIn Out
         */
        slotNum = 20;

    }

}
