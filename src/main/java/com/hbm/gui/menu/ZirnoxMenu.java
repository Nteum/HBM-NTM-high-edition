package com.hbm.gui.menu;

import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.OutputSlot;
import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.Inventory.fluid.ModFluids;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class ZirnoxMenu extends BaseMachineMenu {
    public static final int SLOT_COUNT = 28;
    public static final int DATA_COUNT = 6;
    public static final int SLOT_ROD_START = 0;
    public static final int SLOT_ROD_END = 24;
    public static final int SLOT_CO2_IN = 24;
    public static final int SLOT_WATER_IN = 25;
    public static final int SLOT_CO2_OUT = 26;
    public static final int SLOT_WATER_OUT = 27;

    public ZirnoxMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public ZirnoxMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(ModMenuType.ZIRNOX_MENU.get(), containerId, container, data);
        slotNum = SLOT_COUNT;

        // Fuel rods
        this.addSlot(new Slot(container, 0, 26, 16));
        this.addSlot(new Slot(container, 1, 62, 16));
        this.addSlot(new Slot(container, 2, 98, 16));
        this.addSlot(new Slot(container, 3, 8, 34));
        this.addSlot(new Slot(container, 4, 44, 34));
        this.addSlot(new Slot(container, 5, 80, 34));
        this.addSlot(new Slot(container, 6, 116, 34));
        this.addSlot(new Slot(container, 7, 26, 52));
        this.addSlot(new Slot(container, 8, 62, 52));
        this.addSlot(new Slot(container, 9, 98, 52));
        this.addSlot(new Slot(container, 10, 8, 70));
        this.addSlot(new Slot(container, 11, 44, 70));
        this.addSlot(new Slot(container, 12, 80, 70));
        this.addSlot(new Slot(container, 13, 116, 70));
        this.addSlot(new Slot(container, 14, 26, 88));
        this.addSlot(new Slot(container, 15, 62, 88));
        this.addSlot(new Slot(container, 16, 98, 88));
        this.addSlot(new Slot(container, 17, 8, 106));
        this.addSlot(new Slot(container, 18, 44, 106));
        this.addSlot(new Slot(container, 19, 80, 106));
        this.addSlot(new Slot(container, 20, 116, 106));
        this.addSlot(new Slot(container, 21, 26, 124));
        this.addSlot(new Slot(container, 22, 62, 124));
        this.addSlot(new Slot(container, 23, 98, 124));

        // Fluid IO
        this.addSlot(new Slot(container, SLOT_CO2_IN, 143, 124));
        this.addSlot(new Slot(container, SLOT_WATER_IN, 179, 124));
        this.addSlot(new OutputSlot(container, SLOT_CO2_OUT, 143, 142));
        this.addSlot(new OutputSlot(container, SLOT_WATER_OUT, 179, 142));

        addPlayerSlot(playerInventory, 0, 90);
        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack current = slot.getItem();
            stack = current.copy();
            if (index < slotNum) {
                if (!moveItemStackTo(current, slotNum, slotNum + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (current.getItem() instanceof ItemZirnoxRod) {
                    if (!moveItemStackTo(current, SLOT_ROD_START, SLOT_ROD_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFluidContainer(current, ModFluids.CARBON_DIOXIDE.source().get())) {
                    if (!moveItemStackTo(current, SLOT_CO2_IN, SLOT_CO2_IN + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFluidContainer(current, Fluids.WATER)) {
                    if (!moveItemStackTo(current, SLOT_WATER_IN, SLOT_WATER_IN + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (current.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    private static boolean isFluidContainer(ItemStack stack, Fluid fluid) {
        if (stack.getItem() instanceof BucketItem bucket) {
            return bucket.getFluid().isSame(fluid);
        }
        FluidStack contained = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
        return !contained.isEmpty() && contained.getFluid().isSame(fluid);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.container instanceof com.hbm.blockentity.machine.ZirnoxReactorBlockEntity zirnox) {
            if (id == 0) {
                zirnox.toggleActive();
                return true;
            }
            if (id == 1) {
                zirnox.ventCarbonDioxide();
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }

    public int getHeat() {
        return containerData.get(0);
    }

    public int getPressure() {
        return containerData.get(1);
    }

    public int getSteam() {
        return containerData.get(2);
    }

    public int getCarbonDioxide() {
        return containerData.get(3);
    }

    public int getWater() {
        return containerData.get(4);
    }

    public boolean isOn() {
        return containerData.get(5) == 1;
    }
}
