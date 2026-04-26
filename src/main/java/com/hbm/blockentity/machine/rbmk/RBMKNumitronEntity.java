package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLinkable;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKMonitorMetric;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * First modern slice of the legacy RBMK numitron panel. It directly reads a
 * linked column and exposes two configurable numeric windows.
 */
public class RBMKNumitronEntity extends UpdateableBlockEntity implements RBMKLinkable {

    private static final int SYNC_INTERVAL_TICKS = 10;

    @Nullable
    private BlockPos linkedColumn;
    private int rotation;
    private int tickCounter;
    private final RBMKMonitorMetric[] metrics = {RBMKMonitorMetric.HEAT, RBMKMonitorMetric.CONTROL};
    private final String[] displays = {"----", "----"};
    private final int[] colors = {0x55FF55, 0x55FF55};

    public RBMKNumitronEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_NUMITRON_ENTITY.get(), pos, state);
    }

    @Override
    protected void onUpdateServer() {
        tickCounter++;
        if (tickCounter % SYNC_INTERVAL_TICKS != 0) {
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        RBMKColumnState state = linkedColumn != null ? RBMKManager.context(serverLevel).column(linkedColumn).orElse(null) : null;
        updateDisplays(state);
        sendUpdatePacket();
    }

    private void updateDisplays(@Nullable RBMKColumnState state) {
        for (int i = 0; i < metrics.length; i++) {
            if (state == null) {
                displays[i] = "----";
                colors[i] = 0x404040;
            } else {
                displays[i] = metrics[i].format(state);
                colors[i] = metrics[i].color(state);
            }
        }
    }

    public void rotatePanel() {
        rotation = (rotation + 1) & 3;
        setChanged();
        sendUpdatePacket();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void cycleMetric(int slot) {
        if (slot < 0 || slot >= metrics.length) {
            return;
        }
        metrics[slot] = metrics[slot].next();
        if (level instanceof ServerLevel serverLevel) {
            updateDisplays(linkedColumn != null ? RBMKManager.context(serverLevel).column(linkedColumn).orElse(null) : null);
        }
        setChanged();
        sendUpdatePacket();
    }

    @Override
    public boolean linkToColumn(BlockPos target) {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(target);
        if (!(state.getBlock() instanceof BlockRBMKBase base)) {
            return false;
        }
        BlockPos core = base.getCore(state, level, target);
        if (!(level.getBlockEntity(core) instanceof RBMKBaseEntity)) {
            return false;
        }
        linkedColumn = core.immutable();
        setChanged();
        sendUpdatePacket();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return true;
    }

    @Nullable
    @Override
    public BlockPos getLinkedColumn() {
        return linkedColumn;
    }

    @Override
    public Component getLinkDisplayName() {
        return Component.translatable("block.hbm.machine_rbmk_numitron");
    }

    public int getRotation() {
        return rotation & 3;
    }

    public RBMKMonitorMetric getMetric(int slot) {
        return metrics[Math.max(0, Math.min(slot, metrics.length - 1))];
    }

    public String getDisplay(int slot) {
        return displays[Math.max(0, Math.min(slot, displays.length - 1))];
    }

    public int getColor(int slot) {
        return colors[Math.max(0, Math.min(slot, colors.length - 1))];
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("Rotation", rotation);
        for (int i = 0; i < metrics.length; i++) {
            tag.putInt("Metric" + i, metrics[i].ordinal());
            tag.putString("Display" + i, displays[i]);
            tag.putInt("Color" + i, colors[i]);
        }
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        for (int i = 0; i < metrics.length; i++) {
            int ordinal = Math.max(0, Math.min(tag.getInt("Metric" + i), RBMKMonitorMetric.values().length - 1));
            metrics[i] = RBMKMonitorMetric.values()[ordinal];
            displays[i] = tag.getString("Display" + i);
            colors[i] = tag.getInt("Color" + i);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("Rotation", rotation);
        for (int i = 0; i < metrics.length; i++) {
            tag.putInt("Metric" + i, metrics[i].ordinal());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        for (int i = 0; i < metrics.length; i++) {
            int ordinal = Math.max(0, Math.min(tag.getInt("Metric" + i), RBMKMonitorMetric.values().length - 1));
            metrics[i] = RBMKMonitorMetric.values()[ordinal];
        }
    }
}
