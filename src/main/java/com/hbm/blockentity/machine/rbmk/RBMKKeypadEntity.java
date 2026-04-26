package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.gui.menu.RBMKKeypadConfigMenu;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLinkable;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKRadioNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simplified RBMK keypad: four hard-wired presets for linked manual/auto control
 * rods, preserving the old "panel with buttons" workflow while keeping the 1.20
 * slice bounded.
 */
public class RBMKKeypadEntity extends UpdateableBlockEntity implements RBMKLinkable, MenuProvider {

    private static final int[] PRESETS = {0, 25, 50, 100};
    private static final int CHANNELS = 4;
    private static final int SYNC_INTERVAL_TICKS = 10;
    private static final int[] DEFAULT_COLORS = {0xFF0000, 0xFFFF00, 0x0080FF, 0x00FF00};

    @Nullable
    private BlockPos linkedColumn;
    private int rotation;
    private int tickCounter;
    private boolean controlColumn;
    private int currentPercent;
    private final boolean[] pressed = new boolean[CHANNELS];
    private int pulseTicks;
    private final boolean[] active = {true, true, true, true};
    private final boolean[] polling = new boolean[CHANNELS];
    private final int[] colors = DEFAULT_COLORS.clone();
    private final String[] labels = new String[CHANNELS];
    private final String[] channels = new String[CHANNELS];
    private final String[] commands = new String[CHANNELS];

