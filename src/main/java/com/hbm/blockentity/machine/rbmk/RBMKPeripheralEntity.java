package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.block.machine.rbmk.BlockRBMKFuelChannel;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheral;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheralLarge;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.registries.ModSounds;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Shared block entity for RBMK peripherals. Periodically searches for a nearby
 * RBMK column and mirrors its telemetry to container data for GUIs to consume.
 */
public class RBMKPeripheralEntity extends DummyableBlockEntity {

    private static final int SEARCH_INTERVAL_TICKS = 40;
    private static final int TELEMETRY_SLOTS = 10;
    private static final int GRID_SIZE = 15;
    private static final int GRID_RADIUS = GRID_SIZE / 2;
    private static final int GRID_DATA_START = TELEMETRY_SLOTS;
    private static final int DATA_SLOTS = TELEMETRY_SLOTS + GRID_SIZE;
    private static final int TYPE_INDEX = 9;

    private static final int GRID_CELL_EMPTY = 0;
    private static final int GRID_CELL_COLUMN = 1;
    private static final int GRID_CELL_FUEL = 2;
    private static final int GRID_CELL_CONTROL = 3;

    private final RBMKPeripheralType peripheralType;
    private final ContainerData containerData = new SimpleContainerData(DATA_SLOTS);
    private final int[] dataBacking = new int[DATA_SLOTS];

    private BlockPos linkedColumn;
    private BlockPos manualLink;
    private int tickCounter;

