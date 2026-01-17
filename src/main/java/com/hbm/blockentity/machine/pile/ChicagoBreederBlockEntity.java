package com.hbm.blockentity.machine.pile;

import com.hbm.block.machine.pile.ChicagoPileStateProperties;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.item.research.ItemPileRod;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Breeder channel that slowly converts a lithium rod into a tritium cell.
 */
public class ChicagoBreederBlockEntity extends ChicagoPileBlockEntity {

    private static final String TAG_ROD = "Rod";
    private static final String TAG_PROGRESS = "Progress";
    private static final String TAG_NEUTRONS = "Neutrons";

    private ItemStack rod = ItemStack.EMPTY;
    private int progress;
    private int neutrons;
    private int lastNeutrons;

    public ChicagoBreederBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CHICAGO_BREEDER.get(), pos, state);
    }

    public void setBaseItem(ItemStack stack) {
        rod = stack.copy();
        rod.setCount(1);
        if (rod.getItem() instanceof ItemPileRod rodItem) {
            progress = rodItem.getLifetime(rod);
        } else {
            progress = 0;
        }
        neutrons = 0;
        lastNeutrons = 0;
        setChanged();
    }

    public ItemStack extractRod() {
        if (rod.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = rod.copy();
        rod = ItemStack.EMPTY;
        progress = 0;
        neutrons = 0;
        lastNeutrons = 0;
        setChanged();
        return result;
    }

    public ItemStack getRodSnapshot() {
        return rod.copy();
    }

    public boolean hasRod() {
        return !rod.isEmpty();
    }

    public int getMaxProgress() {
        if (rod.getItem() instanceof ItemPileRod rodItem) {
            return rodItem.getSpec().maxLife();
        }
        return 0;
    }

    @Override
    protected void serverTick() {
        if (level == null) {
            return;
        }
        if (rod.isEmpty()) {
            neutrons = 0;
            return;
        }

        Item item = rod.getItem();
        if (!(item instanceof ItemPileRod rodItem)) {
            rod = ItemStack.EMPTY;
            neutrons = 0;
            return;
        }

        lastNeutrons = neutrons;
        if (lastNeutrons > 0) {
            rodItem.addLifetime(rod, lastNeutrons);
            progress = rodItem.getLifetime(rod);
            emitNeutrons(1, 2);
            setChanged();
        }
        neutrons = 0;

        int max = rodItem.getSpec().maxLife();
        if (max > 0 && progress >= max) {
            finishBreeding();
        }
    }

    private void finishBreeding() {
        if (level == null) {
            return;
        }
        BlockState current = getBlockState();
        BlockState replacement = ModBlocks.chicago_graphite_tritium.get().defaultBlockState();
        if (current.hasProperty(RotatedPillarBlock.AXIS)) {
            replacement = replacement.setValue(RotatedPillarBlock.AXIS, current.getValue(RotatedPillarBlock.AXIS));
        }
        if (current.hasProperty(ChicagoPileStateProperties.SHIELDED)) {
            replacement = replacement.setValue(ChicagoPileStateProperties.SHIELDED, current.getValue(ChicagoPileStateProperties.SHIELDED));
        }
        level.setBlock(worldPosition, replacement, 3);
        rod = ItemStack.EMPTY;
        progress = 0;
        lastNeutrons = 0;
        neutrons = 0;
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
        tag.putInt(TAG_PROGRESS, progress);
        tag.putInt(TAG_NEUTRONS, neutrons);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        rod = tag.contains(TAG_ROD) ? ItemStack.of(tag.getCompound(TAG_ROD)) : ItemStack.EMPTY;
        progress = tag.getInt(TAG_PROGRESS);
        neutrons = tag.getInt(TAG_NEUTRONS);
    }

    public int getLastNeutrons() {
        return lastNeutrons;
    }

    public int getProgress() {
        return progress;
    }
}
