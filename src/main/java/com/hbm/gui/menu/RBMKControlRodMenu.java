package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.item.rbmk.ItemRBMKControlRod;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RBMKControlRodMenu extends BaseMachineMenu {

    private static final int BUTTON_LEVEL_BASE = 10;
    private static final int BUTTON_COLOR_BASE = 20;
    public static final int LEVEL_COUNT = 5;
    public static final int COLOR_COUNT = 5;
    private static final double[] LEVEL_STEPS = new double[]{1.0D, 0.75D, 0.5D, 0.25D, 0.0D};
    public static final int COLOR_NONE = -1;

    public static int levelButtonId(int index) {
        return BUTTON_LEVEL_BASE + Math.max(0, Math.min(index, LEVEL_COUNT - 1));
    }

    public static int colorButtonId(int index) {
        return BUTTON_COLOR_BASE + Math.max(0, Math.min(index, COLOR_COUNT - 1));
    }

    public static double levelStepValue(int index) {
        return LEVEL_STEPS[Math.max(0, Math.min(index, LEVEL_STEPS.length - 1))];
    }

    public RBMKControlRodMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(1), new SimpleContainerData(14));
    }

    public RBMKControlRodMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.RBMK_CONTROL_ROD_MENU.get(), containerId, container, data);
        this.slotNum = 1;
        this.addSlot(new Slot(container, 0, 80, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof ItemRBMKControlRod;
            }
        });
        // 对齐旧版 RBMK GUI 的背包栏位置（贴图内置槽位）
        addPlayerSlot(playerInventory, -1, 19);
        this.addDataSlots(data);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.container instanceof RBMKControlRodEntity controlRod) {
            if (id >= BUTTON_LEVEL_BASE && id < BUTTON_LEVEL_BASE + LEVEL_COUNT) {
                double target = LEVEL_STEPS[id - BUTTON_LEVEL_BASE];
                controlRod.setInsertionFraction(target);
                return true;
            } else if (id >= BUTTON_COLOR_BASE && id < BUTTON_COLOR_BASE + COLOR_COUNT) {
                controlRod.toggleColorGroup(id - BUTTON_COLOR_BASE);
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

    public int getInsertionPercent() {
        return containerData.get(8);
    }

    public int getInsertionLevel() {
        return containerData.get(9);
    }

    public int getColumnCount() {
        return containerData.get(10);
    }

    public boolean isAz5CoolingDown() {
        return containerData.get(11) > 0;
    }

    public int getAz5CooldownTicks() {
        return containerData.get(11);
    }

    public int getSelectedColor() {
        int value = containerData.get(12);
        return value >= 0 && value < COLOR_COUNT ? value : RBMKControlRodMenu.COLOR_NONE;
    }

    public boolean hasColumnData() {
        return containerData.get(1) > 0;
    }

    public boolean hasControlRodItem() {
        return containerData.get(13) > 0;
    }
}
