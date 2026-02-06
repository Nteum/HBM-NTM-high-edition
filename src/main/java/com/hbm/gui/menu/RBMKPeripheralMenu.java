package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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

    public static final int LINK_CLEAR_BUTTON = 1;
    public static final int LINK_APPLY_BUTTON = 2;
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    private static final int COORD_LIMIT = 30_000_000;
    private static final int COORD_OFFSET = COORD_LIMIT;
    private static final int COORD_RANGE = COORD_OFFSET * 2 + 1;
    private static final int COORD_BUTTON_BASE = 1_000_000_000;

    private static final int TELEMETRY_SLOTS = 10;
    private static final int GRID_DATA_START = TELEMETRY_SLOTS;
    private static final int DATA_SLOTS = TELEMETRY_SLOTS + GRID_SIZE;

    private int pendingLinkX;
    private int pendingLinkY;
    private int pendingLinkZ;

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
        if (this.container instanceof RBMKPeripheralEntity peripheral) {
            if (id == 0) {
                return peripheral.triggerAz5();
            }
            if (id == LINK_CLEAR_BUTTON) {
                peripheral.clearManualLink();
                return true;
            }
            if (id == LINK_APPLY_BUTTON) {
                return peripheral.linkToColumn(new BlockPos(pendingLinkX, pendingLinkY, pendingLinkZ));
            }
            if (isCoordButton(id)) {
                int axis = decodeAxis(id);
                int value = decodeCoord(id);
                switch (axis) {
                    case AXIS_X -> pendingLinkX = value;
                    case AXIS_Y -> pendingLinkY = value;
                    case AXIS_Z -> pendingLinkZ = value;
                    default -> {
                        return false;
                    }
                }
                return true;
            }
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

    public static int coordButtonId(int axis, int value) {
        int clamped = Mth.clamp(value, -COORD_LIMIT, COORD_LIMIT);
        return COORD_BUTTON_BASE + axis * COORD_RANGE + (clamped + COORD_OFFSET);
    }

    private static boolean isCoordButton(int id) {
        return id >= COORD_BUTTON_BASE && id < COORD_BUTTON_BASE + (COORD_RANGE * 3);
    }

    private static int decodeAxis(int id) {
        return (id - COORD_BUTTON_BASE) / COORD_RANGE;
    }

    private static int decodeCoord(int id) {
        int offset = (id - COORD_BUTTON_BASE) % COORD_RANGE;
        return offset - COORD_OFFSET;
    }
}
