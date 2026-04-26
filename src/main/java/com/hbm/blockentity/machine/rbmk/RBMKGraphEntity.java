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

import java.util.Arrays;

/**
 * First usable 1.20 slice of the legacy RBMK graph panel. It mirrors a linked
 * column into two compact metric histories.
 */
public class RBMKGraphEntity extends UpdateableBlockEntity implements RBMKLinkable {

    private static final int CHANNELS = 2;
    private static final int HISTORY_LENGTH = 30;
    private static final int SYNC_INTERVAL_TICKS = 10;

    @Nullable
    private BlockPos linkedColumn;
    private int rotation;
    private int tickCounter;
    private final RBMKMonitorMetric[] metrics = {RBMKMonitorMetric.HEAT, RBMKMonitorMetric.TOTAL_FLUX};
    private final int[][] history = {new int[HISTORY_LENGTH], new int[HISTORY_LENGTH]};
    private final String[] displays = {"----", "----"};
    private final int[] colors = {0x55FF55, 0x55FF55};

    public RBMKGraphEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_GRAPH_ENTITY.get(), pos, state);
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
        updateChannels(state);
        sendUpdatePacket();
    }

    private void updateChannels(@Nullable RBMKColumnState state) {
        for (int i = 0; i < CHANNELS; i++) {
            if (state == null) {
                push(history[i], 0);
                displays[i] = "----";
                colors[i] = 0x404040;
            } else {
                push(history[i], metrics[i].sampleValue(state));
                displays[i] = metrics[i].format(state);
                colors[i] = metrics[i].color(state);
            }
        }
    }

    private static void push(int[] values, int value) {
        System.arraycopy(values, 1, values, 0, values.length - 1);
        values[values.length - 1] = value;
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
        if (slot < 0 || slot >= CHANNELS) {
            return;
        }
        metrics[slot] = metrics[slot].next();
        Arrays.fill(history[slot], 0);
        if (level instanceof ServerLevel serverLevel) {
            RBMKColumnState state = linkedColumn != null ? RBMKManager.context(serverLevel).column(linkedColumn).orElse(null) : null;
            if (state == null) {
                displays[slot] = "----";
                colors[slot] = 0x404040;
            } else {
                history[slot][HISTORY_LENGTH - 1] = metrics[slot].sampleValue(state);
                displays[slot] = metrics[slot].format(state);
                colors[slot] = metrics[slot].color(state);
            }
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
        return Component.translatable("block.hbm.machine_rbmk_graph");
    }

    public int getRotation() {
        return rotation & 3;
    }

    public RBMKMonitorMetric getMetric(int slot) {
        return metrics[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int[] getHistory(int slot) {
        return history[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String getDisplay(int slot) {
        return displays[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int getColor(int slot) {
        return colors[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("Rotation", rotation);
        for (int i = 0; i < CHANNELS; i++) {
            tag.putInt("Metric" + i, metrics[i].ordinal());
            tag.putIntArray("History" + i, history[i]);
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
        for (int i = 0; i < CHANNELS; i++) {
            int ordinal = Math.max(0, Math.min(tag.getInt("Metric" + i), RBMKMonitorMetric.values().length - 1));
            metrics[i] = RBMKMonitorMetric.values()[ordinal];
            int[] values = tag.getIntArray("History" + i);
            Arrays.fill(history[i], 0);
            System.arraycopy(values, 0, history[i], 0, Math.min(values.length, HISTORY_LENGTH));
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
        for (int i = 0; i < CHANNELS; i++) {
            tag.putInt("Metric" + i, metrics[i].ordinal());
            tag.putIntArray("History" + i, history[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        for (int i = 0; i < CHANNELS; i++) {
            int ordinal = Math.max(0, Math.min(tag.getInt("Metric" + i), RBMKMonitorMetric.values().length - 1));
            metrics[i] = RBMKMonitorMetric.values()[ordinal];
            int[] values = tag.getIntArray("History" + i);
            Arrays.fill(history[i], 0);
            System.arraycopy(values, 0, history[i], 0, Math.min(values.length, HISTORY_LENGTH));
        }
    }
}
