package com.hbm.gui.menu;

import com.hbm.blockentity.machine.GasTurbineBlockEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.gui.menu.slot.BatterySlot;
import com.hbm.registries.ModTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Menu for the gas turbine multiblock controller. Provides two slots: a battery
 * output and a configuration slot used to set the accepted fuel type.
 */
public class GasTurbineMenu extends BaseMachineMenu implements ITileAccess {

    public static final int SLOT_OUTPUT = 0;
    public static final int SLOT_CONFIG = 1;
    public static final int DATA_SIZE = 13;
    private static final int PLAYER_INV_Y_OFFSET = 57;
    private static final int SLIDER_BUTTON_BASE = 2000;

    @Nullable
    private GasTurbineBlockEntity blockEntity;

    public GasTurbineMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(2), new SimpleContainerData(DATA_SIZE));
    }

    public GasTurbineMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenuType.GAS_TURBINE_MENU.get(), containerId, container, data);
        if (container instanceof GasTurbineBlockEntity turbine) {
            this.blockEntity = turbine;
        }
        this.slotNum = 2;
        this.addSlot(new BatterySlot(container, SLOT_OUTPUT, 8, 109));
        this.addSlot(new Slot(container, SLOT_CONFIG, 36, 17));
        addPlayerSlot(inventory, 0, PLAYER_INV_Y_OFFSET);
        this.addDataSlots(data);
    }

    @Override
    public boolean innerMovePlayer2Container(int index, ItemStack stack) {
        if (stack.is(ModTags.Items.CHARGEABLE)) {
            return this.moveItemStackTo(stack, SLOT_OUTPUT, SLOT_OUTPUT + 1, false);
        }
        return this.moveItemStackTo(stack, SLOT_CONFIG, SLOT_CONFIG + 1, false);
    }

    @Override
    public void setTile(BlockEntity blockEntity) {
        if (blockEntity instanceof GasTurbineBlockEntity turbine) {
            this.blockEntity = turbine;
        }
    }

    @Nullable
    public GasTurbineBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public long getEnergy() {
        long lo = Integer.toUnsignedLong(containerData.get(0));
        long hi = Integer.toUnsignedLong(containerData.get(1));
        return (hi << 32) | lo;
    }

    public int getRpm() {
        return containerData.get(2);
    }

    public int getTemperature() {
        return containerData.get(3);
    }

    public int getState() {
        return containerData.get(4);
    }

    public boolean isAutoMode() {
        return containerData.get(5) > 0;
    }

    public int getSliderPosition() {
        return containerData.get(6);
    }

    public int getThrottle() {
        return containerData.get(7);
    }

    public int getSteamAmount() {
        return containerData.get(8);
    }

    public int getWaterAmount() {
        return containerData.get(9);
    }

    public int getFuelAmount() {
        return containerData.get(10);
    }

    public int getLubricantAmount() {
        return containerData.get(11);
    }

    public int getInstantPowerOutput() {
        return containerData.get(12);
    }

    public boolean isRunning() {
        return getState() == 1;
    }

    public static int getFuelCapacity() {
        return 100_000;
    }

    public static int getLubricantCapacity() {
        return 16_000;
    }

    public static int getWaterCapacity() {
        return 16_000;
    }

    public static int getSteamCapacity() {
        return 160_000;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.container instanceof GasTurbineBlockEntity turbine) {
            if (id == 0) {
                if (turbine.getState() == 0) {
                    turbine.requestStart();
                } else {
                    turbine.requestStop();
                }
                return true;
            }
            if (id == 1) {
                if (turbine.getState() == 1) {
                    turbine.toggleAutoMode();
                    return true;
                }
                return false;
            }
            if (id >= SLIDER_BUTTON_BASE && id <= SLIDER_BUTTON_BASE + 60) {
                int slider = id - SLIDER_BUTTON_BASE;
                turbine.setAutoMode(false);
                turbine.setPowerSliderPos(slider);
                return true;
            }
        }
        return super.clickMenuButton(player, id);
    }

    public static int sliderButtonId(int sliderValue) {
        return SLIDER_BUTTON_BASE + sliderValue;
    }
}
