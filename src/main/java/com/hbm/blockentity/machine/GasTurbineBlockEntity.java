package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.fluid.trait.FT_Combustible;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.fluid.VisitRestrictWrapper;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.gui.menu.GasTurbineMenu;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GasTurbineBlockEntity extends DummyableBlockEntity {

    private static final int OUTPUT_SLOT = 0;
    private static final int FUEL_CONFIG_SLOT = 1;

    private static final int FUEL_TANK = 0;
    private static final int LUBE_TANK = 1;
    private static final int WATER_TANK = 2;
    private static final int STEAM_TANK = 3;

    public static final long CAPACITY = 1_000_000L;
    private static final long MAX_EXTRACT = 5_000L;

    private static final int STARTUP_TIME = 580;
    private static final int SHUTDOWN_TIME = 225;

    private static final Map<String, Double> FUEL_MULTIPLIER = new HashMap<>();

    static {
        FUEL_MULTIPLIER.put(ModFluids.REFINERY_GAS.source().getId().toString(), 50D);
        FUEL_MULTIPLIER.put(ModFluids.BIOGAS.source().getId().toString(), 15D);
        FUEL_MULTIPLIER.put(ModFluids.REFORM_GAS.source().getId().toString(), 5D);
        FUEL_MULTIPLIER.put(ModFluids.DEUTERIUM.source().getId().toString(), 30D);
    }

    private final BasicEnergyContainer energy = new BasicEnergyContainer(CAPACITY, 0, MAX_EXTRACT);
    private final BasicFluidHandler fluids;
    private final ContainerData containerData;

    private int rpm;
    private int temp;
    private int throttle;
    private int powerSliderPos;
    private int state; //0 = off, -1 = startup, 1 = running
    private int counter;
    private boolean autoMode;
    private int instantPowerOutput;
    private long powerBeforeNet;
    private double fuelRemainder;
    private double waterToBoil;

    public GasTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.GAS_TURBINE_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder()
                .addMode(Mode.OUTPUT)
                .addMode(Mode.INPUT)
                .get();
        this.fluids = buildFluidHandler();
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluids);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new com.hbm.api.energy.HybridEnergyStorage(energy));
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energy));
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_turbine_gas.get());
        this.isFormed = true;
        this.containerData = createDataSlots();
        energy.setListener(this::setChanged);
    }

    private BasicFluidHandler buildFluidHandler() {
        BasicFluidHandler handler = new BasicFluidHandler()
                .addTank(100_000, Mode.INPUT)
                .addTank(16_000, Mode.INPUT)
                .addTank(16_000, Mode.INPUT)
                .addTank(160_000, Mode.OUTPUT);
        handler.getFluidTanks().get(FUEL_TANK).setValidator(stack -> hasGasTrait(stack));
        handler.getFluidTanks().get(LUBE_TANK).setValidator(stack -> stack.getFluid() == ModFluids.OIL.source().get());
        handler.getFluidTanks().get(WATER_TANK).setValidator(stack -> stack.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER));
        handler.getFluidTanks().get(STEAM_TANK).setValidator(stack -> stack.getFluid() == ModFluids.HOT_STEAM.source().get());
        return handler;
    }

    private ContainerData createDataSlots() {
        return new SimpleContainerData(13) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> (int) energy.getEnergy();
                    case 1 -> (int) (energy.getEnergy() >>> 32);
                    case 2 -> rpm;
                    case 3 -> temp;
                    case 4 -> state;
                    case 5 -> autoMode ? 1 : 0;
                    case 6 -> powerSliderPos;
                    case 7 -> throttle;
                    case 8 -> fluids.getFluidTanks().get(STEAM_TANK).getFluidAmount();
                    case 9 -> fluids.getFluidTanks().get(WATER_TANK).getFluidAmount();
                    case 10 -> fluids.getFluidTanks().get(FUEL_TANK).getFluidAmount();
                    case 11 -> fluids.getFluidTanks().get(LUBE_TANK).getFluidAmount();
                    case 12 -> instantPowerOutput;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 6) {
                    setPowerSliderPos(value);
                }
            }
        };
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        powerBeforeNet = energy.getEnergy();
        updateAutoSlider();

        switch (state) {
            case -1 -> startup();
            case 1 -> run();
            default -> shutdown();
        }

        TransmitUtils.chargeItem(this, getStackInSlot(OUTPUT_SLOT));
        TransmitUtils.outputOnly(this);

        if (level != null && level.getGameTime() % 20 == 0) {
            sendUpdatePacket();
        }
    }

    private void updateAutoSlider() {
        throttle = powerSliderPos * 100 / 60;
        if (!autoMode) {
            return;
        }
        int target;
        FluidTank fuel = fluids.getFluidTanks().get(FUEL_TANK);
        if (fuel.getFluidAmount() * 10 > fuel.getCapacity()) {
            target = 60 - (int) (60 * energy.getEnergy() / CAPACITY);
        } else {
            target = (int) (fuel.getFluidAmount() * 0.0001 * (60 - (int) (60 * energy.getEnergy() / CAPACITY)));
        }
        if (target > powerSliderPos) {
            powerSliderPos++;
        } else if (target < powerSliderPos) {
            powerSliderPos--;
        }
    }

    private void startup() {
        counter++;
        if (counter <= 20) {
            rpm = 5 * counter;
        } else if (counter <= 40) {
            rpm = 100 - 5 * (counter - 20);
        } else if (counter > 50) {
            rpm = 10 + (counter - 50) * 90 / (STARTUP_TIME - 50);
            temp = 300 + (counter - 50) * 500 / (STARTUP_TIME - 50);
        }
        if (counter >= STARTUP_TIME) {
            state = 1;
            counter = 0;
        }
    }

    private void shutdown() {
        autoMode = false;
        instantPowerOutput = 0;
        if (powerSliderPos > 0) {
            powerSliderPos--;
        }
        if (rpm > 0) {
            rpm = Math.max(0, rpm - 2);
        }
        if (temp > 20) {
            temp = Math.max(20, temp - 4);
        }
    }

    private void run() {
        if (!hasAcceptableFuel() || !hasLubricant()) {
            state = 0;
            return;
        }
        adjustRPM();
        adjustTemp();
        burnFuel();
        boilWater();
        pushSteam();
    }

    private void adjustRPM() {
        int target = Math.min(100, powerSliderPos == 0 ? 10 : 10 + powerSliderPos);
        if (rpm < target && (level.getGameTime() % 5 == 0)) {
            rpm++;
        } else if (rpm > target && (level.getGameTime() % 2 == 0)) {
            rpm--;
        }
    }

    private void adjustTemp() {
        int maxTemp = getBurnTemperature();
        int target = (int) (300 + (maxTemp - 300) * throttle / 100.0);
        if (temp < target && (level.getGameTime() % 2 == 0)) {
            temp++;
        } else if (temp > target && (level.getGameTime() % 2 == 0)) {
            temp--;
        }
    }

    private int getBurnTemperature() {
        FluidTank fuel = fluids.getFluidTanks().get(FUEL_TANK);
        if (fuel.isEmpty()) {
            return 600;
        }
        FT_Combustible trait = getCombustibleTrait(fuel.getFluid());
        if (trait == null) {
            return 600;
        }
        double energy = trait.getCombustionEnergy();
        return (int) Math.floor(800D - (Math.pow(Math.E, -energy / 100_000D)) * 300D);
    }

    private void burnFuel() {
        FluidTank fuel = fluids.getFluidTanks().get(FUEL_TANK);
        if (fuel.isEmpty()) {
            state = 0;
            return;
        }
        double consumption = getMaxConsumption(fuel.getFluid()) * throttle / 100.0;
        fuelRemainder += consumption;
        int whole = (int) Math.floor(fuelRemainder);
        if (whole > 0) {
            FluidStack drained = fuel.drain(whole, IFluidHandler.FluidAction.EXECUTE);
            if (drained.isEmpty() || drained.getAmount() < whole) {
                state = 0;
                return;
            }
            fuelRemainder -= whole;
            fluids.getFluidTanks().get(LUBE_TANK).drain(1, IFluidHandler.FluidAction.EXECUTE);
            long produced = calculateEnergy(drained, whole);
            instantPowerOutput = (int) produced;
            energy.receive(produced, false);
        }
        if (fuel.getFluidAmount() <= 0 || fluids.getFluidTanks().get(LUBE_TANK).getFluidAmount() <= 0) {
            state = 0;
        }
    }

    private long calculateEnergy(FluidStack fuel, int amount) {
        FT_Combustible trait = getCombustibleTrait(fuel);
        if (trait == null) {
            return 0;
        }
        double multiplier = trait.getCombustionEnergy() / 1_000D;
        int rpmEff = Math.max(0, rpm - 10);
        return (long) (multiplier * amount * rpmEff / 90D);
    }

    private void boilWater() {
        FluidTank water = fluids.getFluidTanks().get(WATER_TANK);
        FluidTank steam = fluids.getFluidTanks().get(STEAM_TANK);
        int heatCycles = (int) Math.floor(getMaxConsumption(fluids.getFluidTanks().get(FUEL_TANK).getFluid()) * throttle / 200.0);
        int waterCycles = Math.min(water.getFluidAmount(), steam.getSpace() / 10);
        int cycles = Math.min(Math.min(heatCycles, waterCycles), 100);
        if (cycles > 0) {
            water.drain(cycles, IFluidHandler.FluidAction.EXECUTE);
            steam.fill(new FluidStack(ModFluids.HOT_STEAM.source().get(), cycles * 10), IFluidHandler.FluidAction.EXECUTE);
            waterToBoil = cycles;
        } else {
            waterToBoil = 0;
        }
    }

    private void pushSteam() {
        // steam output handled via proxy capability exposure
    }

    private double getMaxConsumption(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return 5D;
        }
        ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
        String name = key != null ? key.toString() : "";
        return FUEL_MULTIPLIER.getOrDefault(name, 5D);
    }

    private boolean hasGasTrait(FluidStack stack) {
        return hasGasTrait(stack.getFluid());
    }

    private boolean hasGasTrait(net.minecraft.world.level.material.Fluid fluid) {
        FluidType type = fluid.getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended) {
            FT_Combustible trait = extended.getTrait(FT_Combustible.class);
            return trait != null && trait.getGrade() == FT_Combustible.FuelGrade.GAS;
        }
        return false;
    }

    private FT_Combustible getCombustibleTrait(FluidStack stack) {
        FluidType type = stack.getFluid().getFluidType();
        if (type instanceof com.hbm.Inventory.fluid.ExtendedFluidType extended) {
            return extended.getTrait(FT_Combustible.class);
        }
        return null;
    }

    private boolean hasAcceptableFuel() {
        FluidTank fuel = fluids.getFluidTanks().get(FUEL_TANK);
        return !fuel.isEmpty() && hasGasTrait(fuel.getFluid());
    }

    private boolean hasLubricant() {
        return fluids.getFluidTanks().get(LUBE_TANK).getFluidAmount() > 0;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public BasicEnergyContainer getEnergy() {
        return energy;
    }

    public BasicFluidHandler getFluids() {
        return fluids;
    }

    public double getWaterToBoil() {
        return waterToBoil;
    }

    public long getPowerBeforeNet() {
        return powerBeforeNet;
    }

    public void setPowerSliderPos(int value) {
        int clamped = Math.max(0, Math.min(60, value));
        if (clamped != powerSliderPos) {
            powerSliderPos = clamped;
            setChanged();
        }
    }

    public void toggleAutoMode() {
        setAutoMode(!autoMode);
    }

    public void setAutoMode(boolean enabled) {
        if (autoMode != enabled) {
            autoMode = enabled;
            setChanged();
        }
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public int getState() {
        return state;
    }

    public void requestStart() {
        if (state == 0 && hasAcceptableFuel() && hasLubricant()) {
            counter = 0;
            state = -1;
        }
    }

    public void requestStop() {
        state = 0;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ENERGY, energy.serializeNBT());
        tag.put(HBMKey.FLUIDS, fluids.serializeNBT());
        tag.putInt("rpm", rpm);
        tag.putInt("temp", temp);
        tag.putInt("state", state);
        tag.putInt("counter", counter);
        tag.putInt("slider", powerSliderPos);
        tag.putBoolean("auto", autoMode);
        tag.putDouble("fuelRem", fuelRemainder);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.ENERGY)) {
            energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        }
        if (tag.contains(HBMKey.FLUIDS)) {
            fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
        rpm = tag.getInt("rpm");
        temp = tag.getInt("temp");
        state = tag.getInt("state");
        counter = tag.getInt("counter");
        powerSliderPos = tag.getInt("slider");
        autoMode = tag.getBoolean("auto");
        fuelRemainder = tag.getDouble("fuelRem");
    }

    @NotNull
    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.ENERGY, energy.serializeNBT());
        tag.put(HBMKey.FLUIDS, fluids.serializeNBT());
        tag.putInt("rpm", rpm);
        tag.putInt("temp", temp);
        tag.putInt("state", state);
        tag.putInt("slider", powerSliderPos);
        tag.putBoolean("auto", autoMode);
        return tag;
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.ENERGY)) {
            energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        }
        if (tag.contains(HBMKey.FLUIDS)) {
            fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
        rpm = tag.getInt("rpm");
        temp = tag.getInt("temp");
        state = tag.getInt("state");
        powerSliderPos = tag.getInt("slider");
        autoMode = tag.getBoolean("auto");
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new GasTurbineMenu(containerId, inventory, this, containerData);
    }

    @Override
    public void giveProxyCapabilities(Vec3i offset, TileProxyBase proxy, Capability<?> cap, Set<Direction> directions) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (offset.equals(new Vec3i(-1, 0, -1)) || offset.equals(new Vec3i(-1, 0, 1))) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(fluids, false, FUEL_TANK), directions);
                return;
            }
            if (offset.equals(new Vec3i(-2, 0, -1)) || offset.equals(new Vec3i(-2, 0, 1))) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(fluids, false, LUBE_TANK), directions);
                return;
            }
            if (offset.equals(new Vec3i(2, 0, -1)) || offset.equals(new Vec3i(2, 0, 1))) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(fluids, false, WATER_TANK), directions);
                return;
            }
            if (offset.equals(new Vec3i(3, 1, 0))) {
                proxy.capabilitiesContent.addCapability(cap, new VisitRestrictWrapper(fluids, false, STEAM_TANK), directions);
                return;
            }
        } else if (cap == ForgeCapabilities.ENERGY || cap == HBMCaps.LONG_ENERGY) {
            if (offset.equals(new Vec3i(0, 1, -1))) {
                proxy.capabilitiesContent.addCapability(cap, proxyCap(cap), directions);
                return;
            }
        }
        super.giveProxyCapabilities(offset, proxy, cap, directions);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_turbine_gas");
    }

    private Object proxyCap(Capability<?> cap) {
        if (cap == ForgeCapabilities.ENERGY) {
            return new com.hbm.api.energy.HybridEnergyStorage(energy);
        }
        if (cap == HBMCaps.LONG_ENERGY) {
            return new ProxyEnergyHandler(energy);
        }
        return null;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{OUTPUT_SLOT};
        }
        return new int[]{OUTPUT_SLOT, FUEL_CONFIG_SLOT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == OUTPUT_SLOT;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        if (slot == OUTPUT_SLOT && stack.isEmpty()) {
            ContainerHelper.removeItem(items, slot, stack.getCount());
        }
    }
}
