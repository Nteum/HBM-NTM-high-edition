package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RBMKPeripheralMenu extends BaseMachineMenu {

    public RBMKPeripheralMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(0), new SimpleContainerData(10));
    }

    public RBMKPeripheralMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.RBMK_PERIPHERAL_MENU.get(), containerId, container, data);
        this.slotNum = 0;
        // 原版 RBMK 外设控制台不显示玩家物品栏
        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0 && this.container instanceof RBMKPeripheralEntity peripheral) {
            return peripheral.triggerAz5();
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
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

    public int getColumnCount() {
        return containerData.get(8);
    }

    public RBMKPeripheralType getPeripheralType() {
        int idx = containerData.get(9);
        RBMKPeripheralType[] values = RBMKPeripheralType.values();
        return idx >= 0 && idx < values.length ? values[idx] : RBMKPeripheralType.CONSOLE;
    }

    public boolean hasColumnData() {
        return containerData.get(1) > 0;
    }
}
