package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.gui.menu.slot.BladeSlot;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShredderMenu extends BaseMachineMenu {
    private static final int TOTAL_SLOTS = 30;
    private static final int BATTERY_SLOT = 29;
    private static final int LEFT_BLADE_SLOT = 27;
    private static final int RIGHT_BLADE_SLOT = 28;

    public ShredderMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(TOTAL_SLOTS), new SimpleContainerData(6));
    }

    public ShredderMenu(int id, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.SHREDDER_MENU.get(), id, container, data);
        this.slotNum = TOTAL_SLOTS;

        // inputs (3x3)
        int[] inputX = new int[]{44, 62, 80};
        int[] inputY = new int[]{18, 36, 54};
        int slotIndex = 0;
        for (int y : inputY) {
            for (int x : inputX) {
                this.addSlot(new Slot(container, slotIndex++, x, y));
            }
        }

        // outputs (6 rows x 3 cols)
        int[] outputX = new int[]{116, 134, 152};
        int[] outputY = new int[]{18, 36, 54, 72, 90, 108};
        for (int y : outputY) {
            for (int x : outputX) {
                this.addSlot(new OutputSlot(container, slotIndex++, x, y));
            }
        }

        // blades
        this.addSlot(new BladeSlot(container, LEFT_BLADE_SLOT, 44, 108));
        this.addSlot(new BladeSlot(container, RIGHT_BLADE_SLOT, 80, 108));

        // battery
        this.addSlot(new BatterySlot(container, BATTERY_SLOT, 8, 108));

        addPlayerSlot(playerInventory, 0, 67);
        this.addDataSlots(data);
    }

    @Override
    public boolean innerMovePlayer2Container(int index, ItemStack stack) {
        if (stack.is(ModTags.Items.CHARGEABLE)) {
            return this.moveItemStackTo(stack, BATTERY_SLOT, BATTERY_SLOT + 1, false);
        }
        if (stack.is(ModTags.Items.SHREDDER_BLADES)) {
            return this.moveItemStackTo(stack, LEFT_BLADE_SLOT, RIGHT_BLADE_SLOT + 1, false);
        }
        return this.moveItemStackTo(stack, 0, 9, false);
    }

    public int getProgress() {
        return containerData.get(0);
    }

    public int getMaxProgress() {
        return containerData.get(1);
    }

    public int getEnergyStored() {
        return containerData.get(2);
    }

    public int getEnergyCapacity() {
        return containerData.get(3);
    }

    public int getLeftBladeState() {
        return containerData.get(4);
    }

    public int getRightBladeState() {
        return containerData.get(5);
    }
}