    public RBMKKeypadEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_KEYPAD_ENTITY.get(), pos, state);
        for (int i = 0; i < CHANNELS; i++) {
            labels[i] = "Button " + (i + 1);
            channels[i] = "";
            commands[i] = "";
        }
    }

    @Override
    protected void onUpdateServer() {
        tickCounter++;
        if (pulseTicks > 0 && --pulseTicks == 0) {
            clearPressed();
        }
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < CHANNELS; i++) {
                if (active[i] && polling[i] && pressed[i]) {
                    broadcastConfiguredCommand(serverLevel, i);
                }
            }
        }
        if (tickCounter % SYNC_INTERVAL_TICKS != 0) {
            return;
        }
        refreshState();
        sendUpdatePacket();
    }

    private void refreshState() {
        controlColumn = false;
        currentPercent = 0;
        if (!(level instanceof ServerLevel serverLevel) || linkedColumn == null) {
            return;
        }
        RBMKColumnState state = RBMKManager.context(serverLevel).column(linkedColumn).orElse(null);
        if (state == null) {
            return;
        }
        controlColumn = switch (state.columnType()) {
            case CONTROL, CONTROL_AUTO -> true;
            default -> false;
        };
        currentPercent = Math.round((float) (state.controlRodInsertion() * 100.0D));
    }

    public void rotatePanel() {
        rotation = (rotation + 1) & 3;
        setChanged();
        sendUpdatePacket();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void trigger(int index) {
        if (!(level instanceof ServerLevel serverLevel) || index < 0 || index >= PRESETS.length || !active[index]) {
            return;
        }

        if (polling[index]) {
            pressed[index] = !pressed[index];
            pulseTicks = 0;
            if (pressed[index]) {
                broadcastConfiguredCommand(serverLevel, index);
            }
            setChanged();
            sendUpdatePacket();
            return;
        }

        boolean sentByRadio = broadcastConfiguredCommand(serverLevel, index);
        if (!sentByRadio && linkedColumn != null
                && serverLevel.getBlockEntity(linkedColumn.above()) instanceof RBMKControlRodEntity controlRod) {
            if (!controlRod.setInsertionPercent(PRESETS[index])) {
                return;
            }
            currentPercent = PRESETS[index];
            controlColumn = true;
        }

        clearPressed();
        pressed[index] = true;
        pulseTicks = 6;
        setChanged();
        sendUpdatePacket();
    }

    private void clearPressed() {
        for (int i = 0; i < pressed.length; i++) {
            pressed[i] = false;
        }
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
        refreshState();
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
        return Component.translatable("block.hbm.machine_rbmk_keypad");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.rbmkKeyPad");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, net.minecraft.world.entity.player.Player player) {
        return new RBMKKeypadConfigMenu(containerId, inventory, this);
    }

    public int getRotation() {
        return rotation & 3;
    }

    public boolean isControlColumn() {
        return controlColumn;
    }

    public int currentPercent() {
        return currentPercent;
    }

    public int preset(int slot) {
        return PRESETS[Math.max(0, Math.min(slot, PRESETS.length - 1))];
    }

    public boolean isPressed(int slot) {
        return pressed[Math.max(0, Math.min(slot, pressed.length - 1))];
    }

    public boolean isActive(int slot) {
        return active[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public boolean isPolling(int slot) {
        return polling[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public int color(int slot) {
        return colors[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String label(int slot) {
        return labels[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String channel(int slot) {
        return channels[Math.max(0, Math.min(slot, CHANNELS - 1))];
    }

    public String command(int slot) {
        return commands[Math.max(0, Math.min(slot, CHANNELS - 1))];
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
                if (!polling[i]) {
                    pressed[i] = false;
                }
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
            if (tag.contains("CfgCommand" + i)) {
                commands[i] = trimText(tag.getString("CfgCommand" + i), 48);
            }
        }
        setChanged();
        sendUpdatePacket();
    }

    private static String trimText(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    private boolean broadcastConfiguredCommand(ServerLevel level, int slot) {
        if (slot < 0 || slot >= CHANNELS || !active[slot]) {
            return false;
        }
        String channel = channels[slot];
        String command = commands[slot];
        if (channel == null || channel.isBlank() || command == null || command.isBlank()) {
            return false;
        }
        RBMKRadioNetwork.broadcast(level, channel, command);
        return true;
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("Rotation", rotation);
        tag.putBoolean("ControlColumn", controlColumn);
        tag.putInt("CurrentPercent", currentPercent);
        for (int i = 0; i < pressed.length; i++) {
            tag.putBoolean("Pressed" + i, pressed[i]);
            tag.putBoolean("Active" + i, active[i]);
            tag.putBoolean("Polling" + i, polling[i]);
            tag.putInt("Color" + i, colors[i]);
            tag.putString("Label" + i, labels[i]);
            tag.putString("Channel" + i, channels[i]);
            tag.putString("Command" + i, commands[i]);
        }
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        controlColumn = tag.getBoolean("ControlColumn");
        currentPercent = tag.getInt("CurrentPercent");
        for (int i = 0; i < CHANNELS; i++) {
            pressed[i] = tag.getBoolean("Pressed" + i);
            active[i] = tag.getBoolean("Active" + i);
            polling[i] = tag.getBoolean("Polling" + i);
            colors[i] = tag.contains("Color" + i) ? tag.getInt("Color" + i) : DEFAULT_COLORS[i];
            labels[i] = tag.contains("Label" + i) ? tag.getString("Label" + i) : "Button " + (i + 1);
            channels[i] = tag.getString("Channel" + i);
            commands[i] = tag.getString("Command" + i);
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
            tag.putBoolean("Active" + i, active[i]);
            tag.putBoolean("Polling" + i, polling[i]);
            tag.putInt("Color" + i, colors[i]);
            tag.putString("Label" + i, labels[i]);
            tag.putString("Channel" + i, channels[i]);
            tag.putString("Command" + i, commands[i]);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        for (int i = 0; i < CHANNELS; i++) {
            active[i] = tag.contains("Active" + i) ? tag.getBoolean("Active" + i) : true;
            polling[i] = tag.getBoolean("Polling" + i);
            colors[i] = tag.contains("Color" + i) ? Mth.clamp(tag.getInt("Color" + i), 0, 0xFFFFFF) : DEFAULT_COLORS[i];
            labels[i] = tag.contains("Label" + i) ? trimText(tag.getString("Label" + i), 15) : "Button " + (i + 1);
            channels[i] = trimText(tag.getString("Channel" + i), 16);
            commands[i] = trimText(tag.getString("Command" + i), 48);
        }
    }
}
