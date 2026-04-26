package com.hbm.blockentity.machine.rbmk;

import com.hbm.api.Mode;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.block.base.BlockContainerBase;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheral;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheralLarge;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKColumnType;
import com.hbm.reactor.rbmk.RBMKColumns;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKLinkable;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.reactor.rbmk.RBMKScreenType;
import com.hbm.registries.ModSounds;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Shared block entity for RBMK peripherals. Consoles mirror a 15x15 slice of
 * the linked RBMK into a client cache so the GUI can match the legacy 1.7.10
 * interaction model instead of relying on a handful of summary integers.
 */
public class RBMKPeripheralEntity extends DummyableBlockEntity implements RBMKLinkable {

    private static final int SEARCH_INTERVAL_TICKS = 40;
    private static final int CONSOLE_SCAN_INTERVAL_TICKS = 10;
    private static final int CLIENT_SYNC_INTERVAL_TICKS = 10;
    private static final int TELEMETRY_SLOTS = 10;
    private static final int GRID_SIZE = 15;
    private static final int GRID_RADIUS = GRID_SIZE / 2;
    private static final int GRID_DATA_START = TELEMETRY_SLOTS;
    private static final int DATA_SLOTS = TELEMETRY_SLOTS + GRID_SIZE * GRID_SIZE;
    private static final int TYPE_INDEX = 9;
    private static final int SCREEN_COUNT = 6;
    private static final int FLUX_BUFFER_SIZE = 60;
    private static final int TREND_BUFFER_SIZE = 60;
    private static final int AUTOLOADER_INPUT_SLOTS = 9;
    private static final int AUTOLOADER_OUTPUT_SLOTS = 9;
    private static final int AUTOLOADER_MIN_CYCLE = 5;
    private static final int AUTOLOADER_MAX_CYCLE = 95;

    private static final int GRID_CELL_EMPTY = 0;
    private static final int GRID_CELL_COLUMN = 1;
    private static final int GRID_CELL_FUEL = 2;
    private static final int GRID_CELL_CONTROL = 3;
    private static final int GRID_CELL_CONTROL_AUTO = 4;
    private static final int GRID_CELL_BOILER = 5;
    private static final int GRID_CELL_MODERATOR = 6;
    private static final int GRID_CELL_ABSORBER = 7;
    private static final int GRID_CELL_REFLECTOR = 8;
    private static final int GRID_CELL_OUTGASSER = 9;
    private static final int GRID_CELL_BREEDER = 10;
    private static final int GRID_CELL_STORAGE = 11;
    private static final int GRID_CELL_COOLER = 12;
    private static final int GRID_CELL_HEATEX = 13;

    private final RBMKPeripheralType peripheralType;
    private final ContainerData containerData = new SimpleContainerData(DATA_SLOTS);
    private final int[] dataBacking = new int[DATA_SLOTS];
    private final ConsoleColumn[] consoleColumns = new ConsoleColumn[GRID_SIZE * GRID_SIZE];
    private final ConsoleScreen[] screens = new ConsoleScreen[SCREEN_COUNT];
    private final int[] fluxBuffer = new int[FLUX_BUFFER_SIZE];
    private final int[] waterBuffer = new int[TREND_BUFFER_SIZE];
    private final int[] controlBuffer = new int[TREND_BUFFER_SIZE];

    private BlockPos linkedColumn;
    private BlockPos manualLink;
    private int tickCounter;
    private int autoloaderCycle = 50;
    private int autoloaderDelay;
    private boolean autoloaderWorking;
    private int telemetryHeat;
    private int telemetryWater;
    private int telemetrySteam;
    private int telemetryControl;
    private int telemetryFlux;
    private int telemetryFuelRods;

