package com.hbm.reactor.rbmk;

import com.hbm.blockentity.machine.rbmk.RBMKBaseEntity;
import com.hbm.blockentity.machine.rbmk.RBMKBoilerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKControlRodEntity;
import com.hbm.blockentity.machine.rbmk.RBMKCoolerEntity;
import com.hbm.blockentity.machine.rbmk.RBMKFuelChannelEntity;
import com.hbm.blockentity.machine.rbmk.RBMKHeaterEntity;
import com.hbm.blockentity.machine.rbmk.RBMKOutgasserEntity;
import com.hbm.item.rbmk.ItemRBMKFuelRod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Shared RBMK column inspection helpers. The server simulation and the client
 * DODD overlay both need the same "what kind of column is this and what does
 * it currently contain?" view, so keep it in one place.
 */
public final class RBMKColumns {

    private RBMKColumns() {
    }

    public static RBMKColumnType determineColumnType(final Level level, final BlockPos corePos) {
        final Block selfBlock = level.getBlockState(corePos).getBlock();
        final RBMKColumnType selfType = classifyBlockPath(BuiltInRegistries.BLOCK.getKey(selfBlock).getPath());
        if (selfType != RBMKColumnType.BLANK) {
            return selfType;
        }

        final BlockPos topPos = corePos.above();
        final BlockEntity topEntity = level.getBlockEntity(topPos);

        if (topEntity instanceof RBMKFuelChannelEntity) {
            return RBMKColumnType.FUEL;
        }
        if (topEntity instanceof RBMKControlRodEntity controlRod) {
            return controlRod.isAutoColumn() ? RBMKColumnType.CONTROL_AUTO : RBMKColumnType.CONTROL;
        }
        if (topEntity instanceof RBMKBoilerEntity) {
            return RBMKColumnType.BOILER;
        }
        if (topEntity instanceof RBMKOutgasserEntity) {
            return RBMKColumnType.OUTGASSER;
        }
        if (topEntity instanceof com.hbm.blockentity.machine.rbmk.RBMKStorageEntity) {
            return RBMKColumnType.STORAGE;
        }
        if (topEntity instanceof RBMKCoolerEntity) {
            return RBMKColumnType.COOLER;
        }
        if (topEntity instanceof RBMKHeaterEntity) {
            return RBMKColumnType.HEATEX;
        }

        final Block topBlock = level.getBlockState(topPos).getBlock();
        return classifyBlockPath(BuiltInRegistries.BLOCK.getKey(topBlock).getPath());
    }

    public static RBMKColumnType classifyBlockPath(final String path) {
        if (path == null || !path.contains("rbmk")) {
            return RBMKColumnType.BLANK;
        }
        if (path.contains("control_auto")) {
            return RBMKColumnType.CONTROL_AUTO;
        }
        if (path.contains("control_rod") || path.contains("control_mod")
                || (path.contains("control") && !path.contains("console"))) {
            return RBMKColumnType.CONTROL;
        }
        if (path.contains("fuel_channel")
                || path.contains("rbmk_rod")
                || path.contains("fuel_")) {
            return RBMKColumnType.FUEL;
        }
        if (path.contains("moderator") || path.contains("element")) {
            return RBMKColumnType.MODERATOR;
        }
        if (path.contains("absorber")) {
            return RBMKColumnType.ABSORBER;
        }
        if (path.contains("reflector")) {
            return RBMKColumnType.REFLECTOR;
        }
        if (path.contains("boiler")) {
            return RBMKColumnType.BOILER;
        }
        if (path.contains("outgasser")) {
            return RBMKColumnType.OUTGASSER;
        }
        if (path.contains("storage")) {
            return RBMKColumnType.STORAGE;
        }
        if (path.contains("cooler")) {
            return RBMKColumnType.COOLER;
        }
        if (path.contains("heatex") || path.contains("heater")) {
            return RBMKColumnType.HEATEX;
        }
        if (path.contains("breeder")) {
            return RBMKColumnType.BREEDER;
        }
        return RBMKColumnType.BLANK;
    }

    public static void populateState(final Level level, final RBMKColumnState state) {
        final BlockPos corePos = state.corePosition();
        state.setColumnType(determineColumnType(level, corePos));
        state.resetFlux();
        state.setHasRod(false);
        state.setModerated(false);
        state.setControlColor(-1);
        state.setTargetControlRodInsertion(state.controlRodInsertion());
        state.setEnrichment(1.0D);
        state.setXenon(0.0D);
        state.setCoreHeat(state.heat());
        state.setCoreMaxHeat(Math.max(1_500.0D, state.maxHeat()));
        state.setFluidBuffer(0, 0, 0, 0);
        state.setSteamCompression(0);
        state.setMaxHeat(state.settings().meltdownHeat());

        if (level.getBlockEntity(corePos) instanceof RBMKBaseEntity baseEntity) {
            state.setLidType(baseEntity.getLidType());
            state.setFluidBuffer(baseEntity.getWaterAmount(), baseEntity.getWaterCapacity(),
                    baseEntity.getSteamAmount(), baseEntity.getSteamCapacity());
        }

        final BlockEntity topEntity = level.getBlockEntity(corePos.above());
        if (topEntity instanceof RBMKFuelChannelEntity fuelChannel) {
            populateFuelState(state, fuelChannel);
        } else if (topEntity instanceof RBMKControlRodEntity controlRod) {
            state.setControlRodInsertion(controlRod.getInsertionFraction());
            state.setTargetControlRodInsertion(controlRod.getTargetInsertionFraction());
            state.setControlColor(controlRod.getSelectedColorIndex());
        } else if (topEntity instanceof RBMKBoilerEntity boiler) {
            state.setSteamCompression(boiler.compressionStage());
        } else if (topEntity instanceof RBMKHeaterEntity heater) {
            state.setHasRod(heater.isActive());
        }

        state.setModerated(hasAdjacentType(level, corePos, RBMKColumnType.MODERATOR));
    }

    private static void populateFuelState(final RBMKColumnState state, final RBMKFuelChannelEntity fuelChannel) {
        final ItemStack stack = fuelChannel.fuelStack();
        if (!(stack.getItem() instanceof ItemRBMKFuelRod fuelRod)) {
            return;
        }

        state.setHasRod(true);
        final int total = Math.max(1, fuelChannel.burnTimeTotal());
        final int remaining = Math.max(0, fuelChannel.burnTimeRemaining());
        final double depletion = 1.0D - (remaining / (double) total);
        final double insertionFactor = Math.max(0.0D, 1.0D - state.controlRodInsertion());

        state.setEnrichment(remaining / (double) total);
        state.setXenon(depletion * 100.0D);
        state.setCoreHeat(Math.max(state.heat(), fuelRod.heatPerSecond() * 4.0D));
        state.setCoreMaxHeat(fuelRod.coreMaxHeat());
        state.setMaxHeat(Math.max(state.settings().meltdownHeat(), fuelRod.coreMaxHeat()));
        state.addFastFlux(fuelRod.fastFluxPerSecond() * insertionFactor);
        state.addSlowFlux(fuelRod.slowFluxPerSecond() * insertionFactor);
    }

    public static boolean hasAdjacentType(final Level level, final BlockPos corePos, final RBMKColumnType type) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (determineColumnType(level, corePos.relative(direction)) == type) {
                return true;
            }
        }
        return false;
    }
}
