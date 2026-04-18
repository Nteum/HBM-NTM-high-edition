package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKPeripheralEntity;
import com.hbm.gui.ModMenuType;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class RBMKAutoloaderMenu extends BaseMachineMenu {

    public static final int BUTTON_MINUS = 1;
    public static final int BUTTON_PLUS = 2;

    private static final int INPUT_SLOTS = 9;
    private static final int OUTPUT_SLOTS = 9;
    private static final int SLOT_COUNT = INPUT_SLOTS + OUTPUT_SLOTS;
    private static final int DATA_SLOTS = 235;

    private final RBMKPeripheralEntity peripheral;

    public RBMKAutoloaderMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_SLOTS));
    }

    public RBMKAutoloaderMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveAutoloader(playerInventory, buf));
    }

    public RBMKAutoloaderMenu(int containerId, Inventory playerInventory, RBMKPeripheralEntity peripheral) {
        this(containerId, playerInventory, peripheral,
                peripheral != null ? peripheral : new SimpleContainer(SLOT_COUNT),
                peripheral != null ? peripheral.getContainerData() : new SimpleContainerData(DATA_SLOTS));
    }

    public RBMKAutoloaderMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, container instanceof RBMKPeripheralEntity entity ? entity : null, container, data);
    }

    private RBMKAutoloaderMenu(int containerId, Inventory playerInventory, RBMKPeripheralEntity peripheral, Container container, ContainerData data) {
        super(ModMenuType.RBMK_AUTOLOADER_MENU.get(), containerId, container, data);
        this.peripheral = peripheral;
        this.slotNum = SLOT_COUNT;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = row * 3 + col;
                this.addSlot(new Slot(this.container, slotIndex, 17 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem() instanceof ItemRBMKFuelRod;
                    }
                });
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = INPUT_SLOTS + row * 3 + col;
                this.addSlot(new Slot(this.container, slotIndex, 107 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        addPlayerSlot(playerInventory, 0, 16);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (peripheral != null) {
            if (id == BUTTON_MINUS) {
                return peripheral.adjustAutoloaderCycle(-5);
            }
            if (id == BUTTON_PLUS) {
                return peripheral.adjustAutoloaderCycle(5);
            }
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public boolean innerMovePlayer2Container(int index, ItemStack stack) {
        if (stack.getItem() instanceof ItemRBMKFuelRod) {
            return this.moveItemStackTo(stack, 0, INPUT_SLOTS, false);
        }
        return false;
    }

    public int getCycle() {
        return containerData.get(6);
    }

    public boolean isLinked() {
        return containerData.get(7) > 0;
    }

    public boolean isWorking() {
        return containerData.get(8) > 0;
    }

    public float getHeat() {
        return containerData.get(0) / 10.0F;
    }

    public float getMeltdownThreshold() {
        return containerData.get(1) / 10.0F;
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

    private static RBMKPeripheralEntity resolveAutoloader(Inventory playerInventory, FriendlyByteBuf buf) {
        Objects.requireNonNull(buf, "buffer missing block position");
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof RBMKPeripheralEntity entity
                && entity.getPeripheralType() == RBMKPeripheralType.AUTOLOADER) {
            return entity;
        }
        return null;
    }
}