    public RBMKPeripheralEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_PERIPHERAL_ENTITY.get(), pos, state);
        this.peripheralType = resolvePeripheralType(state.getBlock());
        if (peripheralType == RBMKPeripheralType.AUTOLOADER) {
            this.items = NonNullList.withSize(AUTOLOADER_INPUT_SLOTS + AUTOLOADER_OUTPUT_SLOTS, ItemStack.EMPTY);
            this.slotModes = new ModeBuilder().addModes(AUTOLOADER_INPUT_SLOTS, Mode.INPUT, AUTOLOADER_OUTPUT_SLOTS, Mode.OUTPUT).get();
        } else {
            this.items = NonNullList.create();
            this.slotModes = java.util.List.of();
        }
        this.multiblockData = MultiblockData.mapping.get(state.getBlock());
        for (int i = 0; i < screens.length; i++) {
            screens[i] = new ConsoleScreen();
        }
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

    private boolean requiresLinkedColumn() {
        return peripheralType == RBMKPeripheralType.CONSOLE
                || peripheralType == RBMKPeripheralType.CRANE_CONSOLE
                || peripheralType == RBMKPeripheralType.AUTOLOADER;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (manualLink != null) {
            tag.putLong("ManualLink", manualLink.asLong());
        }
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("AutoCycle", autoloaderCycle);
        tag.putInt("AutoDelay", autoloaderDelay);
        tag.putBoolean("AutoWorking", autoloaderWorking);
        tag.putInt("THeat", telemetryHeat);
        tag.putInt("TWater", telemetryWater);
        tag.putInt("TSteam", telemetrySteam);
        tag.putInt("TControl", telemetryControl);
        tag.putInt("TFlux", telemetryFlux);
        tag.putInt("TFuelRods", telemetryFuelRods);
        tag.putIntArray("WaterBuffer", waterBuffer);
        tag.putIntArray("ControlBuffer", controlBuffer);
        saveScreens(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ManualLink")) {
            manualLink = BlockPos.of(tag.getLong("ManualLink"));
            linkedColumn = manualLink;
        } else if (tag.contains("LinkedColumn")) {
            manualLink = null;
            linkedColumn = BlockPos.of(tag.getLong("LinkedColumn"));
        } else {
            manualLink = null;
            linkedColumn = null;
        }
        autoloaderCycle = Mth.clamp(tag.getInt("AutoCycle"), AUTOLOADER_MIN_CYCLE, AUTOLOADER_MAX_CYCLE);
        if (autoloaderCycle == 0) {
            autoloaderCycle = 50;
        }
        autoloaderDelay = Math.max(0, tag.getInt("AutoDelay"));
        autoloaderWorking = tag.getBoolean("AutoWorking");
        telemetryHeat = tag.getInt("THeat");
        telemetryWater = tag.getInt("TWater");
        telemetrySteam = tag.getInt("TSteam");
        telemetryControl = tag.getInt("TControl");
        telemetryFlux = tag.getInt("TFlux");
        telemetryFuelRods = tag.getInt("TFuelRods");
        copyTrendBuffer(tag.getIntArray("WaterBuffer"), waterBuffer);
        copyTrendBuffer(tag.getIntArray("ControlBuffer"), controlBuffer);
        loadScreens(tag);
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        if (manualLink != null) {
            tag.putLong("ManualLink", manualLink.asLong());
        }
        if (linkedColumn != null) {
            tag.putLong("LinkedColumn", linkedColumn.asLong());
        }
        tag.putInt("PeripheralType", peripheralType.ordinal());
        tag.putIntArray("FluxBuffer", fluxBuffer);
        tag.putIntArray("WaterBuffer", waterBuffer);
        tag.putIntArray("ControlBuffer", controlBuffer);
        tag.putInt("AutoCycle", autoloaderCycle);
        tag.putBoolean("AutoWorking", autoloaderWorking);
        tag.putInt("THeat", telemetryHeat);
        tag.putInt("TWater", telemetryWater);
        tag.putInt("TSteam", telemetrySteam);
        tag.putInt("TControl", telemetryControl);
        tag.putInt("TFlux", telemetryFlux);
        tag.putInt("TFuelRods", telemetryFuelRods);

        ListTag columnList = new ListTag();
        for (int i = 0; i < consoleColumns.length; i++) {
            ConsoleColumn column = consoleColumns[i];
            if (column != null) {
                columnList.add(column.toTag(i));
            }
        }
        tag.put("ConsoleColumns", columnList);
        saveScreens(tag);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains("ManualLink")) {
            manualLink = BlockPos.of(tag.getLong("ManualLink"));
        } else {
            manualLink = null;
        }
        if (tag.contains("LinkedColumn")) {
            linkedColumn = BlockPos.of(tag.getLong("LinkedColumn"));
        } else if (manualLink != null) {
            linkedColumn = manualLink;
        } else {
            linkedColumn = null;
        }
        loadConsoleColumns(tag);
        loadScreens(tag);
        int[] packetFlux = tag.getIntArray("FluxBuffer");
        Arrays.fill(fluxBuffer, 0);
        System.arraycopy(packetFlux, 0, fluxBuffer, 0, Math.min(packetFlux.length, fluxBuffer.length));
        copyTrendBuffer(tag.getIntArray("WaterBuffer"), waterBuffer);
        copyTrendBuffer(tag.getIntArray("ControlBuffer"), controlBuffer);
        if (tag.contains("AutoCycle")) {
            autoloaderCycle = Mth.clamp(tag.getInt("AutoCycle"), AUTOLOADER_MIN_CYCLE, AUTOLOADER_MAX_CYCLE);
        }
        autoloaderWorking = tag.getBoolean("AutoWorking");
        telemetryHeat = tag.getInt("THeat");
        telemetryWater = tag.getInt("TWater");
        telemetrySteam = tag.getInt("TSteam");
        telemetryControl = tag.getInt("TControl");
        telemetryFlux = tag.getInt("TFlux");
        telemetryFuelRods = tag.getInt("TFuelRods");
    }

    @Override
    protected void onUpdateServer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        tickCounter++;
        dataBacking[TYPE_INDEX] = peripheralType.ordinal();

        if (!requiresLinkedColumn()) {
            linkedColumn = null;
            manualLink = null;
            clearTelemetry();
            clearConsoleData();
            pushData();
            syncClientIfNeeded();
            return;
        }

        if (manualLink != null) {
            if (isColumnValid(manualLink)) {
                linkedColumn = manualLink;
            } else {
                manualLink = null;
                linkedColumn = null;
            }
        }

        int scanInterval = peripheralType == RBMKPeripheralType.CONSOLE ? CONSOLE_SCAN_INTERVAL_TICKS : SEARCH_INTERVAL_TICKS;
        if (manualLink == null && (tickCounter % scanInterval == 0 || !isLinkedColumnValid())) {
            linkedColumn = findNearestColumn();
        }

        if (peripheralType == RBMKPeripheralType.AUTOLOADER) {
            updateAutoloader(serverLevel);
            pushData();
            return;
        }

        if (linkedColumn == null) {
            clearTelemetry();
            clearConsoleData();
            pushData();
            syncClientIfNeeded();
            return;
        }

        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Optional<RBMKColumnState> columnOpt = context.column(linkedColumn);
        if (columnOpt.isEmpty()) {
            linkedColumn = null;
            clearTelemetry();
            clearConsoleData();
            pushData();
            syncClientIfNeeded();
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

        telemetryFuelRods = updateGrid(serverLevel, context);
        telemetryFlux = updateFluxBuffer(context);
        telemetryHeat = dataBacking[0];
        telemetryWater = dataBacking[4];
        telemetrySteam = dataBacking[5];
        telemetryControl = dataBacking[7];
        pushTrend(waterBuffer, telemetryWater);
        pushTrend(controlBuffer, telemetryControl);
        prepareScreenDisplays();
        pushData();
        syncClientIfNeeded();
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        if (!(level instanceof ServerLevel serverLevel) || linkedColumn == null) {
            return;
        }
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        boolean dirty = false;

        if (tag.contains("toggle")) {
            int slot = Mth.clamp(tag.getInt("toggle"), 0, screens.length - 1);
            screens[slot].type = screens[slot].type.next();
            dirty = true;
        }

        if (tag.contains("id")) {
            int slot = Mth.clamp(tag.getInt("id"), 0, screens.length - 1);
            screens[slot].columns = readSelectedColumns(tag);
            dirty = true;
        }

        if (tag.contains("assignColor")) {
            int color = Mth.clamp(tag.getInt("assignColor"), 0, RBMKControlRodEntity.ControlGroup.values().length - 1);
            RBMKControlRodEntity.ControlGroup group = RBMKControlRodEntity.ControlGroup.values()[color];
            for (int index : readSelectedColumns(tag)) {
                BlockEntity entity = level.getBlockEntity(getCorePosForIndex(index).above());
                if (entity instanceof RBMKControlRodEntity rod) {
                    rod.setColorGroup(group);
                    dirty = true;
                }
            }
        }

        if (tag.contains("compressor")) {
            for (int index : readSelectedColumns(tag)) {
                BlockEntity entity = level.getBlockEntity(getCorePosForIndex(index).above());
                if (entity instanceof RBMKBoilerEntity boiler) {
                    boiler.cycleCompression();
                    dirty = true;
                }
            }
        }

        if (tag.contains("level")) {
            double requested = tag.getDouble("level");
            double fraction = requested > 1.0D ? requested / 100.0D : requested;
            fraction = Mth.clamp(fraction, 0.0D, 1.0D);
            for (int index : readSelectedColumns(tag)) {
                BlockPos topPos = getCorePosForIndex(index).above();
                BlockEntity entity = level.getBlockEntity(topPos);
                if (entity instanceof RBMKControlRodEntity rod && rod.setInsertionFraction(fraction)) {
                    context.setControlRodInsertion(topPos.below(), fraction);
                    dirty = true;
                }
            }
        }

        if (dirty) {
            setChanged();
            prepareScreenDisplays();
            sendUpdatePacket();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void syncClientIfNeeded() {
        if (peripheralType == RBMKPeripheralType.CONSOLE && tickCounter % CLIENT_SYNC_INTERVAL_TICKS == 0) {
            sendUpdatePacket();
        }
    }

    private void updateAutoloader(ServerLevel serverLevel) {
        clearConsoleData();
        dataBacking[6] = autoloaderCycle;
        dataBacking[7] = linkedColumn != null ? 1 : 0;
        dataBacking[8] = autoloaderWorking ? 1 : 0;

        if (autoloaderDelay > 0) {
            autoloaderDelay--;
        }

        if (linkedColumn == null) {
            clearTelemetry();
            dataBacking[TYPE_INDEX] = peripheralType.ordinal();
            dataBacking[6] = autoloaderCycle;
            dataBacking[7] = 0;
            dataBacking[8] = 0;
            autoloaderWorking = false;
            return;
        }

        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Optional<RBMKColumnState> columnOpt = context.column(linkedColumn);
        if (columnOpt.isEmpty()) {
            linkedColumn = null;
            clearTelemetry();
            dataBacking[TYPE_INDEX] = peripheralType.ordinal();
            dataBacking[6] = autoloaderCycle;
            dataBacking[7] = 0;
            dataBacking[8] = 0;
            autoloaderWorking = false;
            return;
        }

        RBMKColumnState column = columnOpt.get();
        dataBacking[0] = (int) Math.round(column.heat() * 10.0D);
        dataBacking[1] = (int) Math.round(column.settings().meltdownHeat() * 10.0D);

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

        autoloaderWorking = false;
        if (autoloaderDelay <= 0 && tickCounter % 20 == 0) {
            BlockEntity entity = level.getBlockEntity(linkedColumn.above());
            if (entity instanceof RBMKFuelChannelEntity fuelChannel) {
                autoloaderWorking = runAutoloaderCycle(fuelChannel);
                if (autoloaderWorking) {
                    autoloaderDelay = 20;
                }
            }
        }

        dataBacking[6] = autoloaderCycle;
        dataBacking[7] = 1;
        dataBacking[8] = autoloaderWorking ? 1 : 0;
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
        telemetryHeat = 0;
        telemetryWater = 0;
        telemetrySteam = 0;
        telemetryControl = 0;
    }

    private void clearConsoleData() {
        Arrays.fill(consoleColumns, null);
        Arrays.fill(fluxBuffer, 0);
        Arrays.fill(waterBuffer, 0);
        Arrays.fill(controlBuffer, 0);
        telemetryFlux = 0;
        telemetryFuelRods = 0;
        for (ConsoleScreen screen : screens) {
            screen.display = null;
        }
    }

    private int updateGrid(ServerLevel serverLevel, RBMKLevelContext context) {
        for (int i = GRID_DATA_START; i < DATA_SLOTS; i++) {
            dataBacking[i] = 0;
        }
        Arrays.fill(consoleColumns, null);
        if (linkedColumn == null) {
            return 0;
        }

        int fuelRods = 0;
        for (Map.Entry<BlockPos, RBMKColumnState> entry : context.snapshot().entrySet()) {
            int index = getIndexForCorePos(entry.getKey());
            if (index < 0 || index >= consoleColumns.length) {
                continue;
            }
            RBMKColumnState state = entry.getValue();
            if ((state.columnType() == RBMKColumnType.FUEL || state.columnType() == RBMKColumnType.FUEL_SIM) && state.hasRod()) {
                fuelRods++;
            }
            ConsoleColumn column = ConsoleColumn.capture(serverLevel, state);
            consoleColumns[index] = column;
            setGridCellByIndex(index, resolveGridState(serverLevel, entry.getKey()));
        }
        return fuelRods;
    }

    private int updateFluxBuffer(RBMKLevelContext context) {
        int totalFlux = 0;
        for (RBMKColumnState state : context.snapshot().values()) {
            if (state.columnType() == RBMKColumnType.FUEL || state.columnType() == RBMKColumnType.FUEL_SIM) {
                totalFlux += (int) Math.round(state.fastFlux() + state.slowFlux());
            }
        }
        System.arraycopy(fluxBuffer, 1, fluxBuffer, 0, fluxBuffer.length - 1);
        fluxBuffer[fluxBuffer.length - 1] = totalFlux;
        return totalFlux;
    }

    private void prepareScreenDisplays() {
        for (ConsoleScreen screen : screens) {
            if (screen.type == RBMKScreenType.NONE || screen.columns.length == 0) {
                screen.display = null;
                continue;
            }

            double total = 0.0D;
            int count = 0;
            for (int index : screen.columns) {
                if (index < 0 || index >= consoleColumns.length) {
                    continue;
                }
                ConsoleColumn column = consoleColumns[index];
                if (column == null) {
                    continue;
                }
                CompoundTag data = column.data();
                switch (screen.type) {
                    case COL_TEMP -> {
                        total += data.getDouble("heat");
                        count++;
                    }
                    case ROD_EXTRACTION -> {
                        if (data.contains("level")) {
                            total += data.getDouble("level") * 100.0D;
                            count++;
                        }
                    }
                    case FUEL_DEPLETION -> {
                        if (data.contains("enrichment")) {
                            total += 100.0D - (data.getDouble("enrichment") * 100.0D);
                            count++;
                        }
                    }
                    case FUEL_POISON -> {
                        if (data.contains("xenon")) {
                            total += data.getDouble("xenon");
                            count++;
                        }
                    }
                    case FUEL_TEMP -> {
                        if (data.contains("c_heat")) {
                            total += data.getDouble("c_heat");
                            count++;
                        }
                    }
                    case NONE -> {
                    }
                }
            }

            if (count <= 0) {
                screen.display = null;
                continue;
            }

            double value = total / count;
            screen.display = switch (screen.type) {
                case COL_TEMP -> String.format(Locale.ROOT, "%.1f°C", value);
                case ROD_EXTRACTION, FUEL_DEPLETION, FUEL_POISON -> String.format(Locale.ROOT, "%.1f%%", value);
                case FUEL_TEMP -> String.format(Locale.ROOT, "%.1f°C", value);
                case NONE -> null;
            };
        }
    }

    private int resolveGridState(ServerLevel serverLevel, BlockPos corePos) {
        return columnTypeToCell(RBMKColumns.determineColumnType(serverLevel, corePos));
    }

    private int columnTypeToCell(final RBMKColumnType type) {
        return switch (type) {
            case FUEL, FUEL_SIM -> GRID_CELL_FUEL;
            case CONTROL -> GRID_CELL_CONTROL;
            case CONTROL_AUTO -> GRID_CELL_CONTROL_AUTO;
            case BOILER -> GRID_CELL_BOILER;
            case MODERATOR -> GRID_CELL_MODERATOR;
            case ABSORBER -> GRID_CELL_ABSORBER;
            case REFLECTOR -> GRID_CELL_REFLECTOR;
            case OUTGASSER -> GRID_CELL_OUTGASSER;
            case BREEDER -> GRID_CELL_BREEDER;
            case STORAGE -> GRID_CELL_STORAGE;
            case COOLER -> GRID_CELL_COOLER;
            case HEATEX -> GRID_CELL_HEATEX;
            default -> GRID_CELL_COLUMN;
        };
    }

    private void setGridCellByIndex(int index, int state) {
        if (index < 0 || index >= GRID_SIZE * GRID_SIZE) {
            return;
        }
        int row = index / GRID_SIZE;
        int col = index % GRID_SIZE;
        setGridCell(row, col, state);
    }

    private void setGridCell(int row, int col, int state) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) {
            return;
        }
        int index = GRID_DATA_START + row * GRID_SIZE + col;
        if (index < 0 || index >= DATA_SLOTS) {
            return;
        }
        dataBacking[index] = state;
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

    @Override
    public Component getLinkDisplayName() {
        return peripheralType.displayName();
    }

    public boolean triggerAz5() {
        if (peripheralType != RBMKPeripheralType.CONSOLE || !(level instanceof ServerLevel serverLevel) || linkedColumn == null) {
            return false;
        }
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        boolean changed = false;
        for (int index = 0; index < GRID_SIZE * GRID_SIZE; index++) {
            BlockPos corePos = getCorePosForIndex(index);
            if (!context.column(corePos).isPresent()) {
                continue;
            }
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
            prepareScreenDisplays();
            sendUpdatePacket();
        }
        return changed;
    }

    public boolean linkToColumn(BlockPos target) {
        if (level == null || !requiresLinkedColumn()) {
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
        sendUpdatePacket();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        return true;
    }

    public void clearManualLink() {
        if (manualLink != null) {
            manualLink = null;
            linkedColumn = null;
            setChanged();
            sendUpdatePacket();
        }
    }

    @Override
    public Component getDefaultName() {
        return peripheralType.displayName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (peripheralType == RBMKPeripheralType.AUTOLOADER) {
            return new com.hbm.gui.menu.RBMKAutoloaderMenu(containerId, inventory, this);
        }
        return new com.hbm.gui.menu.RBMKPeripheralMenu(containerId, inventory, this);
    }

    @Nullable
    public ConsoleColumn getConsoleColumn(int index) {
        if (index < 0 || index >= consoleColumns.length) {
            return null;
        }
        return consoleColumns[index];
    }

    public ConsoleScreen getScreen(int index) {
        if (index < 0 || index >= screens.length) {
            return new ConsoleScreen();
        }
        return screens[index];
    }

    public int[] getFluxBuffer() {
        return fluxBuffer;
    }

    public int[] getWaterBuffer() {
        return waterBuffer;
    }

    public int[] getControlBuffer() {
        return controlBuffer;
    }

    public int getTelemetryHeat() {
        return telemetryHeat;
    }

    public int getTelemetryWater() {
        return telemetryWater;
    }

    public int getTelemetrySteam() {
        return telemetrySteam;
    }

    public int getTelemetryControl() {
        return telemetryControl;
    }

    public int getTelemetryFlux() {
        return telemetryFlux;
    }

    public int getTelemetryFuelRods() {
        return telemetryFuelRods;
    }

    public int getAutoloaderCycle() {
        return autoloaderCycle;
    }

    public boolean isAutoloaderWorking() {
        return autoloaderWorking;
    }

    public boolean adjustAutoloaderCycle(int delta) {
        if (peripheralType != RBMKPeripheralType.AUTOLOADER) {
            return false;
        }
        int next = Mth.clamp(autoloaderCycle + delta, AUTOLOADER_MIN_CYCLE, AUTOLOADER_MAX_CYCLE);
        if (next == autoloaderCycle) {
            return false;
        }
        autoloaderCycle = next;
        dataBacking[6] = autoloaderCycle;
        containerData.set(6, autoloaderCycle);
        setChanged();
        if (level != null) {
            sendUpdatePacket();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
        return true;
    }

    @Nullable
    public BlockPos getLinkedColumn() {
        return linkedColumn;
    }

    public int getIndexForCorePos(BlockPos corePos) {
        if (linkedColumn == null) {
            return -1;
        }
        int dx = corePos.getX() - linkedColumn.getX();
        int dz = corePos.getZ() - linkedColumn.getZ();
        int i;
        int j;
        switch (getRotationIndex()) {
            case 1 -> {
                i = dz;
                j = -dx;
            }
            case 2 -> {
                i = -dx;
                j = -dz;
            }
            case 3 -> {
                i = -dz;
                j = dx;
            }
            default -> {
                i = dx;
                j = dz;
            }
        }
        if (Math.abs(i) > GRID_RADIUS || Math.abs(j) > GRID_RADIUS) {
            return -1;
        }
        return (j + GRID_RADIUS) * GRID_SIZE + (i + GRID_RADIUS);
    }

    public BlockPos getCorePosForIndex(int index) {
        if (linkedColumn == null) {
            return BlockPos.ZERO;
        }
        int i = index % GRID_SIZE - GRID_RADIUS;
        int j = index / GRID_SIZE - GRID_RADIUS;
        int dx;
        int dz;
        switch (getRotationIndex()) {
            case 1 -> {
                dx = -j;
                dz = i;
            }
            case 2 -> {
                dx = -i;
                dz = -j;
            }
            case 3 -> {
                dx = j;
                dz = -i;
            }
            default -> {
                dx = i;
                dz = j;
            }
        }
        return linkedColumn.offset(dx, 0, dz);
    }

    private int getRotationIndex() {
        BlockState state = getBlockState();
        if (!state.hasProperty(BlockContainerBase.FACING)) {
            return 0;
        }
        Direction facing = state.getValue(BlockContainerBase.FACING);
        return switch (facing) {
            case WEST -> 1;
            case NORTH -> 0;
            case EAST -> 3;
            case SOUTH -> 2;
            default -> 0;
        };
    }

    private void saveScreens(CompoundTag tag) {
        ListTag screensTag = new ListTag();
        for (int i = 0; i < screens.length; i++) {
            screensTag.add(screens[i].toTag(i));
        }
        tag.put("Screens", screensTag);
    }

    private void loadScreens(CompoundTag tag) {
        if (!tag.contains("Screens", Tag.TAG_LIST)) {
            return;
        }
        ListTag screensTag = tag.getList("Screens", Tag.TAG_COMPOUND);
        for (int i = 0; i < screens.length; i++) {
            screens[i] = new ConsoleScreen();
        }
        for (Tag entry : screensTag) {
            CompoundTag screenTag = (CompoundTag) entry;
            int slot = Mth.clamp(screenTag.getInt("Slot"), 0, screens.length - 1);
            screens[slot] = ConsoleScreen.fromTag(screenTag);
        }
    }

    private void loadConsoleColumns(CompoundTag tag) {
        Arrays.fill(consoleColumns, null);
        if (!tag.contains("ConsoleColumns", Tag.TAG_LIST)) {
            return;
        }
        ListTag columnList = tag.getList("ConsoleColumns", Tag.TAG_COMPOUND);
        for (Tag entry : columnList) {
            CompoundTag columnTag = (CompoundTag) entry;
            int index = columnTag.getInt("Index");
            if (index >= 0 && index < consoleColumns.length) {
                consoleColumns[index] = ConsoleColumn.fromTag(columnTag);
            }
        }
    }

    private int[] readSelectedColumns(CompoundTag tag) {
        if (tag.contains("cols", Tag.TAG_INT_ARRAY)) {
            int[] raw = tag.getIntArray("cols");
            return Arrays.stream(raw).filter(i -> i >= 0 && i < GRID_SIZE * GRID_SIZE).distinct().toArray();
        }
        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            if (tag.getBoolean("s" + i) || tag.contains("sel_" + i)) {
                selected.add(i);
            }
        }
        return selected.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void copyTrendBuffer(int[] source, int[] target) {
        Arrays.fill(target, 0);
        if (source.length <= 0) {
            return;
        }
        int offset = Math.max(0, source.length - target.length);
        int length = Math.min(source.length, target.length);
        System.arraycopy(source, offset, target, target.length - length, length);
    }

    private static void pushTrend(int[] buffer, int value) {
        if (buffer.length <= 0) {
            return;
        }
        System.arraycopy(buffer, 1, buffer, 0, buffer.length - 1);
        buffer[buffer.length - 1] = value;
    }

    private boolean runAutoloaderCycle(RBMKFuelChannelEntity fuelChannel) {
        if (!hasAutoloaderInput() || !hasAutoloaderOutputSpace()) {
            return false;
        }
        boolean needsReload = fuelChannel.fuelStack().isEmpty();
        if (!needsReload && fuelChannel.burnTimeTotal() > 0) {
            needsReload = fuelChannel.burnTimeRemaining() * 100 < fuelChannel.burnTimeTotal() * autoloaderCycle;
        }
        if (!needsReload) {
            return false;
        }

        ItemStack extracted = ItemStack.EMPTY;
        if (!fuelChannel.fuelStack().isEmpty()) {
            extracted = fuelChannel.extractItem(0, 1, false);
            if (!extracted.isEmpty() && !storeAutoloaderOutput(extracted.copy())) {
                fuelChannel.insertItem(0, extracted, false);
                return false;
            }
        }

        int inputSlot = firstAutoloaderInputSlot();
        if (inputSlot < 0) {
            return !extracted.isEmpty();
        }

        ItemStack input = items.get(inputSlot);
        ItemStack moved = input.copyWithCount(1);
        ItemStack remainder = fuelChannel.insertItem(0, moved, false);
        if (!remainder.isEmpty()) {
            if (!extracted.isEmpty()) {
                removeAutoloaderOutput(extracted.copy());
                fuelChannel.insertItem(0, extracted, false);
            }
            return false;
        }

        input.shrink(1);
        items.set(inputSlot, input.isEmpty() ? ItemStack.EMPTY : input);
        setChanged();
        return true;
    }

    private boolean hasAutoloaderInput() {
        return firstAutoloaderInputSlot() >= 0;
    }

    private int firstAutoloaderInputSlot() {
        for (int i = 0; i < AUTOLOADER_INPUT_SLOTS; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemRBMKFuelRod) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasAutoloaderOutputSpace() {
        for (int i = AUTOLOADER_INPUT_SLOTS; i < AUTOLOADER_INPUT_SLOTS + AUTOLOADER_OUTPUT_SLOTS; i++) {
            if (items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean storeAutoloaderOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        for (int i = AUTOLOADER_INPUT_SLOTS; i < AUTOLOADER_INPUT_SLOTS + AUTOLOADER_OUTPUT_SLOTS; i++) {
            ItemStack existing = items.get(i);
            if (existing.isEmpty()) {
                items.set(i, stack);
                setChanged();
                return true;
            }
            if (ItemStack.isSameItemSameTags(existing, stack)) {
                int max = Math.min(existing.getMaxStackSize(), getMaxStackSize());
                int room = max - existing.getCount();
                if (room <= 0) {
                    continue;
                }
                int move = Math.min(room, stack.getCount());
                existing.grow(move);
                stack.shrink(move);
                items.set(i, existing);
                if (stack.isEmpty()) {
                    setChanged();
                    return true;
                }
            }
        }
        return stack.isEmpty();
    }

    private void removeAutoloaderOutput(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        for (int i = AUTOLOADER_INPUT_SLOTS; i < AUTOLOADER_INPUT_SLOTS + AUTOLOADER_OUTPUT_SLOTS; i++) {
            ItemStack existing = items.get(i);
            if (!ItemStack.isSameItemSameTags(existing, stack)) {
                continue;
            }
            int remove = Math.min(existing.getCount(), stack.getCount());
            existing.shrink(remove);
            stack.shrink(remove);
            items.set(i, existing.isEmpty() ? ItemStack.EMPTY : existing);
            if (stack.isEmpty()) {
                setChanged();
                return;
            }
        }
    }

    public static final class ConsoleColumn {
        private final RBMKColumnType type;
        private final CompoundTag data;

        private ConsoleColumn(RBMKColumnType type, CompoundTag data) {
            this.type = type;
            this.data = data;
        }

        public static ConsoleColumn capture(ServerLevel level, RBMKColumnState state) {
            RBMKColumnType normalizedType = RBMKColumns.determineColumnType(level, state.corePosition());
            CompoundTag data = new CompoundTag();
            data.putDouble("heat", state.heat());
            data.putDouble("maxHeat", state.maxHeat());
            if (state.moderated()) {
                data.putBoolean("moderated", true);
            }

            BlockEntity topEntity = level.getBlockEntity(state.corePosition().above());
            switch (normalizedType) {
                case FUEL, FUEL_SIM -> {
                    data.putDouble("enrichment", state.enrichment());
                    data.putDouble("xenon", state.xenon());
                    data.putDouble("c_heat", state.coreHeat());
                    data.putDouble("c_coreHeat", state.coreHeat());
                    data.putDouble("c_maxHeat", state.coreMaxHeat());
                    if (topEntity instanceof RBMKFuelChannelEntity fuel) {
                        ItemStack stack = fuel.fuelStack();
                        if (!stack.isEmpty()) {
                            data.putString("rodName", stack.getHoverName().getString());
                        }
                    }
                }
                case CONTROL, CONTROL_AUTO -> {
                    data.putDouble("level", state.controlRodInsertion());
                    data.putDouble("targetLevel", state.targetControlRodInsertion());
                    if (state.controlColor() >= 0) {
                        data.putInt("color", state.controlColor());
                    }
                    if (topEntity instanceof RBMKControlRodEntity rod) {
                        data.putString("function", rod.getAutoFunction().name());
                        data.putDouble("heatLower", rod.getHeatLower());
                        data.putDouble("heatUpper", rod.getHeatUpper());
                        data.putDouble("levelLower", rod.getLevelLower());
                        data.putDouble("levelUpper", rod.getLevelUpper());
                    }
                }
                case BOILER -> {
                    data.putInt("water", state.waterAmount());
                    data.putInt("maxWater", state.maxWater());
                    data.putInt("steam", state.steamAmount());
                    data.putInt("maxSteam", state.maxSteam());
                    data.putInt("compression", state.steamCompression());
                }
                case OUTGASSER -> {
                    if (topEntity instanceof RBMKOutgasserEntity outgasser) {
                        data.putDouble("progress", outgasser.progress());
                        data.putInt("requiredFlux", RBMKOutgasserEntity.REQUIRED_FLUX);
                        ItemStack input = outgasser.getItem(0);
                        if (!input.isEmpty()) {
                            data.putString("craftingName", input.getHoverName().getString());
                            data.putInt("craftingNumber", input.getCount());
                        }
                    }
                }
                case HEATEX -> {
                    if (topEntity instanceof RBMKHeaterEntity heater) {
                        data.putBoolean("active", heater.isActive());
                    }
                }
                default -> {
                }
            }
            return new ConsoleColumn(normalizedType, data);
        }

        public CompoundTag toTag(int index) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Index", index);
            tag.putInt("Type", type.ordinal());
            tag.put("Data", data.copy());
            return tag;
        }

        public static ConsoleColumn fromTag(CompoundTag tag) {
            int ordinal = Mth.clamp(tag.getInt("Type"), 0, RBMKColumnType.values().length - 1);
            return new ConsoleColumn(RBMKColumnType.values()[ordinal], tag.getCompound("Data"));
        }

        public RBMKColumnType type() {
            return type;
        }

        public CompoundTag data() {
            return data;
        }

        public List<Component> getFancyStats() {
            List<Component> stats = new ArrayList<>();
            stats.add(type.displayName());
            stats.add(Component.translatable("gui.hbm.rbmk.tooltip.heat",
                    String.format(Locale.ROOT, "%.1f", data.getDouble("heat")),
                    String.format(Locale.ROOT, "%.1f", data.getDouble("maxHeat"))));
            switch (type) {
                case FUEL, FUEL_SIM -> {
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.depletion",
                            String.format(Locale.ROOT, "%.1f", 100.0D - data.getDouble("enrichment") * 100.0D)));
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.xenon",
                            String.format(Locale.ROOT, "%.1f", data.getDouble("xenon"))));
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.core_temp",
                            String.format(Locale.ROOT, "%.1f", data.getDouble("c_coreHeat"))));
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.skin_temp",
                            String.format(Locale.ROOT, "%.1f", data.getDouble("c_heat")),
                            String.format(Locale.ROOT, "%.1f", data.getDouble("c_maxHeat"))));
                    if (data.contains("rodName")) {
                        stats.add(Component.translatable("gui.hbm.rbmk.tooltip.rod", data.getString("rodName")));
                    }
                }
                case CONTROL, CONTROL_AUTO -> {
                    if (data.contains("color")) {
                        stats.add(Component.translatable("gui.hbm.rbmk.tooltip.group", data.getInt("color") + 1));
                    }
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.level",
                            String.format(Locale.ROOT, "%.0f", data.getDouble("level") * 100.0D)));
                    if (type == RBMKColumnType.CONTROL_AUTO && data.contains("function")) {
                        stats.add(Component.translatable("gui.hbm.rbmk.tooltip.function", data.getString("function")));
                        stats.add(Component.translatable("gui.hbm.rbmk.tooltip.heat_range",
                                String.format(Locale.ROOT, "%.0f", data.getDouble("heatLower")),
                                String.format(Locale.ROOT, "%.0f", data.getDouble("heatUpper"))));
                    }
                }
                case BOILER -> {
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.water", data.getInt("water"), data.getInt("maxWater")));
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.steam", data.getInt("steam"), data.getInt("maxSteam")));
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.compressor_level", data.getInt("compression")));
                }
                case OUTGASSER -> {
                    stats.add(Component.translatable("gui.hbm.rbmk.tooltip.progress",
                            String.format(Locale.ROOT, "%.0f", data.getDouble("progress")), data.getInt("requiredFlux")));
                    if (data.contains("craftingName")) {
                        stats.add(Component.translatable("gui.hbm.rbmk.tooltip.input",
                                data.getString("craftingName"), data.getInt("craftingNumber")));
                    }
                }
                case HEATEX -> stats.add(Component.translatable("gui.hbm.rbmk.tooltip.active", data.getBoolean("active")));
                default -> {
                }
            }
            if (data.getBoolean("moderated")) {
                stats.add(Component.translatable("gui.hbm.rbmk.tooltip.moderated"));
            }
            return stats;
        }
    }

    public static final class ConsoleScreen {
        private RBMKScreenType type = RBMKScreenType.NONE;
        private int[] columns = new int[0];
        private String display;

        public CompoundTag toTag(int slot) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Slot", slot);
            tag.putInt("Type", type.ordinal());
            tag.putIntArray("Columns", columns);
            if (display != null && !display.isEmpty()) {
                tag.putString("Display", display);
            }
            return tag;
        }

        public static ConsoleScreen fromTag(CompoundTag tag) {
            ConsoleScreen screen = new ConsoleScreen();
            int ordinal = Mth.clamp(tag.getInt("Type"), 0, RBMKScreenType.values().length - 1);
            screen.type = RBMKScreenType.values()[ordinal];
            screen.columns = tag.getIntArray("Columns");
            if (tag.contains("Display")) {
                screen.display = tag.getString("Display");
            }
            return screen;
        }

        public RBMKScreenType type() {
            return type;
        }

        public int[] columns() {
            return columns;
        }

        @Nullable
        public String display() {
            return display;
        }
    }
}
