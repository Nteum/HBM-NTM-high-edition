package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.gui.menu.RBMKGaugeConfigMenu;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLinkable;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKMonitorMetric;
import com.hbm.reactor.rbmk.RBMKRadioNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Four-window gauge panel bound to a single RBMK column.
 */
public class RBMKGaugeEntity extends UpdateableBlockEntity implements RBMKLinkable, MenuProvider {

    private static final int CHANNELS = 4;
    private static final int SYNC_INTERVAL_TICKS = 10;
    private static final int[] DEFAULT_COLORS = {0x800000, 0x804000, 0x808000, 0x000080};
    private static final RBMKMonitorMetric[] DEFAULTS = {
            RBMKMonitorMetric.HEAT,
            RBMKMonitorMetric.TOTAL_FLUX,
            RBMKMonitorMetric.CONTROL,
            RBMKMonitorMetric.WATER
    };

    @Nullable
    private BlockPos linkedColumn;
    private int rotation;
    private int tickCounter;
    private final RBMKMonitorMetric[] metrics = DEFAULTS.clone();
    private final String[] displays = {"----", "----", "----", "----"};
    private final int[] colors = DEFAULT_COLORS.clone();
    private final float[] normalized = new float[CHANNELS];
    private final boolean[] active = {true, true, true, true};
    private final boolean[] polling = new boolean[CHANNELS];
    private final String[] labels = new String[CHANNELS];
    private final String[] channels = new String[CHANNELS];
    private final int[] minValues = new int[CHANNELS];
    private final int[] maxValues = {100, 100, 100, 100};
    private final int[] radioValues = new int[CHANNELS];
    private final String[] lastSignals = new String[CHANNELS];

