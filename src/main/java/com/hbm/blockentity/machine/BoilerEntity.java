package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.fluid.trait.FT_Combustible;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.BlockLitSingleBlockMachine;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoilerEntity extends BaseMachineBlockEntity {
    private static final int FUEL_TANK = 0;
    private static final int WATER_TANK = 1;
    private static final int STEAM_TANK = 2;

    private static final int WATER_TO_STEAM_RATIO = 10;
    private static final int MAX_WATER_PER_TICK = 40;

    private final BasicFluidHandler fluids;

    public BoilerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityType.BOILER_ENTITY.get(), pPos, pBlockState);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.slotModes = List.of();
        this.fluids = buildFluidHandler();
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluids);
    }

    private BasicFluidHandler buildFluidHandler() {
        BasicFluidHandler handler = new BasicFluidHandler()
                .addTank(8_000, Mode.INPUT)
                .addTank(8_000, Mode.INPUT)
                .addTank(16_000, Mode.OUTPUT);
        handler.getFluidTanks().get(FUEL_TANK).setValidator(this::isCombustible);
        handler.getFluidTanks().get(WATER_TANK).setValidator(stack -> stack.getFluid().isSame(Fluids.WATER));
        handler.getFluidTanks().get(STEAM_TANK).setValidator(stack -> stack.getFluid() == ModFluids.STEAM.source().get());
        return handler;
    }

    private boolean isCombustible(FluidStack stack) {
        return getCombustibleTrait(stack) != null;
    }

    private FT_Combustible getCombustibleTrait(FluidStack stack) {
        FluidType type = stack.getFluid().getFluidType();
        if (type instanceof ExtendedFluidType extended) {
            return extended.getTrait(FT_Combustible.class);
        }
        return null;
    }

    private int getWaterPerFuel(FT_Combustible trait) {
        int amount = (int) (trait.getCombustionEnergy() / 25_000L);
        return Math.max(1, Math.min(4, amount));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level == null) {
            return;
        }
        boolean wasRunning = running;
        boolean changed = boilWater();
        if (running != wasRunning) {
            updateLitState();
            changed = true;
        }
        if (changed) {
            setChanged();
            sendUpdatePacket();
        }
    }

    private boolean boilWater() {
        FluidTank fuel = fluids.getFluidTanks().get(FUEL_TANK);
        FluidTank water = fluids.getFluidTanks().get(WATER_TANK);
        FluidTank steam = fluids.getFluidTanks().get(STEAM_TANK);
        if (fuel.isEmpty() || water.isEmpty()) {
            running = false;
            return false;
        }
        FT_Combustible trait = getCombustibleTrait(fuel.getFluid());
        if (trait == null) {
            running = false;
            return false;
        }
        int waterPerFuel = getWaterPerFuel(trait);
        int maxByFuel = fuel.getFluidAmount() * waterPerFuel;
        int maxBySteam = steam.getSpace() / WATER_TO_STEAM_RATIO;
        int toBoil = Math.min(MAX_WATER_PER_TICK, Math.min(water.getFluidAmount(), Math.min(maxByFuel, maxBySteam)));
        if (toBoil <= 0) {
            running = false;
            return false;
        }
        int fuelToConsume = (toBoil + waterPerFuel - 1) / waterPerFuel;
        FluidStack drained = fuel.drain(fuelToConsume, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty() || drained.getAmount() < fuelToConsume) {
            running = false;
            return false;
        }
        water.drain(toBoil, IFluidHandler.FluidAction.EXECUTE);
        steam.fill(new FluidStack(ModFluids.STEAM.source().get(), toBoil * WATER_TO_STEAM_RATIO),
                IFluidHandler.FluidAction.EXECUTE);
        running = true;
        return true;
    }

    private void updateLitState() {
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockLitSingleBlockMachine.LIT)) {
            level.setBlock(worldPosition, state.setValue(BlockLitSingleBlockMachine.LIT, running), 3);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction pSide) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int pIndex, ItemStack pItemStack, @Nullable Direction pDirection) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int pIndex, ItemStack pStack, Direction pDirection) {
        return false;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.BOILER.key());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, fluids.serializeNBT());
        tag.putBoolean(HBMKey.RUNNING, running);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
        running = tag.getBoolean(HBMKey.RUNNING);
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.FLUIDS, fluids.serializeNBT());
        tag.putBoolean(HBMKey.RUNNING, running);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
        if (tag.contains(HBMKey.RUNNING)) {
            running = tag.getBoolean(HBMKey.RUNNING);
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }
}
