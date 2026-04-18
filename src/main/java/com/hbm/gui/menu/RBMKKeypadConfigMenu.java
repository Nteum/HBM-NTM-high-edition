package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKKeypadEntity;
import com.hbm.gui.ModMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

import java.util.Objects;

public class RBMKKeypadConfigMenu extends BaseMachineMenu {

    private static final int CHANNELS = 4;

    private final BlockPos pos;
    private final RBMKKeypadEntity keypad;

    public RBMKKeypadConfigMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, null, new SimpleContainer(0), new SimpleContainerData(0));
    }

    public RBMKKeypadConfigMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveKeypad(playerInventory, buf));
    }

    public RBMKKeypadConfigMenu(int containerId, Inventory playerInventory, RBMKKeypadEntity keypad) {
        this(containerId, playerInventory, keypad,
                keypad != null ? keypad.getBlockPos() : null,
                new SimpleContainer(0),
                new SimpleContainerData(0));
    }

    public RBMKKeypadConfigMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, container instanceof RBMKKeypadEntity entity ? entity : null,
                container instanceof RBMKKeypadEntity entity ? entity.getBlockPos() : null,
                container, data);
    }

    private RBMKKeypadConfigMenu(int containerId, Inventory playerInventory, RBMKKeypadEntity keypad, BlockPos pos, Container container, ContainerData data) {
        super(ModMenuType.RBMK_KEYPAD_CONFIG_MENU.get(), containerId, container, data);
        this.slotNum = 0;
        this.keypad = keypad;
        this.pos = pos;
    }

    @Override
    public boolean stillValid(Player player) {
        if (pos == null) {
            return true;
        }
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isActive(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return false;
        }
        return keypad.isActive(slot);
    }

    public boolean isPolling(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return false;
        }
        return keypad.isPolling(slot);
    }

    public int getColor(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return 0;
        }
        return keypad.color(slot);
    }

    public String getLabel(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return "";
        }
        return keypad.label(slot);
    }

    public String getChannel(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return "";
        }
        return keypad.channel(slot);
    }

    public String getCommand(int slot) {
        if (keypad == null || slot < 0 || slot >= CHANNELS) {
            return "";
        }
        return keypad.command(slot);
    }

    private static RBMKKeypadEntity resolveKeypad(Inventory playerInventory, FriendlyByteBuf buf) {
        Objects.requireNonNull(buf, "buffer missing block position");
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof RBMKKeypadEntity entity) {
            return entity;
        }
        return null;
    }
}
