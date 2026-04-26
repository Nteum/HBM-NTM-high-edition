package com.hbm.blockentity.machine.rbmk;

import com.hbm.HBM;
import com.hbm.api.Mode;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Minimal fuel channel block entity. Stores a single RBMK fuel rod and injects
 * heat into the column below while burning.
 */
public class RBMKFuelChannelEntity extends BaseMachineBlockEntity {

    private static final int FUEL_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final double TIME_STEP = 1.0D / 20.0D;
    private static final int REDSTONE_SIGNAL_ON = 15;
    private static final String TAG_BURN_TIME_REMAINING = "BurnTimeRemaining";
    private static final String TAG_BURN_TIME_TOTAL = "BurnTimeTotal";

    private int burnTimeRemaining;
    private int burnTimeTotal;
    private int lastRedstoneSignal = -1;
    private int lastComparatorSignal = -1;
    private final ContainerData containerData = new SimpleContainerData(12);
    private final int[] dataSlots = new int[12];

    public RBMKFuelChannelEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_FUEL_CHANNEL_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder().addMode(Mode.INPUT).addMode(Mode.OUTPUT).get();
    }

    public boolean isBurning() {
        return burnTimeRemaining > 0 && items.get(FUEL_SLOT).getItem() instanceof ItemRBMKFuelRod;
    }

    public int burnTimeRemaining() {
        return burnTimeRemaining;
    }

    public int burnTimeTotal() {
        return burnTimeTotal;
    }

    public ItemStack fuelStack() {
        return items.get(FUEL_SLOT);
    }

    public ItemStack spentFuelStack() {
        return items.get(OUTPUT_SLOT);
    }

    public int redstoneSignal() {
        return isBurning() ? REDSTONE_SIGNAL_ON : 0;
    }

    public int comparatorSignal() {
        return calculateComparatorSignal();
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final BlockPos corePos = worldPosition.below();
        final RBMKLevelContext context = RBMKManager.context(serverLevel);
        final Optional<RBMKColumnState> column = context.column(corePos);
        final RBMKBaseEntity baseEntity = level.getBlockEntity(corePos) instanceof RBMKBaseEntity base ? base : null;
        double reportedHeatPerSecond = 0.0D;

        ItemStack fuelStack = items.get(FUEL_SLOT);
        if (fuelStack.isEmpty() || !(fuelStack.getItem() instanceof ItemRBMKFuelRod fuelRod)) {
            if (burnTimeRemaining != 0 || burnTimeTotal != 0) {
                burnTimeRemaining = 0;
                burnTimeTotal = 0;
                setChanged();
            }
            updateSignalLevels();
            updateClientData(column.orElse(null), baseEntity, context, reportedHeatPerSecond);
            return;
        }

        if (burnTimeRemaining <= 0) {
            burnTimeTotal = loadTotalTimeFromFuel(fuelStack, fuelRod);
            burnTimeRemaining = loadRemainingTimeFromFuel(fuelStack, fuelRod, burnTimeTotal);
            setChanged();
        }

        if (column.isPresent()) {
            RBMKColumnState state = column.get();
            double controlRod = context.controlRodAverage();
            double heatPerSecond = fuelRod.heatPerSecond() * state.settings().reactivityModifier() * (1.0D - controlRod);
            if (heatPerSecond > 0.0D) {
                reportedHeatPerSecond = heatPerSecond;
                context.addHeat(corePos, heatPerSecond * TIME_STEP);
            }
        }

        if (burnTimeRemaining > 0) {
            burnTimeRemaining--;
            setChanged();
        }
        if (burnTimeRemaining == 0 && burnTimeTotal > 0) {
            ItemStack spentFuel = ModItems.rbmk_fuel_empty.get().getDefaultInstance();
            if (!insertIntoOutput(spentFuel.copy())) {
                Containers.dropItemStack(serverLevel, worldPosition.getX() + 0.5D, worldPosition.getY() + 1.0D, worldPosition.getZ() + 0.5D, spentFuel.copy());
            }
            items.set(FUEL_SLOT, ItemStack.EMPTY);
            burnTimeTotal = 0;
            setChanged();
            HBM.LOGGER.debug("RBMK fuel channel at {} finished burn cycle.", worldPosition);
        }

        updateSignalLevels();
        updateClientData(column.orElse(null), baseEntity, context, reportedHeatPerSecond);
    }

    public boolean tryInsertFuel(final Player player, final InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof ItemRBMKFuelRod)) {
            return false;
        }
        if (!items.get(FUEL_SLOT).isEmpty()) {
            return false;
        }
        ItemStack output = items.get(OUTPUT_SLOT);
        ItemStack spentPrototype = ModItems.rbmk_fuel_empty.get().getDefaultInstance();
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(output, spentPrototype)) {
                return false;
            }
            int max = Math.min(output.getMaxStackSize(), this.getMaxStackSize());
            if (output.getCount() >= max) {
                return false;
            }
        }
        ItemStack toInsert = held.copyWithCount(1);
        items.set(FUEL_SLOT, toInsert);
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        burnTimeRemaining = 0;
        burnTimeTotal = 0;
        setChanged();
        updateSignalLevels();
        return true;
    }

    public void tryExtractFuel(final Player player) {
        ItemStack output = items.get(OUTPUT_SLOT);
        if (!output.isEmpty()) {
            items.set(OUTPUT_SLOT, ItemStack.EMPTY);
            setChanged();
            ItemStack toDrop = output.copy();
            if (!player.addItem(toDrop)) {
                Containers.dropItemStack(player.level(), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, toDrop);
            }
            updateSignalLevels();
            return;
        }

        ItemStack input = items.get(FUEL_SLOT);
        if (input.isEmpty()) {
            return;
        }
        writeBurnProgressToFuel(input);
        items.set(FUEL_SLOT, ItemStack.EMPTY);
        burnTimeRemaining = 0;
        burnTimeTotal = 0;
        setChanged();

        ItemStack toDrop = input.copy();
        if (!player.addItem(toDrop)) {
            Containers.dropItemStack(player.level(), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, toDrop);
        }
        updateSignalLevels();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTimeRemaining = tag.getInt(TAG_BURN_TIME_REMAINING);
        burnTimeTotal = tag.getInt(TAG_BURN_TIME_TOTAL);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(TAG_BURN_TIME_REMAINING, burnTimeRemaining);
        tag.putInt(TAG_BURN_TIME_TOTAL, burnTimeTotal);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rbmk_fuel_channel");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new com.hbm.gui.menu.RBMKFuelChannelMenu(containerId, inventory, this, getContainerData());
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public void prepareForDrop() {
        ItemStack input = items.get(FUEL_SLOT);
        if (!input.isEmpty()) {
            writeBurnProgressToFuel(input);
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot != FUEL_SLOT) {
            return stack;
        }
        if (!(stack.getItem() instanceof ItemRBMKFuelRod)) {
            return stack;
        }
        if (!items.get(FUEL_SLOT).isEmpty()) {
            return stack;
        }

        int toMove = Math.min(1, stack.getCount());
        if (!simulate) {
            items.set(FUEL_SLOT, stack.copyWithCount(toMove));
            burnTimeRemaining = 0;
            burnTimeTotal = 0;
            setChanged();
            updateSignalLevels();
        }

        return stack.getCount() == toMove ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - toMove);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        if (slot == OUTPUT_SLOT) {
            ItemStack existing = items.get(OUTPUT_SLOT);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            }
            int toExtract = Math.min(amount, existing.getCount());
            ItemStack result = existing.copyWithCount(toExtract);
            if (!simulate) {
                existing.shrink(toExtract);
                items.set(OUTPUT_SLOT, existing.isEmpty() ? ItemStack.EMPTY : existing);
                setChanged();
                updateSignalLevels();
            }
            return result;
        }

        if (slot != FUEL_SLOT) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = items.get(FUEL_SLOT);
        if (!(existing.getItem() instanceof ItemRBMKFuelRod)) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(toExtract);
        writeBurnProgressToFuel(result);

        if (!simulate) {
            items.set(FUEL_SLOT, ItemStack.EMPTY);
            burnTimeRemaining = 0;
            burnTimeTotal = 0;
            setChanged();
            updateSignalLevels();
        }

        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable net.minecraft.core.Direction side) {
        if (index != FUEL_SLOT) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemRBMKFuelRod)) {
            return false;
        }
        return super.canPlaceItemThroughFace(index, stack, side);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, net.minecraft.core.Direction side) {
        return index == OUTPUT_SLOT;
    }

    private void updateSignalLevels() {
        if (level == null) {
            return;
        }

        final int redstoneSignal = redstoneSignal();
        final int comparatorSignal = calculateComparatorSignal();

        if (redstoneSignal != lastRedstoneSignal) {
            lastRedstoneSignal = redstoneSignal;
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }

        if (comparatorSignal != lastComparatorSignal) {
            lastComparatorSignal = comparatorSignal;
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    private int calculateComparatorSignal() {
        ItemStack stack = items.get(FUEL_SLOT);
        if (!(stack.getItem() instanceof ItemRBMKFuelRod)) {
            return 0;
        }
        if (burnTimeTotal <= 0) {
            return 0;
        }
        double fraction = burnTimeRemaining / (double) burnTimeTotal;
        if (fraction <= 0.0D) {
            return 0;
        }
        if (fraction >= 1.0D) {
            return REDSTONE_SIGNAL_ON;
        }
        return Math.min(REDSTONE_SIGNAL_ON, Math.max(0, (int) Math.ceil(fraction * REDSTONE_SIGNAL_ON)));
    }

    private void updateClientData(@Nullable RBMKColumnState column, @Nullable RBMKBaseEntity base,
                                  RBMKLevelContext context, double heatPerSecond) {
        dataSlots[0] = column != null ? (int) Math.round(column.heat() * 10.0D) : 0;
        dataSlots[1] = column != null ? (int) Math.round(column.settings().meltdownHeat() * 10.0D) : 0;
        dataSlots[2] = base != null ? (int) Math.min(Integer.MAX_VALUE, base.getEnergyStored()) : 0;
        dataSlots[3] = base != null ? (int) Math.min(Integer.MAX_VALUE, base.getEnergyCapacity()) : 0;
        dataSlots[4] = base != null ? base.getWaterAmount() : 0;
        dataSlots[5] = base != null ? base.getSteamAmount() : 0;
        dataSlots[6] = burnTimeRemaining;
        dataSlots[7] = burnTimeTotal;
        dataSlots[8] = isBurning() ? 1 : 0;
        dataSlots[9] = (int) Math.round(Math.max(0.0D, heatPerSecond) * 10.0D);
        dataSlots[10] = column != null ? (int) Math.round(column.controlRodInsertion() * 100.0D) : 0;
        dataSlots[11] = (int) Math.round(context.controlRodAverage() * 100.0D);

        for (int i = 0; i < dataSlots.length; i++) {
            containerData.set(i, dataSlots[i]);
        }
    }

    private boolean insertIntoOutput(final ItemStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        ItemStack existing = items.get(OUTPUT_SLOT);
        if (existing.isEmpty()) {
            items.set(OUTPUT_SLOT, stack);
            return true;
        }
        if (!ItemStack.isSameItemSameTags(existing, stack)) {
            return false;
        }
        int max = Math.min(existing.getMaxStackSize(), this.getMaxStackSize());
        int space = max - existing.getCount();
        if (space <= 0) {
            return false;
        }
        int toAdd = Math.min(space, stack.getCount());
        existing.grow(toAdd);
        stack.shrink(toAdd);
        items.set(OUTPUT_SLOT, existing);
        return stack.isEmpty();
    }

    private void writeBurnProgressToFuel(final ItemStack stack) {
        if (!(stack.getItem() instanceof ItemRBMKFuelRod fuelRod)) {
            return;
        }
        if (burnTimeTotal <= 0) {
            return;
        }
        int total = Math.min(burnTimeTotal, fuelRod.burnTimeTicks());
        int remaining = Math.min(burnTimeRemaining, total);
        stack.getOrCreateTag().putInt(TAG_BURN_TIME_TOTAL, total);
        stack.getOrCreateTag().putInt(TAG_BURN_TIME_REMAINING, remaining);
    }

    private int loadTotalTimeFromFuel(final ItemStack stack, final ItemRBMKFuelRod fuelRod) {
        int configuredTotal = fuelRod.burnTimeTicks();
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return configuredTotal;
        }
        int stored = tag.getInt(TAG_BURN_TIME_TOTAL);
        if (stored <= 0) {
            return configuredTotal;
        }
        return Math.min(stored, configuredTotal);
    }

    private int loadRemainingTimeFromFuel(final ItemStack stack, final ItemRBMKFuelRod fuelRod, final int burnTimeTotal) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return burnTimeTotal;
        }
        int stored = tag.getInt(TAG_BURN_TIME_REMAINING);
        if (stored <= 0) {
            return burnTimeTotal;
        }
        return Math.min(stored, burnTimeTotal);
    }
}
