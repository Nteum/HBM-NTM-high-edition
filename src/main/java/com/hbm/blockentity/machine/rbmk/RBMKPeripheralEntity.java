package com.hbm.blockentity.machine.rbmk;

import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.block.machine.rbmk.BlockRBMKPeripheral;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKPeripheralType;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
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
public class RBMKPeripheralEntity extends BaseMachineBlockEntity {

    private static final int SEARCH_INTERVAL_TICKS = 40;
    private static final int DATA_SLOTS = 10;

    private final RBMKPeripheralType peripheralType;
    private final ContainerData containerData = new SimpleContainerData(DATA_SLOTS);
    private final int[] dataBacking = new int[DATA_SLOTS];

    private BlockPos linkedColumn;
    private int tickCounter;

    public RBMKPeripheralEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_PERIPHERAL_ENTITY.get(), pos, state);
        this.items = NonNullList.create();
        this.slotModes = java.util.List.of();
        this.peripheralType = state.getBlock() instanceof BlockRBMKPeripheral block
                ? block.getPeripheralType() : RBMKPeripheralType.CONSOLE;
    }

    @Override
    protected void onUpdateServer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        // embed the type ordinal for the client screen
        dataBacking[9] = peripheralType.ordinal();

        if (++tickCounter % SEARCH_INTERVAL_TICKS == 0 || !isLinkedColumnValid()) {
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

        pushData();
    }

    private void pushData() {
        for (int i = 0; i < dataBacking.length; i++) {
            containerData.set(i, dataBacking[i]);
        }
    }

    private void clearTelemetry() {
        for (int i = 0; i < dataBacking.length - 1; i++) {
            dataBacking[i] = 0;
        }
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

    @Override
    public Component getDefaultName() {
        return peripheralType.displayName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new com.hbm.gui.menu.RBMKPeripheralMenu(containerId, inventory, this, getContainerData());
    }
}