    public RBMKPeripheralEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_PERIPHERAL_ENTITY.get(), pos, state);
        this.items = NonNullList.create();
        this.slotModes = java.util.List.of();
        this.peripheralType = resolvePeripheralType(state.getBlock());
        this.multiblockData = MultiblockData.mapping.get(state.getBlock());
    }

    private static RBMKPeripheralType resolvePeripheralType(Block block) {
        if (block instanceof BlockRBMKPeripheral peripheral) {
            return peripheral.getPeripheralType();
        }
        if (block instanceof BlockRBMKPeripheralLarge peripheralLarge) {
            return peripheralLarge.getPeripheralType();
        }
        return RBMKPeripheralType.CONSOLE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (manualLink != null) {
            tag.putLong("ManualLink", manualLink.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ManualLink")) {
            manualLink = BlockPos.of(tag.getLong("ManualLink"));
            linkedColumn = manualLink;
        } else {
            manualLink = null;
        }
    }

    @Override
    protected void onUpdateServer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        // embed the type ordinal for the client screen
        dataBacking[TYPE_INDEX] = peripheralType.ordinal();

        if (manualLink != null) {
            if (isColumnValid(manualLink)) {
                linkedColumn = manualLink;
            } else {
                manualLink = null;
                linkedColumn = null;
            }
        }

        if (manualLink == null && (++tickCounter % SEARCH_INTERVAL_TICKS == 0 || !isLinkedColumnValid())) {
            linkedColumn = findNearestColumn();
        }

        if (linkedColumn == null) {
            clearTelemetry();
            pushData();
            return;
        }

        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Optional<RBMKColumnState> columnOpt = context.column(linkedColumn);
        if (columnOpt.isEmpty()) {
            linkedColumn = null;
            clearTelemetry();
            pushData();
            return;
        }

        RBMKColumnState column = columnOpt.get();
        dataBacking[0] = (int) Math.round(column.heat() * 10.0D);
        dataBacking[1] = (int) Math.round(column.settings().meltdownHeat() * 10.0D);
        dataBacking[6] = (int) Math.round(column.controlRodInsertion() * 100.0D);
        dataBacking[7] = (int) Math.round(context.controlRodAverage() * 100.0D);
        dataBacking[8] = context.snapshot().size();

        RBMKBaseEntity base = resolveBaseEntity();
        if (base != null) {
            dataBacking[2] = (int) Math.min(Integer.MAX_VALUE, base.getEnergyStored());
            dataBacking[3] = (int) Math.min(Integer.MAX_VALUE, base.getEnergyCapacity());
            dataBacking[4] = base.getWaterAmount();
            dataBacking[5] = base.getSteamAmount();
        } else {
            dataBacking[2] = 0;
            dataBacking[3] = 0;
            dataBacking[4] = 0;
            dataBacking[5] = 0;
        }

        updateGrid(serverLevel, context);
        pushData();
    }

    private void pushData() {
        for (int i = 0; i < dataBacking.length; i++) {
            containerData.set(i, dataBacking[i]);
        }
    }

    private void clearTelemetry() {
        for (int i = 0; i < dataBacking.length; i++) {
            if (i != TYPE_INDEX) {
                dataBacking[i] = 0;
            }
        }
    }

    private void updateGrid(ServerLevel serverLevel, RBMKLevelContext context) {
        for (int i = GRID_DATA_START; i < DATA_SLOTS; i++) {
            dataBacking[i] = 0;
        }
        if (linkedColumn == null) {
            return;
        }
        BlockPos origin = linkedColumn;
        for (BlockPos corePos : context.snapshot().keySet()) {
            int dx = corePos.getX() - origin.getX();
            int dz = corePos.getZ() - origin.getZ();
            if (Math.abs(dx) > GRID_RADIUS || Math.abs(dz) > GRID_RADIUS) {
                continue;
            }
            int col = dx + GRID_RADIUS;
            int row = dz + GRID_RADIUS;
            int state = resolveGridState(serverLevel, corePos);
            setGridCell(row, col, state);
        }
    }

    private int resolveGridState(ServerLevel serverLevel, BlockPos corePos) {
        BlockState aboveState = serverLevel.getBlockState(corePos.above());
        Block aboveBlock = aboveState.getBlock();
        if (aboveBlock instanceof BlockRBMKFuelChannel) {
            return GRID_CELL_FUEL;
        }
        if (aboveBlock instanceof BlockRBMKControlRod) {
            return GRID_CELL_CONTROL;
        }
        return GRID_CELL_COLUMN;
    }

    private void setGridCell(int row, int col, int state) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return;
        }
        int index = GRID_DATA_START + row;
        int shift = col * 2;
        int mask = 0x3 << shift;
        dataBacking[index] = (dataBacking[index] & ~mask) | ((state & 0x3) << shift);
    }

    private boolean isLinkedColumnValid() {
        if (linkedColumn == null || level == null) {
            return false;
        }
        BlockState state = level.getBlockState(linkedColumn);
        if (!(state.getBlock() instanceof BlockRBMKBase)) {
            return false;
        }
        return state.hasProperty(BlockRBMKBase.IS_CORE) && state.getValue(BlockRBMKBase.IS_CORE);
    }

    private boolean isColumnValid(BlockPos pos) {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockRBMKBase)) {
            return false;
        }
        return state.hasProperty(BlockRBMKBase.IS_CORE) && state.getValue(BlockRBMKBase.IS_CORE);
    }

    @Nullable
    private BlockPos findNearestColumn() {
        if (level == null) {
            return null;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos best = null;
        double closest = Double.MAX_VALUE;
        int horizontal = 8;
        int vertical = 6;
        for (int dx = -horizontal; dx <= horizontal; dx++) {
            for (int dy = -vertical; dy <= vertical; dy++) {
                for (int dz = -horizontal; dz <= horizontal; dz++) {
                    cursor.set(worldPosition.getX() + dx, worldPosition.getY() + dy, worldPosition.getZ() + dz);
                    if (!level.hasChunkAt(cursor)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(cursor);
                    if (!(state.getBlock() instanceof BlockRBMKBase)) {
                        continue;
                    }
                    if (!state.hasProperty(BlockRBMKBase.IS_CORE) || !state.getValue(BlockRBMKBase.IS_CORE)) {
                        continue;
                    }
                    double dist = cursor.distSqr(worldPosition);
                    if (dist < closest) {
                        closest = dist;
                        best = cursor.immutable();
                    }
                }
            }
        }
        return best;
    }

    @Nullable
    private RBMKBaseEntity resolveBaseEntity() {
        if (linkedColumn == null || level == null) {
            return null;
        }
        BlockEntity entity = level.getBlockEntity(linkedColumn);
        if (entity instanceof RBMKBaseEntity baseEntity) {
            return baseEntity;
        }
        return null;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public RBMKPeripheralType getPeripheralType() {
        return peripheralType;
    }

    public boolean triggerAz5() {
        if (peripheralType != RBMKPeripheralType.CONSOLE || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Map<BlockPos, RBMKColumnState> snapshot = context.snapshot();
        if (snapshot.isEmpty()) {
            return false;
        }
        boolean changed = false;
        for (BlockPos corePos : snapshot.keySet()) {
            context.setControlRodInsertion(corePos, 1.0D);
            BlockPos rodPos = corePos.above();
            BlockEntity entity = level.getBlockEntity(rodPos);
            if (entity instanceof RBMKControlRodEntity controlRod) {
                controlRod.engageAz5();
                changed = true;
            } else {
                BlockState rodState = level.getBlockState(rodPos);
                if (rodState.getBlock() instanceof BlockRBMKControlRod
                        && rodState.hasProperty(BlockRBMKControlRod.INSERTION)) {
                    level.setBlock(rodPos, rodState.setValue(BlockRBMKControlRod.INSERTION, BlockRBMKControlRod.MAX_INSERTION), Block.UPDATE_ALL);
                    changed = true;
                }
            }
        }
        if (changed) {
            level.playSound(null, worldPosition, ModSounds.BLOCK_RBMK_AZ5_COVER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return changed;
    }

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
        manualLink = core.immutable();
        linkedColumn = manualLink;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return true;
    }

    public void clearManualLink() {
        if (manualLink != null) {
            manualLink = null;
            linkedColumn = null;
            setChanged();
        }
    }

    @Override
    public Component getDefaultName() {
        return peripheralType.displayName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new com.hbm.gui.menu.RBMKPeripheralMenu(containerId, inventory, this, getContainerData());
    }
}
