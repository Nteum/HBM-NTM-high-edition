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

    public static final int GRID_SIZE = 15;
    public static final int GRID_CENTER = GRID_SIZE / 2;
    public static final int GRID_EMPTY = 0;
    public static final int GRID_COLUMN = 1;
    public static final int GRID_FUEL = 2;
    public static final int GRID_CONTROL = 3;

    private static final int TELEMETRY_SLOTS = 10;
    private static final int GRID_DATA_START = TELEMETRY_SLOTS;
    private static final int DATA_SLOTS = TELEMETRY_SLOTS + GRID_SIZE;

    public RBMKPeripheralMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(0), new SimpleContainerData(DATA_SLOTS));
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

    public int getGridCell(int col, int row) {
        if (col < 0 || col >= GRID_SIZE || row < 0 || row >= GRID_SIZE) {
            return GRID_EMPTY;
        }
        int index = GRID_DATA_START + row;
        if (index < 0 || index >= containerData.getCount()) {
            return GRID_EMPTY;
        }
        int rowMask = containerData.get(index);
        return (rowMask >>> (col * 2)) & 0x3;
    }
}
