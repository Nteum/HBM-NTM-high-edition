package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.BaseSingleBlockMachine;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.gui.menu.RBMKRadioControllerMenu;
import com.hbm.reactor.rbmk.RBMKRadioNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class RBMKRadioControllerEntity extends UpdateableBlockEntity implements MenuProvider {

    private static final int MAX_CHANNEL_LEN = 16;
    private static final int MAX_SIGNAL_LEN = 64;

    private String channel = "";
    private String lastSignal = "";
    private boolean polling;

    public RBMKRadioControllerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_RADIO_CONTROLLER_ENTITY.get(), pos, state);
    }

    @Override
    protected void onUpdateServer() {
        if (!(level instanceof ServerLevel serverLevel) || channel.isBlank()) {
            return;
        }

        RBMKRadioNetwork.Signal signal = RBMKRadioNetwork.listen(serverLevel, channel);
        if (signal == null || signal.signal() == null) {
            return;
        }

        String payload = trimText(signal.signal(), MAX_SIGNAL_LEN);
        if (payload.isBlank()) {
            return;
        }
        if (!polling && payload.equals(lastSignal)) {
            return;
        }

        lastSignal = payload;
        applySignal(payload);
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        super.handleClientPacket(tag);
        if (tag.contains("Channel")) {
            channel = trimText(tag.getString("Channel"), MAX_CHANNEL_LEN);
        }
        if (tag.contains("Polling")) {
            polling = tag.getBoolean("Polling");
        }
        setChanged();
        sendUpdatePacket();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.rttyController");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new RBMKRadioControllerMenu(containerId, inventory, this);
    }

    public String getChannel() {
        return channel;
    }

    public boolean isPolling() {
        return polling;
    }

    private void applySignal(String payload) {
        String normalized = payload.trim();
        if (normalized.equalsIgnoreCase("selfdestruct")) {
            explodeSelf();
            return;
        }

        String command = normalized;
        String argument = "";
        int split = normalized.indexOf('!');
        if (split < 0) {
            split = normalized.indexOf(':');
        }
        if (split < 0) {
            split = normalized.indexOf(' ');
        }
        if (split >= 0) {
            command = normalized.substring(0, split).trim();
            argument = normalized.substring(split + 1).trim();
        }

        RBMKControlRodEntity rod = resolveTargetControlRod();
        if (rod == null || command.isBlank()) {
            return;
        }

        String key = command.toLowerCase(Locale.ROOT);
        if (key.equals("setrods") || key.equals("setrod") || key.equals("setrodspercent")) {
            int value = parsePercent(argument, 0);
            if (rod.setInsertionPercent(value)) {
                setChanged();
            }
            return;
        }
        if (key.equals("extendrods") || key.equals("extendrod") || key.equals("extendrodspercent")) {
            int delta = parsePercent(argument, 0);
            int current = Math.round(rod.getTargetInsertionFraction() * 100.0F);
            if (rod.setInsertionPercent(Mth.clamp(current + delta, 0, 100))) {
                setChanged();
            }
        }
    }

    @Nullable
    private RBMKControlRodEntity resolveTargetControlRod() {
        Level level = getLevel();
        if (level == null) {
            return null;
        }
        BlockState state = getBlockState();
        if (!state.hasProperty(BaseSingleBlockMachine.FACING)) {
            return null;
        }

        Direction facing = state.getValue(BaseSingleBlockMachine.FACING);
        BlockPos target = worldPosition.relative(facing.getOpposite());
        if (level.getBlockEntity(target) instanceof RBMKControlRodEntity rod) {
            return rod;
        }
        if (level.getBlockEntity(target.above()) instanceof RBMKControlRodEntity rodAbove) {
            return rodAbove;
        }
        return null;
    }

    private void explodeSelf() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.removeBlock(worldPosition, false);
        serverLevel.explode(null,
                worldPosition.getX() + 0.5D,
                worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D,
                2.5F,
                Level.ExplosionInteraction.BLOCK);
    }

    private static int parsePercent(String text, int fallback) {
        if (text == null || text.isBlank()) {
            return fallback;
        }
        try {
            return Mth.clamp(Integer.parseInt(text.trim()), 0, 100);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
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
        tag.putString("Channel", channel);
        tag.putBoolean("Polling", polling);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        channel = tag.getString("Channel");
        polling = tag.getBoolean("Polling");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Channel", channel);
        tag.putString("LastSignal", lastSignal);
        tag.putBoolean("Polling", polling);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        channel = trimText(tag.getString("Channel"), MAX_CHANNEL_LEN);
        lastSignal = trimText(tag.getString("LastSignal"), MAX_SIGNAL_LEN);
        polling = tag.getBoolean("Polling");
    }
}
