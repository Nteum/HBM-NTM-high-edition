package com.hbm.gui.menu;

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
        super(ModMenuType.CHEMPLANT_MENU.get(), pContainerId, new SimpleContainer(20), new SimpleContainerData(3));
    }

}
