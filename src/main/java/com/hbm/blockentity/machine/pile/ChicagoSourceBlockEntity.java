package com.hbm.blockentity.machine.pile;

import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Emits neutrons at a fixed rate depending on the inserted source rod.
 */
public class ChicagoSourceBlockEntity extends ChicagoPileBlockEntity {

    private SourceType type = SourceType.SOURCE;

    public ChicagoSourceBlockEntity(BlockPos pos, BlockState state) {
        super(com.hbm.blockentity.ModBlockEntityType.CHICAGO_SOURCE.get(), pos, state);
    }

    public void setType(SourceType type) {
        this.type = type;
        setChanged();
    }

    public SourceType getSourceType() {
        return type;
    }

    public ItemStack extractRod() {
        if (type == null) {
            return ItemStack.EMPTY;
        }
        Item item = type.rod().get();
        if (item == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        type = null;
        setChanged();
        return stack;
    }

    @Override
    protected void serverTick() {
        if (type == null) {
            return;
        }
        emitNeutrons(type.fluxPerStream(), type.streams());
    }

    @Override
    public void receiveNeutrons(int amount) {
        // source rods emit but do not react to external neutrons
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (type != null) {
            tag.putString("Type", type.name());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Type")) {
            try {
                type = SourceType.valueOf(tag.getString("Type").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                type = SourceType.SOURCE;
            }
        } else {
            type = SourceType.SOURCE;
        }
    }

    public enum SourceType {
        SOURCE(ModItems.PILE_ROD_SOURCE, 1, 12),
        PLUTONIUM(ModItems.PILE_ROD_PLUTONIUM, 2, 16);

        private final Supplier<? extends Item> rod;
        private final int fluxPerStream;
        private final int streams;

        SourceType(Supplier<? extends Item> rod, int fluxPerStream, int streams) {
            this.rod = rod;
            this.fluxPerStream = fluxPerStream;
            this.streams = streams;
        }

        public Supplier<? extends Item> rod() {
            return rod;
        }

        public int fluxPerStream() {
            return fluxPerStream;
        }

        public int streams() {
            return streams;
        }
    }
}
