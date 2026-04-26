package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKLinkable;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Legacy RBMK display panel. It mirrors a 7x7 slice around a linked column and
 * renders it in-world, matching the old standalone panel behavior.
 */
public class RBMKDisplayEntity extends UpdateableBlockEntity implements RBMKLinkable {

    private static final int GRID_SIZE = 7;
    private static final int GRID_RADIUS = GRID_SIZE / 2;
    private static final int SYNC_INTERVAL_TICKS = 10;
    private static final int AUTO_LINK_RADIUS = 4;

    private final RBMKPeripheralEntity.ConsoleColumn[] columns = new RBMKPeripheralEntity.ConsoleColumn[GRID_SIZE * GRID_SIZE];

    @Nullable
    private BlockPos linkedColumn;
    private int rotation;
    private int tickCounter;

    public RBMKDisplayEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_DISPLAY_ENTITY.get(), pos, state);
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

        if (linkedColumn == null || !isColumnValid(linkedColumn)) {
            BlockPos inherited = findLinkedColumnFromNearbyConsole();
            if (inherited != null) {
                linkedColumn = inherited;
            }
        }

        if (linkedColumn == null || !isColumnValid(linkedColumn)) {
            clearColumns();
            sendUpdatePacket();
            return;
        }

        RBMKLevelContext context = RBMKManager.context(serverLevel);
        for (int index = 0; index < columns.length; index++) {
            BlockPos corePos = linkedColumn.offset(getXFromIndex(index), 0, getZFromIndex(index));
            RBMKColumnState state = context.column(corePos).orElse(null);
            columns[index] = state != null ? RBMKPeripheralEntity.ConsoleColumn.capture(serverLevel, state) : null;
        }
        sendUpdatePacket();
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("Rotation", rotation);
        CompoundTag gridTag = new CompoundTag();
        for (int i = 0; i < columns.length; i++) {
            RBMKPeripheralEntity.ConsoleColumn column = columns[i];
            if (column != null) {
                gridTag.put("C" + i, column.toTag(i));
            }
        }
        tag.put("Columns", gridTag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
        clearColumns();
        if (tag.contains("Columns", net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            CompoundTag gridTag = tag.getCompound("Columns");
            for (int i = 0; i < columns.length; i++) {
                String key = "C" + i;
                if (gridTag.contains(key, net.minecraft.nbt.Tag.TAG_COMPOUND)) {
                    columns[i] = RBMKPeripheralEntity.ConsoleColumn.fromTag(gridTag.getCompound(key));
                }
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        linkedColumn = tag.contains("LinkedColumn") ? BlockPos.of(tag.getLong("LinkedColumn")) : null;
        rotation = tag.getInt("Rotation");
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
        if (!isColumnValid(core)) {
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
        return Component.translatable("block.hbm.machine_rbmk_display");
    }

    public void rotateGrid() {
        rotation = (rotation + 1) & 3;
        setChanged();
        sendUpdatePacket();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Nullable
    public RBMKPeripheralEntity.ConsoleColumn getConsoleColumn(int index) {
        if (index < 0 || index >= columns.length) {
            return null;
        }
        return columns[index];
    }

    public int getRotationIndex() {
        return rotation & 3;
    }

    private boolean isColumnValid(BlockPos pos) {
        return level != null && level.getBlockEntity(pos) instanceof RBMKBaseEntity;
    }

    @Nullable
    private BlockPos findLinkedColumnFromNearbyConsole() {
        if (level == null) {
            return null;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        double closest = Double.MAX_VALUE;

        for (int dx = -AUTO_LINK_RADIUS; dx <= AUTO_LINK_RADIUS; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -AUTO_LINK_RADIUS; dz <= AUTO_LINK_RADIUS; dz++) {
                    cursor.set(worldPosition.getX() + dx, worldPosition.getY() + dy, worldPosition.getZ() + dz);
                    BlockEntity blockEntity = level.getBlockEntity(cursor);
                    if (!(blockEntity instanceof RBMKPeripheralEntity peripheral)) {
                        continue;
                    }
                    if (peripheral.getPeripheralType() != RBMKPeripheralType.CONSOLE) {
                        continue;
                    }
                    BlockPos linked = peripheral.getLinkedColumn();
                    if (linked == null || !isColumnValid(linked)) {
                        continue;
                    }
                    double distance = cursor.distSqr(worldPosition);
                    if (distance < closest) {
                        closest = distance;
                        best = linked.immutable();
                    }
                }
            }
        }
        return best;
    }

    private void clearColumns() {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = null;
        }
    }

    private int getXFromIndex(int index) {
        int i = index % GRID_SIZE - GRID_RADIUS;
        int j = index / GRID_SIZE - GRID_RADIUS;
        return switch (getRotationIndex()) {
            case 1 -> -j;
            case 2 -> -i;
            case 3 -> j;
            default -> i;
        };
    }

    private int getZFromIndex(int index) {
        int i = index % GRID_SIZE - GRID_RADIUS;
        int j = index / GRID_SIZE - GRID_RADIUS;
        return switch (getRotationIndex()) {
            case 1 -> i;
            case 2 -> -j;
            case 3 -> -i;
            default -> j;
        };
    }
}
