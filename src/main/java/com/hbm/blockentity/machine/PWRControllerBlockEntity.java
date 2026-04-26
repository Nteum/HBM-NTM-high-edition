package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.api.math.MathUtils;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.gui.menu.PWRMenu;
import com.hbm.item.pwr.ItemPWRFuel;
import com.hbm.reactor.pwr.PWRFuelType;
import com.hbm.registries.ModBlocks;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.fluid.ExtendedFluidType;
import com.hbm.Inventory.fluid.trait.FT_Heatable;
import com.hbm.Inventory.fluid.trait.FT_Heatable.HeatingStep;
import com.hbm.Inventory.fluid.trait.FT_Heatable.HeatingType;
import com.hbm.Inventory.fluid.trait.FT_PWRModerator;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PWRControllerBlockEntity extends BaseMachineBlockEntity {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_COOLANT = 2;

    public static final int COOLANT_CAPACITY = 128_000;
    public static final long CORE_HEAT_CAPACITY_BASE = 10_000_000L;
    public static final long HULL_HEAT_CAPACITY_BASE = 10_000_000L;

    private static final int DEFAULT_ROD_COUNT = 16;
    private static final int DEFAULT_CONNECTIONS = 32;
    private static final int DEFAULT_HEATEX = 8;
    private static final int DEFAULT_HEATSINK = 8;
    private static final int DEFAULT_CHANNEL = 8;
    private static final int DEFAULT_SOURCE = 1;

    public long coreHeat = 0L;
    public long coreHeatCapacity = CORE_HEAT_CAPACITY_BASE;
    public long hullHeat = 0L;
    public double flux = 0D;

    public double rodLevel = 100D;
    public double rodTarget = 100D;

    public int typeLoaded = -1;
    public int amountLoaded = 0;
    public double progress = 0D;
    public double processTime = 1D;

    public int rodCount = DEFAULT_ROD_COUNT;
    public int connections = DEFAULT_CONNECTIONS;
    public int connectionsControlled = DEFAULT_CONNECTIONS;
    public int heatexCount = DEFAULT_HEATEX;
    public int heatsinkCount = DEFAULT_HEATSINK;
    public int channelCount = DEFAULT_CHANNEL;
    public int sourceCount = DEFAULT_SOURCE;
    public boolean assembled = false;

    private final List<BlockPos> ports = new ArrayList<>();
    private final List<BlockPos> rods = new ArrayList<>();

    private final BasicFluidHandler fluidHandler = new BasicFluidHandler()
            .addTank(COOLANT_CAPACITY, Mode.INPUT)
            .addTank(COOLANT_CAPACITY, Mode.OUTPUT);

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> MathUtils.clampToInt(coreHeat);
                case 1 -> MathUtils.clampToInt(coreHeatCapacity);
                case 2 -> MathUtils.clampToInt(hullHeat);
                case 3 -> MathUtils.clampToInt(HULL_HEAT_CAPACITY_BASE);
                case 4 -> MathUtils.clampToInt(flux * 10D);
                case 5 -> MathUtils.clampToInt(progress);
                case 6 -> MathUtils.clampToInt(processTime);
                case 7 -> MathUtils.clampToInt(rodLevel * 100D);
                case 8 -> MathUtils.clampToInt(rodTarget * 100D);
                case 9 -> typeLoaded;
                case 10 -> amountLoaded;
                case 11 -> rodCount;
                case 12 -> fluidHandler.getFluidTanks().get(0).getFluidAmount();
                case 13 -> fluidHandler.getFluidTanks().get(1).getFluidAmount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return PWRMenu.DATA_COUNT;
        }
    };

    public PWRControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.PWR_CONTROLLER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder().addModes(1, Mode.INPUT, 1, Mode.OUTPUT, 1, Mode.INPUT).get();
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);

        this.fluidHandler.getFluidTanks().set(0, new FluidTank(COOLANT_CAPACITY) {
            @Override
            public boolean isFluidValid(final FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.COOLANT.source().get());
            }
        });
        this.fluidHandler.getFluidTanks().set(1, new FluidTank(COOLANT_CAPACITY) {
            @Override
            public boolean isFluidValid(final FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.COOLANT_HOT.source().get());
            }
        });
        recalcCoreCapacity();
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean dirty = false;

        if (coreHeatCapacity < CORE_HEAT_CAPACITY_BASE) {
            recalcCoreCapacity();
        }

        ItemStack coolantStack = items.get(SLOT_COOLANT);
        if (!coolantStack.isEmpty()) {
            ItemStack drained = fluidHandler.drainItem(0, coolantStack);
            if (drained != coolantStack) {
                items.set(SLOT_COOLANT, drained);
                dirty = true;
            }
        }

        if (!assembled) {
            if (coreHeat != 0L || hullHeat != 0L || flux != 0D || progress != 0D) {
                coreHeat = 0L;
                hullHeat = 0L;
                flux = 0D;
                progress = 0D;
            }
            if (dirty) {
                setChanged();
            }
            sendUpdatePacket();
            return;
        }

        ItemStack fuelStack = items.get(SLOT_FUEL);
        if (typeLoaded == -1 || amountLoaded <= 0) {
            if (ItemPWRFuel.isFreshFuel(fuelStack)) {
                PWRFuelType type = ItemPWRFuel.getFuelType(fuelStack);
                typeLoaded = type.ordinal();
                amountLoaded++;
                fuelStack.shrink(1);
                dirty = true;
            }
        } else if (ItemPWRFuel.isFreshFuel(fuelStack)) {
            PWRFuelType type = ItemPWRFuel.getFuelType(fuelStack);
            if (type.ordinal() == typeLoaded && amountLoaded < rodCount) {
                amountLoaded++;
                fuelStack.shrink(1);
                dirty = true;
            }
        }

        if (Math.abs(rodLevel - rodTarget) < 1D) {
            rodLevel = rodTarget;
        } else if (rodTarget > rodLevel) {
            rodLevel++;
        } else if (rodTarget < rodLevel) {
            rodLevel--;
        }

        int newFlux = sourceCount * 20;
        if (typeLoaded != -1 && amountLoaded > 0 && rodCount > 0) {
            PWRFuelType fuel = PWRFuelType.fromIndex(typeLoaded);
            double usedRods = getTotalProcessMultiplier();
            double fluxPerRod = flux / Math.max(rodCount, 1);
            double outputPerRod = fuel.outputForFlux(fluxPerRod);
            double totalOutput = outputPerRod * amountLoaded * usedRods;
            double totalHeatOutput = totalOutput * fuel.heatEmission;

            coreHeat += (long) totalHeatOutput;
            newFlux += totalOutput;

            processTime = fuel.yield;
            progress += totalOutput;

            if (progress >= processTime) {
                progress -= processTime;
                ItemStack out = items.get(SLOT_OUTPUT);
                ItemStack produced = ItemPWRFuel.createStack(ModItems.pwr_fuel_hot.get(), fuel);
                if (out.isEmpty()) {
                    items.set(SLOT_OUTPUT, produced);
                } else if (ItemPWRFuel.isHotFuel(out)
                        && ItemPWRFuel.getFuelType(out) == fuel
                        && out.getCount() < out.getMaxStackSize()) {
                    out.grow(1);
                }
                amountLoaded--;
                dirty = true;
            }
        }

        if (amountLoaded <= 0) {
            typeLoaded = -1;
        }

        if (amountLoaded > rodCount) {
            amountLoaded = rodCount;
        }

        double coolingDenom = Math.max(getRodCountForCoolant(), 1);
        double coreCoolingApproachNum = getXOverE((double) heatexCount * 5D / coolingDenom, 2D) / 2D;
        long averageCoreHeat = (coreHeat + hullHeat) / 2L;
        coreHeat -= (long) ((coreHeat - averageCoreHeat) * coreCoolingApproachNum);
        hullHeat -= (long) ((hullHeat - averageCoreHeat) * coreCoolingApproachNum);

        updateCoolant();

        coreHeat *= 0.999D;
        hullHeat *= 0.999D;
        flux = newFlux;
        applyModerator();

        if (coreHeat > coreHeatCapacity) {
            coreHeat = coreHeatCapacity;
        }

        if (dirty) {
            setChanged();
        }
        sendUpdatePacket();
    }

    private void updateCoolant() {
        FluidStack coolant = fluidHandler.getFluidTanks().get(0).getFluid();
        if (coolant.isEmpty()) {
            return;
        }
        FT_Heatable trait = null;
        if (coolant.getFluid().getFluidType() instanceof ExtendedFluidType extended) {
            trait = extended.getTrait(FT_Heatable.class);
        }
        if (trait == null || trait.getEfficiency(HeatingType.PWR) <= 0D) {
            return;
        }

        double coolingEff = (double) channelCount / (double) getRodCountForCoolant() * 0.1D;
        if (coolingEff > 1D) {
            coolingEff = 1D;
        }

        int heatToUse = (int) Math.min(Math.min(hullHeat, (long) (hullHeat * coolingEff * trait.getEfficiency(HeatingType.PWR))), 2_000_000_000L);
        HeatingStep step = trait.getFirstStep();
        int coolCycles = coolant.getAmount() / step.amountReq;
        FluidTank hotTank = fluidHandler.getFluidTanks().get(1);
        int hotCycles = (hotTank.getCapacity() - hotTank.getFluidAmount()) / step.amountProduced;
        int heatCycles = heatToUse / step.heatReq;
        int cycles = Math.min(coolCycles, Math.min(hotCycles, heatCycles));

        if (cycles <= 0) {
            return;
        }

        hullHeat -= (long) step.heatReq * cycles;
        fluidHandler.getFluidTanks().get(0).drain(step.amountReq * cycles, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        hotTank.fill(new FluidStack(ModFluids.COOLANT_HOT.source().get(), step.amountProduced * cycles), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
    }

    private int getRodCountForCoolant() {
        return rodCount + (int) Math.ceil(heatsinkCount / 4D);
    }

    public double getTotalProcessMultiplier() {
        double totalConnections = connections + connectionsControlled * (1D - (rodLevel / 100D));
        return connectinFunc(totalConnections);
    }

    private double connectinFunc(double connectionCount) {
        return connectionCount / 10D * (1D - getXOverE(connectionCount, 300D))
                + connectionCount / 150D * getXOverE(connectionCount, 300D);
    }

    private double getXOverE(double x, double d) {
        return 1D - Math.pow(Math.E, -x / d);
    }

    private void recalcCoreCapacity() {
        int cappedHeatsinks = Math.min(heatsinkCount, 80);
        coreHeatCapacity = CORE_HEAT_CAPACITY_BASE + cappedHeatsinks * (CORE_HEAT_CAPACITY_BASE / 20L);
    }

    private void applyModerator() {
        FluidStack coolant = fluidHandler.getFluidTanks().get(0).getFluid();
        if (coolant.isEmpty()) {
            return;
        }
        if (coolant.getFluid().getFluidType() instanceof ExtendedFluidType extended) {
            FT_PWRModerator moderator = extended.getTrait(FT_PWRModerator.class);
            if (moderator != null) {
                flux *= moderator.getMultiplier();
            }
        }
    }

    public void setup(Map<BlockPos, Block> partMap, Map<BlockPos, Block> rodMap) {
        rodCount = 0;
        connections = 0;
        connectionsControlled = 0;
        heatexCount = 0;
        channelCount = 0;
        heatsinkCount = 0;
        sourceCount = 0;
        ports.clear();
        rods.clear();

        int connectionsDouble = 0;
        int connectionsControlledDouble = 0;

        for (Map.Entry<BlockPos, Block> entry : partMap.entrySet()) {
            Block block = entry.getValue();
            if (block == ModBlocks.pwr_fuel_block.get()) {
                rodCount++;
            }
            if (block == ModBlocks.pwr_heatex.get()) {
                heatexCount++;
            }
            if (block == ModBlocks.pwr_channel.get()) {
                channelCount++;
            }
            if (block == ModBlocks.pwr_heatsink.get()) {
                heatsinkCount++;
            }
            if (block == ModBlocks.pwr_neutron_source.get()) {
                sourceCount++;
            }
            if (block == ModBlocks.pwr_port.get()) {
                ports.add(entry.getKey());
            }
        }

        for (Map.Entry<BlockPos, Block> entry : rodMap.entrySet()) {
            BlockPos fuelPos = entry.getKey();
            rods.add(fuelPos);

            for (Direction dir : Direction.values()) {
                boolean controlled = false;
                for (int i = 1; i < 16; i++) {
                    BlockPos checkPos = fuelPos.relative(dir, i);
                    Block atPos = partMap.get(checkPos);
                    if (atPos == null || atPos == ModBlocks.pwr_casing.get()) {
                        break;
                    }
                    if (atPos == ModBlocks.pwr_control.get()) {
                        controlled = true;
                    }
                    if (atPos == ModBlocks.pwr_fuel_block.get()) {
                        if (controlled) {
                            connectionsControlledDouble++;
                        } else {
                            connectionsDouble++;
                        }
                        break;
                    }
                    if (atPos == ModBlocks.pwr_reflector.get()) {
                        if (controlled) {
                            connectionsControlledDouble += 2;
                        } else {
                            connectionsDouble += 2;
                        }
                        break;
                    }
                }
            }
        }

        connections = connectionsDouble / 2;
        connectionsControlled = connectionsControlledDouble / 2;
        heatsinkCount = Math.min(heatsinkCount, 80);
        recalcCoreCapacity();
        setChanged();
    }

    public void setAssembled(boolean assembled) {
        this.assembled = assembled;
        setChanged();
    }

    public boolean isAssembled() {
        return assembled;
    }

    public void setRodTarget(int target) {
        rodTarget = Mth.clamp(target, 0, 100);
        setChanged();
    }

    @Override
    public void handleClientPacket(@NotNull CompoundTag tag) {
        if (tag.contains("rodTarget")) {
            rodTarget = Mth.clamp(tag.getDouble("rodTarget"), 0D, 100D);
        }
    }

    @Override
    public @NotNull CompoundTag getReducedUpdateTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(HBMKey.FLUIDS, this.fluidHandler.serializeNBT());
        tag.putDouble("rodLevel", rodLevel);
        tag.putDouble("rodTarget", rodTarget);
        tag.putInt("typeLoaded", typeLoaded);
        tag.putInt("amountLoaded", amountLoaded);
        tag.putBoolean("assembled", assembled);
        return super.getReducedUpdateTag().merge(tag);
    }

    @Override
    public void handleUpdatePacket(@NotNull CompoundTag tag) {
        super.handleUpdatePacket(tag);
        this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        if (tag.contains("rodLevel")) {
            rodLevel = tag.getDouble("rodLevel");
        }
        if (tag.contains("rodTarget")) {
            rodTarget = tag.getDouble("rodTarget");
        }
        if (tag.contains("typeLoaded")) {
            typeLoaded = tag.getInt("typeLoaded");
        }
        if (tag.contains("amountLoaded")) {
            amountLoaded = tag.getInt("amountLoaded");
        }
        if (tag.contains("assembled")) {
            assembled = tag.getBoolean("assembled");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
        tag.putLong("coreHeat", coreHeat);
        tag.putLong("coreHeatCapacity", coreHeatCapacity);
        tag.putLong("hullHeat", hullHeat);
        tag.putDouble("flux", flux);
        tag.putDouble("rodLevel", rodLevel);
        tag.putDouble("rodTarget", rodTarget);
        tag.putInt("typeLoaded", typeLoaded);
        tag.putInt("amountLoaded", amountLoaded);
        tag.putDouble("progress", progress);
        tag.putDouble("processTime", processTime);
        tag.putInt("rodCount", rodCount);
        tag.putInt("connections", connections);
        tag.putInt("connectionsControlled", connectionsControlled);
        tag.putInt("heatexCount", heatexCount);
        tag.putInt("heatsinkCount", heatsinkCount);
        tag.putInt("channelCount", channelCount);
        tag.putInt("sourceCount", sourceCount);
        tag.putBoolean("assembled", assembled);
        tag.putInt("portCount", ports.size());
        for (int i = 0; i < ports.size(); i++) {
            BlockPos pos = ports.get(i);
            tag.putIntArray("p" + i, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        }
        tag.putInt("rodListCount", rods.size());
        for (int i = 0; i < rods.size(); i++) {
            BlockPos pos = rods.get(i);
            tag.putIntArray("r" + i, new int[]{pos.getX(), pos.getY(), pos.getZ()});
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        coreHeat = tag.getLong("coreHeat");
        coreHeatCapacity = tag.getLong("coreHeatCapacity");
        if (coreHeatCapacity < CORE_HEAT_CAPACITY_BASE) {
            coreHeatCapacity = CORE_HEAT_CAPACITY_BASE;
        }
        hullHeat = tag.getLong("hullHeat");
        flux = tag.getDouble("flux");
        rodLevel = tag.getDouble("rodLevel");
        rodTarget = tag.getDouble("rodTarget");
        typeLoaded = tag.getInt("typeLoaded");
        amountLoaded = tag.getInt("amountLoaded");
        progress = tag.getDouble("progress");
        processTime = tag.getDouble("processTime");
        rodCount = Math.max(tag.getInt("rodCount"), DEFAULT_ROD_COUNT);
        connections = tag.getInt("connections");
        connectionsControlled = tag.getInt("connectionsControlled");
        heatexCount = tag.getInt("heatexCount");
        heatsinkCount = tag.getInt("heatsinkCount");
        channelCount = tag.getInt("channelCount");
        sourceCount = tag.getInt("sourceCount");
        assembled = tag.getBoolean("assembled");

        ports.clear();
        int portCount = tag.getInt("portCount");
        for (int i = 0; i < portCount; i++) {
            int[] port = tag.getIntArray("p" + i);
            if (port.length == 3) {
                ports.add(new BlockPos(port[0], port[1], port[2]));
            }
        }

        rods.clear();
        int rodListCount = tag.getInt("rodListCount");
        for (int i = 0; i < rodListCount; i++) {
            int[] rod = tag.getIntArray("r" + i);
            if (rod.length == 3) {
                rods.add(new BlockPos(rod[0], rod[1], rod[2]));
            }
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_FUEL) {
            return ItemPWRFuel.isFreshFuel(stack);
        }
        if (index == SLOT_COOLANT) {
            return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{SLOT_FUEL, SLOT_OUTPUT, SLOT_COOLANT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return index == SLOT_FUEL || index == SLOT_COOLANT;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_OUTPUT;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.pwrController");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new PWRMenu(containerId, inventory, this, containerData);
    }
}
