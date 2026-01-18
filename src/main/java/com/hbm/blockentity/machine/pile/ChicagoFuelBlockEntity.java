package com.hbm.blockentity.machine.pile;

import com.hbm.item.research.ItemPileRod;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;

public class ChicagoFuelBlockEntity extends ChicagoPileBlockEntity {

    private static final double MELTDOWN_HEAT = 1000.0D;

    private FuelVariant variant;
    private int rodLife;
    private int incomingNeutrons;
    private double heat;

    public ChicagoFuelBlockEntity(BlockPos pos, BlockState state) {
        super(com.hbm.blockentity.ModBlockEntityType.CHICAGO_FUEL.get(), pos, state);
    }

    public void loadFromItem(FuelVariant variant, ItemStack stack) {
        this.variant = variant;
        if (stack.getItem() instanceof ItemPileRod rod) {
            this.rodLife = rod.getLifetime(stack);
        } else {
            this.rodLife = 0;
        }
        this.heat = 0.0D;
        this.incomingNeutrons = 0;
        setChanged();
    }

    public ItemStack extractRod() {
        if (variant == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(variant.item.get());
        if (stack.getItem() instanceof ItemPileRod rod) {
            rod.addLifetime(stack, rodLife);
        }
        variant = null;
        rodLife = 0;
        heat = 0.0D;
        incomingNeutrons = 0;
        setChanged();
        return stack;
    }

    @Override
    public void receiveNeutrons(int amount) {
        incomingNeutrons += amount;
    }

    @Override
    protected void serverTick() {
        if (variant == null || level == null) {
            incomingNeutrons = 0;
            return;
        }
        dissipateHeat();
        int reaction = (int) (incomingNeutrons * reactivityModifier());
        incomingNeutrons = 0;
        if (reaction <= 0) {
            return;
        }
        heat += reaction * variant.heatFactor;
        rodLife += reaction;
        emitNeutrons(Math.max(1, (int) Math.round(reaction * variant.fluxFactor)), 12);
        setChanged();
        if (heat >= MELTDOWN_HEAT) {
            meltdown();
            return;
        }
        if (rodLife >= variant.maxLife) {
            turnIntoPlutonium();
        }
    }

    private double reactivityModifier() {
        double clamp = Math.min(heat / MELTDOWN_HEAT, 1.0D);
        return Math.max(0.0D, 1.0D - clamp * 0.5D);
    }

    private void dissipateHeat() {
        heat *= 0.95D;
        if (heat < 0.01D) {
            heat = 0.0D;
        }
    }

    private void meltdown() {
        if (level == null) {
            return;
        }
        level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D, 4.0F, Level.ExplosionInteraction.BLOCK);
        level.removeBlock(worldPosition, false);
    }

    private void turnIntoPlutonium() {
        if (level == null) {
            return;
        }
        BlockState replacement = ModBlocks.chicago_graphite_source.get().defaultBlockState();
        replacement = replacement.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS, getBlockState().getValue(net.minecraft.world.level.block.RotatedPillarBlock.AXIS));
        replacement = replacement.setValue(com.hbm.block.machine.pile.ChicagoPileStateProperties.SHIELDED, getBlockState().getValue(com.hbm.block.machine.pile.ChicagoPileStateProperties.SHIELDED));
        level.setBlock(worldPosition, replacement, 3);
        BlockEntity be = level.getBlockEntity(worldPosition);
        if (be instanceof ChicagoSourceBlockEntity source) {
            source.setType(ChicagoSourceBlockEntity.SourceType.PLUTONIUM);
        }
    }

    public FuelVariant getVariant() {
        return variant;
    }

    public int getRodLife() {
        return rodLife;
    }

    public double getHeatLevel() {
        return heat;
    }

    public double getMeltdownHeat() {
        return MELTDOWN_HEAT;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (variant != null) {
            tag.putString("Variant", variant.name());
            tag.putInt("RodLife", rodLife);
            tag.putDouble("Heat", heat);
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Variant")) {
            try {
                variant = FuelVariant.valueOf(tag.getString("Variant").toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                variant = FuelVariant.URANIUM;
            }
            rodLife = tag.getInt("RodLife");
            heat = tag.getDouble("Heat");
        } else {
            variant = null;
            rodLife = 0;
            heat = 0.0D;
        }
    }

    public enum FuelVariant {
        URANIUM(ModItems.PILE_ROD_URANIUM, 50000, 0.8D, 0.25D),
        PU239(ModItems.PILE_ROD_PU239, 40000, 1.0D, 0.3D);

        public final java.util.function.Supplier<? extends Item> item;
        public final int maxLife;
        public final double fluxFactor;
        public final double heatFactor;

        FuelVariant(java.util.function.Supplier<? extends Item> item, int maxLife, double fluxFactor, double heatFactor) {
            this.item = item;
            this.maxLife = maxLife;
            this.fluxFactor = fluxFactor;
            this.heatFactor = heatFactor;
        }
    }
}
