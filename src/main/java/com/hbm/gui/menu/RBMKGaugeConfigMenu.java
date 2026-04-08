package com.hbm.gui.menu;

import com.hbm.blockentity.machine.rbmk.RBMKGaugeEntity;
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

public class RBMKGaugeConfigMenu extends BaseMachineMenu {

    private static final int CHANNELS = 4;

    private final BlockPos pos;
    private final RBMKGaugeEntity gauge;

    public RBMKGaugeConfigMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, null, new SimpleContainer(0), new SimpleContainerData(0));
    }

    public RBMKGaugeConfigMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveGauge(playerInventory, buf));
    }

    public RBMKGaugeConfigMenu(int containerId, Inventory playerInventory, RBMKGaugeEntity gauge) {
        this(containerId, playerInventory, gauge,
                gauge != null ? gauge.getBlockPos() : null,
                new SimpleContainer(0),
                new SimpleContainerData(0));
    }

    public RBMKGaugeConfigMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        this(containerId, playerInventory, container instanceof RBMKGaugeEntity entity ? entity : null,
                container instanceof RBMKGaugeEntity entity ? entity.getBlockPos() : null,
                container, data);
    }

    private RBMKGaugeConfigMenu(int containerId, Inventory playerInventory, RBMKGaugeEntity gauge, BlockPos pos, Container container, ContainerData data) {
        super(ModMenuType.RBMK_GAUGE_CONFIG_MENU.get(), containerId, container, data);
        this.slotNum = 0;
        this.gauge = gauge;
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
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return false;
        }
        return gauge.isActive(slot);
    }

    public boolean isPolling(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return false;
        }
        return gauge.isPolling(slot);
    }

    public int getColor(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return 0;
        }
        return gauge.getColor(slot);
    }

    public String getLabel(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return "";
        }
        return gauge.getLabel(slot);
    }

    public String getChannel(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return "";
        }
        return gauge.getChannel(slot);
    }

    public int getMinValue(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return 0;
        }
        return gauge.getMinValue(slot);
    }

    public int getMaxValue(int slot) {
        if (gauge == null || slot < 0 || slot >= CHANNELS) {
            return 100;
        }
        return gauge.getMaxValue(slot);
    }

    private static RBMKGaugeEntity resolveGauge(Inventory playerInventory, FriendlyByteBuf buf) {
        Objects.requireNonNull(buf, "buffer missing block position");
        BlockPos pos = buf.readBlockPos();
        if (playerInventory.player.level().getBlockEntity(pos) instanceof RBMKGaugeEntity entity) {
            return entity;
        }
        return null;
    }
}