    public RBMKGaugeEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_GAUGE_ENTITY.get(), pos, state);
        for (int i = 0; i < CHANNELS; i++) {
            labels[i] = "Gauge " + (i + 1);
            channels[i] = "";
            lastSignals[i] = "";
        }
    }

    @Override
    protected void onUpdateServer() {
        tickCounter++;
        if (tickCounter % SYNC_INTERVAL_TICKS != 0) {
            return;
        }
        RBMKColumnState state = null;
        if (level instanceof ServerLevel serverLevel && linkedColumn != null) {
            state = RBMKManager.context(serverLevel).column(linkedColumn).orElse(null);
        }
        updateDisplays(state, level instanceof ServerLevel serverLevel ? serverLevel : null);
        sendUpdatePacket();
    }

    private void updateDisplays(@Nullable RBMKColumnState state, @Nullable ServerLevel serverLevel) {
        for (int i = 0; i < CHANNELS; i++) {
            if (!active[i]) {
                displays[i] = "";
                colors[i] = 0x404040;
                normalized[i] = 0.0F;
                continue;
            }

            colors[i] = colors[i] & 0x00FFFFFF;

            if (serverLevel != null && channels[i] != null && !channels[i].isBlank()) {
                RBMKRadioNetwork.Signal signal = RBMKRadioNetwork.listen(serverLevel, channels[i]);
                if (signal != null) {
                    String payload = signal.signal();
                    if (polling[i] || !payload.equals(lastSignals[i])) {
                        radioValues[i] = parseSignalValue(payload);
                    }
                    lastSignals[i] = payload;
                } else if (polling[i]) {
                    radioValues[i] = 0;
                }
                displays[i] = Integer.toString(radioValues[i]);
                normalized[i] = normalizeRange(radioValues[i], minValues[i], maxValues[i]);
                continue;
            }

            if (state == null) {
                displays[i] = "----";
                normalized[i] = 0.0F;
                continue;
            }

            RBMKMonitorMetric metric = metrics[i];
            displays[i] = metric.format(state);
            normalized[i] = switch (metric) {
                case HEAT -> ratio(state.heat(), state.maxHeat());
                case TOTAL_FLUX -> clamp(metric.sampleValue(state) / 100.0D);
                case CONTROL, XENON, DEPLETION -> clamp(metric.sampleValue(state) / 100.0D);
                case WATER -> ratio(state.waterAmount(), Math.max(1, state.maxWater()));
                case STEAM -> ratio(state.steamAmount(), Math.max(1, state.maxSteam()));
                case CORE_HEAT -> ratio(state.coreHeat(), Math.max(1.0D, state.coreMaxHeat()));
            };
        }
    }

    private static float ratio(double value, double max) {
        return clamp(max <= 0.0D ? 0.0D : value / max);
    }

    private static float clamp(double value) {
        return (float) Math.max(0.0D, Math.min(1.0D, value));
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
        if (slot < 0 || slot >= CHANNELS || !active[slot]) {
            return;
        }
        metrics[slot] = metrics[slot].next();
        if (level instanceof ServerLevel serverLevel) {
            updateDisplays(linkedColumn != null ? RBMKManager.context(serverLevel).column(linkedColumn).orElse(null) : null, serverLevel);
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
        if (level instanceof ServerLevel serverLevel) {
            updateDisplays(RBMKManager.context(serverLevel).column(core).orElse(null), serverLevel);
        }
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
        return Component.translatable("block.hbm.machine_rbmk_gauge");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.rbmkGauge");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RBMKGaugeConfigMenu(containerId, inventory, this);
    }

    public int getRotation() {
        return rotation & 3;
    }

    public RBMKMonitorMetric getMetric(int slot) {
        return metrics[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String getDisplay(int slot) {
        return displays[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int getColor(int slot) {
        return colors[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public float getNormalized(int slot) {
        return normalized[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public boolean isActive(int slot) {
        return active[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public boolean isPolling(int slot) {
        return polling[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String getLabel(int slot) {
        return labels[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String getChannel(int slot) {
        return channels[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int getMinValue(int slot) {
        return minValues[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int getMaxValue(int slot) {
        return maxValues[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        applyConfigTag(tag);
    }

    private void applyConfigTag(CompoundTag tag) {
        int activeMask = tag.contains("CfgActiveMask") ? tag.getInt("CfgActiveMask") : -1;
        int pollingMask = tag.contains("CfgPollingMask") ? tag.getInt("CfgPollingMask") : -1;
        for (int i = 0; i < CHANNELS; i++) {
            if (activeMask >= 0) {
                active[i] = (activeMask & (1 << i)) != 0;
            }
            if (pollingMask >= 0) {
                polling[i] = (pollingMask & (1 << i)) != 0;
            }
            if (tag.contains("CfgColor" + i)) {
                colors[i] = Mth.clamp(tag.getInt("CfgColor" + i), 0, 0xFFFFFF);
            }
            if (tag.contains("CfgLabel" + i)) {
                labels[i] = trimText(tag.getString("CfgLabel" + i), 15);
            }
            if (tag.contains("CfgChannel" + i)) {
                channels[i] = trimText(tag.getString("CfgChannel" + i), 16);
            }
            if (tag.contains("CfgMin" + i)) {
                minValues[i] = tag.getInt("CfgMin" + i);
            }
            if (tag.contains("CfgMax" + i)) {
                maxValues[i] = tag.getInt("CfgMax" + i);
            }
            if (minValues[i] == maxValues[i]) {
                maxValues[i] = minValues[i] + 1;
            }
        }
        setChanged();
        sendUpdatePacket();
    }

    private static int parseSignalValue(String payload) {
        if (payload == null || payload.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(payload.trim());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static float normalizeRange(int value, int min, int max) {
        if (min == max) {
            return 0.0F;
        }
        int lower = Math.min(min, max);
        int upper = Math.max(min, max);
        float ratio = (float) (value - lower) / (float) Math.max(1, upper - lower);
        ratio = (float) Math.max(0.0D, Math.min(1.0D, ratio));
        if (min > max) {
            ratio = 1.0F - ratio;
        }
        return ratio;
    }

    private static String trimText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
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
            tag.putString("Display" + i, displays[i]);
            tag.putInt("Color" + i, colors[i]);
            tag.putFloat("Needle" + i, normalized[i]);
            tag.putBoolean("Active" + i, active[i]);
            tag.putBoolean("Polling" + i, polling[i]);
            tag.putString("Label" + i, labels[i]);
            tag.putString("Channel" + i, channels[i]);
            tag.putInt("Min" + i, minValues[i]);
            tag.putInt("Max" + i, maxValues[i]);
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
            displays[i] = tag.getString("Display" + i);
            colors[i] = tag.getInt("Color" + i);
            normalized[i] = tag.getFloat("Needle" + i);
            active[i] = tag.contains("Active" + i) ? tag.getBoolean("Active" + i) : true;
            polling[i] = tag.getBoolean("Polling" + i);
            labels[i] = tag.contains("Label" + i) ? tag.getString("Label" + i) : "Gauge " + (i + 1);
            channels[i] = tag.getString("Channel" + i);
            minValues[i] = tag.getInt("Min" + i);
            maxValues[i] = tag.contains("Max" + i) ? tag.getInt("Max" + i) : 100;
            if (minValues[i] == maxValues[i]) {
                maxValues[i] = minValues[i] + 1;
            }
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
            tag.putBoolean("Active" + i, active[i]);
            tag.putBoolean("Polling" + i, polling[i]);
            tag.putInt("Color" + i, colors[i]);
            tag.putString("Label" + i, labels[i]);
            tag.putString("Channel" + i, channels[i]);
            tag.putInt("Min" + i, minValues[i]);
            tag.putInt("Max" + i, maxValues[i]);
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
            active[i] = tag.contains("Active" + i) ? tag.getBoolean("Active" + i) : true;
            polling[i] = tag.getBoolean("Polling" + i);
            colors[i] = tag.contains("Color" + i) ? Mth.clamp(tag.getInt("Color" + i), 0, 0xFFFFFF) : DEFAULT_COLORS[i];
            labels[i] = tag.contains("Label" + i) ? trimText(tag.getString("Label" + i), 15) : "Gauge " + (i + 1);
            channels[i] = trimText(tag.getString("Channel" + i), 16);
            minValues[i] = tag.getInt("Min" + i);
            maxValues[i] = tag.contains("Max" + i) ? tag.getInt("Max" + i) : 100;
            if (minValues[i] == maxValues[i]) {
                maxValues[i] = minValues[i] + 1;
            }
        }
    }
}
