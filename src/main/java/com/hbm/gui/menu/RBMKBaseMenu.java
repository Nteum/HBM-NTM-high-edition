package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.gui.ModMenuType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

/**
 * 控制 RBMK 核心状态的简易面板（仅数据 + AZ-5 按钮）。
 */
public class RBMKBaseMenu extends BaseMachineMenu {

    public RBMKBaseMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(0), new SimpleContainerData(8));
    }

    public RBMKBaseMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.RBMK_BASE_MENU.get(), containerId, container, data);
        this.slotNum = 0;
        // 原版 RBMK 控制台不显示玩家物品栏
        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.container instanceof RBMKBaseEntity base) {
            base.triggerAz5();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public float getHeat() {
        return containerData.get(0) / 10F;
    }

    public float getMeltdownThreshold() {
        return containerData.get(1) / 10F;
    }

    public int getEnergyStored() {
        return containerData.get(2);
    }

    public int getEnergyCapacity() {
        return containerData.get(3);
    }

    public int getWaterAmount() {
        return containerData.get(4);
    }

    public int getSteamAmount() {
        return containerData.get(5);
    }

    public int getLocalControlPercent() {
        return containerData.get(6);
    }

    public int getGlobalControlPercent() {
        return containerData.get(7);
    }

    public boolean hasColumnData() {
        return containerData.get(1) > 0;
    }
}
