package com.hbm.blockentity.machine.pile;

import com.hbm.block.machine.pile.ChicagoPileStateProperties;
import com.hbm.blockentity.ModBlockEntityType;

import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Detector channel that counts incoming neutrons and toggles its state once the
 * configured threshold is exceeded.
 */
public class ChicagoDetectorBlockEntity extends ChicagoPileBlockEntity {

    private static final String TAG_ROD = "Rod";
    private static final String TAG_THRESHOLD = "Threshold";
    private static final String TAG_NEUTRONS = "Neutrons";

    private static final int MIN_THRESHOLD = 1;
    private static final int MAX_THRESHOLD = 64;
    private static final int DEFAULT_THRESHOLD = 10;

    private ItemStack rod = ItemStack.EMPTY;
    private int threshold = DEFAULT_THRESHOLD;
    private int neutrons;

    public ChicagoDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CHICAGO_DETECTOR.get(), pos, state);
    }

    public void insertRod(ItemStack stack) {
        rod = stack.copy();
        rod.setCount(1);
        threshold = DEFAULT_THRESHOLD;
        neutrons = 0;
        setTriggered(false);
        setChanged();
    }

    public ItemStack extractRod() {
        if (rod.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = rod.copy();
        rod = ItemStack.EMPTY;
        neutrons = 0;
        setTriggered(false);
        setChanged();
        return stack;
    }

    public int adjustThreshold(int delta) {
        threshold = Mth.clamp(threshold + delta, MIN_THRESHOLD, MAX_THRESHOLD);
        setChanged();
        return threshold;
    }

    public boolean hasRod() {
        return !rod.isEmpty();
    }

    public ItemStack getRodSnapshot() {
        return rod.copy();
    }

    public int getThresholdValue() {
        return threshold;
    }

    public Component thresholdMessage(int value) {
        return Component.translatable("message.hbm.chicago_detector.threshold", value);
    }

    public void reset() {
        insertRod(new ItemStack(ModItems.PILE_ROD_DETECTOR.get()));
    }

    @Override
    protected void serverTick() {
        if (level == null) {
            return;
        }
        if (rod.isEmpty()) {
            neutrons = 0;
            setTriggered(false);
            return;
        }

        boolean active = neutrons >= threshold;
        neutrons = 0;
        setTriggered(active);
    }

    private void setTriggered(boolean active) {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (!state.hasProperty(ChicagoPileStateProperties.TRIGGERED)) {
            return;
        }
        if (state.getValue(ChicagoPileStateProperties.TRIGGERED) == active) {
            return;
        }
        level.setBlock(worldPosition, state.setValue(ChicagoPileStateProperties.TRIGGERED, active), 3);
        level.updateNeighborsAt(worldPosition, state.getBlock());
    }

    @Override
    public void receiveNeutrons(int amount) {
        neutrons += amount;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (!rod.isEmpty()) {
            tag.put(TAG_ROD, rod.save(new CompoundTag()));
        }
        tag.putInt(TAG_THRESHOLD, threshold);
        tag.putInt(TAG_NEUTRONS, neutrons);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        rod = tag.contains(TAG_ROD) ? ItemStack.of(tag.getCompound(TAG_ROD)) : ItemStack.EMPTY;
        threshold = tag.contains(TAG_THRESHOLD) ? tag.getInt(TAG_THRESHOLD) : DEFAULT_THRESHOLD;
        neutrons = tag.getInt(TAG_NEUTRONS);
    }
}
