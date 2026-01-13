package com.hbm.blockentity.machine.component;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Shared helper that handles the spent-steam -> water conversion used by both the
 * single block condenser and the multiblock cooling tower.
 */
public final class CondenserLogic {

    private static final String TAG_FLUIDS = "Fluids";
    private static final String TAG_TIMER = "WaterTimer";
    private static final String TAG_THROUGHPUT = "Throughput";

    private final BasicFluidHandler fluidHandler;
    private final int conversionRate;
    private final int steamTankIndex;
    private final int waterTankIndex;
    private int waterTimer;
    private int throughput;

    public CondenserLogic(int steamCapacity, int waterCapacity, int conversionRate) {
        this.conversionRate = conversionRate;
        this.steamTankIndex = 0;
        this.waterTankIndex = 1;
        this.fluidHandler = new BasicFluidHandler()
                .addTank(steamCapacity, Mode.INPUT)
                .addTank(waterCapacity, Mode.OUTPUT);
        configureTanks();
    }

    private void configureTanks() {
        FluidTank steamTank = fluidHandler.getFluidTanks().get(steamTankIndex);
        steamTank.setValidator(stack -> stack.getFluid() == ModFluids.SPENT_STEAM.source().get());
        steamTank.setFluid(new FluidStack(ModFluids.SPENT_STEAM.source().get(), 0));

        FluidTank waterTank = fluidHandler.getFluidTanks().get(waterTankIndex);
        waterTank.setValidator(stack -> stack.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER));
        waterTank.setFluid(new FluidStack(net.minecraft.world.level.material.Fluids.WATER, 0));
    }

    public BasicFluidHandler handler() {
        return fluidHandler;
    }

    /**
     * Runs a server-side tick. Returns true when a state change requires syncing.
     */
    public boolean tick(Level level) {
        if (waterTimer > 0) {
            waterTimer--;
        }
        throughput = 0;
        FluidTank steam = fluidHandler.getFluidTanks().get(steamTankIndex);
        FluidTank water = fluidHandler.getFluidTanks().get(waterTankIndex);
        if (steam.isEmpty()) {
            return false;
        }
        int space = water.getSpace();
        if (space <= 0) {
            return false;
        }
        int toConvert = conversionRate <= 0 ? steam.getFluidAmount() : Math.min(conversionRate, steam.getFluidAmount());
        toConvert = Math.min(toConvert, space);
        if (toConvert <= 0) {
            return false;
        }
        FluidStack drained = steam.drain(toConvert, FluidTank.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return false;
        }
        FluidStack fillStack = new FluidStack(net.minecraft.world.level.material.Fluids.WATER, drained.getAmount());
        water.fill(fillStack, FluidTank.FluidAction.EXECUTE);
        waterTimer = 20;
        throughput = drained.getAmount();
        return true;
    }

    public int getWaterTimer() {
        return waterTimer;
    }

    public int getThroughput() {
        return throughput;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put(TAG_FLUIDS, fluidHandler.serializeNBT());
        tag.putInt(TAG_TIMER, waterTimer);
        tag.putInt(TAG_THROUGHPUT, throughput);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains(TAG_FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(TAG_FLUIDS));
        } else {
            configureTanks();
        }
        waterTimer = tag.getInt(TAG_TIMER);
        throughput = tag.getInt(TAG_THROUGHPUT);
    }

    public void appendToTag(CompoundTag tag) {
        tag.put(TAG_FLUIDS, fluidHandler.serializeNBT());
        tag.putInt(TAG_TIMER, waterTimer);
        tag.putInt(TAG_THROUGHPUT, throughput);
    }

    public void readFromTag(CompoundTag tag) {
        if (tag.contains(TAG_FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(TAG_FLUIDS));
        } else {
            configureTanks();
        }
        if (tag.contains(TAG_TIMER)) {
            waterTimer = tag.getInt(TAG_TIMER);
        }
        if (tag.contains(TAG_THROUGHPUT)) {
            throughput = tag.getInt(TAG_THROUGHPUT);
        }
    }

    public void writeSyncTag(CompoundTag tag) {
        tag.put(TAG_FLUIDS, fluidHandler.serializeNBT());
        tag.putInt(HBMKey.WATER_TIMER, waterTimer);
    }

    public void readSyncTag(CompoundTag tag) {
        if (tag.contains(TAG_FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(TAG_FLUIDS));
        }
        if (tag.contains(HBMKey.WATER_TIMER)) {
            waterTimer = tag.getInt(HBMKey.WATER_TIMER);
        }
    }
}
