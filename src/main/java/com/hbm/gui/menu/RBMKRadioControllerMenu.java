package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKRadioControllerEntity;
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

public class RBMKRadioControllerMenu extends BaseMachineMenu {

    private final BlockPos pos;
    private final RBMKRadioControllerEntity controller;

    public RBMKRadioControllerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, null, new SimpleContainer(0), new SimpleContainerData(0));
    }

    public RBMKRadioControllerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveController(playerInventory, buf));
    }

    public RBMKRadioControllerMenu(int containerId, Inventory playerInventory, RBMKRadioControllerEntity controller) {
        this(containerId, playerInventory, controller,
                controller != null ? controller.getBlockPos() : null,
                new SimpleContainer(0),
                new SimpleContainerData(0));
    }

    public RBMKRadioControllerMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, container instanceof RBMKRadioControllerEntity entity ? entity : null,
                container instanceof RBMKRadioControllerEntity entity ? entity.getBlockPos() : null,
                container, data);
    }

    private RBMKRadioControllerMenu(int containerId, Inventory playerInventory, RBMKRadioControllerEntity controller, BlockPos pos, Container container, ContainerData data) {
        super(ModMenuType.RBMK_RADIO_CONTROLLER_MENU.get(), containerId, container, data);
        this.slotNum = 0;
        this.controller = controller;
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

    public String getChannel() {
        return controller != null ? controller.getChannel() : "";
    }

    public boolean isPolling() {
        return controller != null && controller.isPolling();
    }

    private static RBMKRadioControllerEntity resolveController(Inventory playerInventory, FriendlyByteBuf buf) {
        Objects.requireNonNull(buf, "buffer missing block position");
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof RBMKRadioControllerEntity entity) {
            return entity;
        }
        return null;
    }
}
