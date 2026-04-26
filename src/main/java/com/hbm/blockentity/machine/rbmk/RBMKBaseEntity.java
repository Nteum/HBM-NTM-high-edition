package com.hbm.blockentity.machine.rbmk;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.RBMKBaseMenu;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import com.hbm.reactor.rbmk.RBMKLidType;
import com.hbm.reactor.rbmk.RBMKSettings;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModSounds;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Optional;

/**
 * 反应堆核心方块实体。当前负责把方块注册到 {@link RBMKManager}，方便后续接入中子/热模拟。
 */
public class RBMKBaseEntity extends DummyableBlockEntity {

    private static final double TARGET_HEAT = 100.0D;
    private static final double MAX_HEAT_CONVERSION_PER_TICK = 2.0D;
    private static final double ENERGY_PER_HEAT = 10_000.0D;
    private static final int WATER_TANK_CAPACITY = 16_000;
    private static final int STEAM_TANK_CAPACITY = 16_000;
    private static final double HEAT_PER_MB = 5.0D; // 移除 5 heat -> 1 mB 蒸汽

    private final BasicEnergyContainer energy = new BasicEnergyContainer(10_000_000L, 10_000_000L, 50_000L);
    private final BasicFluidHandler fluidHandler = new BasicFluidHandler()
            .addTank(WATER_TANK_CAPACITY, Mode.INPUT)
            .addTank(STEAM_TANK_CAPACITY, Mode.OUTPUT);
    private final int[] dataSlots = new int[8];
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return index >= 0 && index < dataSlots.length ? dataSlots[index] : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < dataSlots.length) {
                dataSlots[index] = value;
            }
        }

        @Override
        public int getCount() {
            return dataSlots.length;
        }
    };

    public RBMKBaseEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntityType.RBMK_BASE_ENTITY.get(), pPos, pState);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.slotModes = java.util.List.of();
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_rbmk_base.get());
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, new HybridEnergyStorage(this.energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
        // 限制冷却液类型：仅允许水及放射性水
        this.fluidHandler.getFluidTanks().set(0, new FluidTank(WATER_TANK_CAPACITY) {
            @Override
            public boolean isFluidValid(final FluidStack stack) {
                return stack.getFluid().isSame(Fluids.WATER) || stack.getFluid().isSame(ModFluids.IRRADIATED_WATER.source().get());
            }
        });
        this.energy.setListener(this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            RBMKLevelContext context = RBMKManager.context(serverLevel);
            RBMKLidType lidType = getLidType();
            context.registerColumn(getBlockPos(), RBMKSettings.forLevel(serverLevel)).setLidType(lidType);
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        RBMKLevelContext context = RBMKManager.context(serverLevel);
        Optional<RBMKColumnState> columnOpt = context.column(worldPosition);
        if (columnOpt.isEmpty()) {
            updateClientData(context, null);
            return;
        }

        RBMKColumnState column = columnOpt.get();
        if (energy.getNeeded() > 0 && column.heat() > TARGET_HEAT) {
            double heatAvailable = Math.min(column.heat() - TARGET_HEAT, MAX_HEAT_CONVERSION_PER_TICK);
            long energyToProduce = (long) Math.min(energy.getNeeded(), heatAvailable * ENERGY_PER_HEAT);
            if (energyToProduce > 0) {
                long accepted = energy.receive(energyToProduce, false);
                if (accepted > 0) {
                    double heatUsed = accepted / ENERGY_PER_HEAT;
                    context.addHeat(worldPosition, -heatUsed);
                    setChanged();
                }
            }
        }

        // 水冷：将水转为蒸汽并带走热量，产出的蒸汽可被管道/锅炉取走
        coolAndBoil(context, column);

        TransmitUtils.outputOnly(this);
        updateClientData(context, column);
    }

    private void coolAndBoil(final RBMKLevelContext context, final RBMKColumnState column) {
        final FluidTank water = fluidHandler.getFluidTanks().get(0);
        final FluidTank steam = fluidHandler.getFluidTanks().get(1);

        if (water.isEmpty() || column.heat() <= 0) {
            return;
        }

        final int steamSpace = steam.getCapacity() - steam.getFluidAmount();
        if (steamSpace <= 0) {
            return;
        }

        // 按热量、供水量、蒸汽空间三者的最小值计算
        final int maxByHeat = (int) Math.floor(column.heat() / HEAT_PER_MB);
        final int maxByWater = water.getFluidAmount();
        final int mbToConvert = Math.min(Math.min(maxByHeat, maxByWater), steamSpace);
        if (mbToConvert <= 0) {
            return;
        }

        water.drain(mbToConvert, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        steam.fill(new FluidStack(ModFluids.STEAM.source().get(), mbToConvert), net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        context.addHeat(worldPosition, -mbToConvert * HEAT_PER_MB);
        setChanged();
    }

    @Override
    public void onChunkUnloaded() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).removeColumn(getBlockPos());
        }
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).removeColumn(getBlockPos());
        }
        super.setRemoved();
    }

    public RBMKLidType getLidType() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockRBMKBase.LID)) {
            return state.getValue(BlockRBMKBase.LID);
        }
        return RBMKLidType.NONE;
    }

    public void setLidType(RBMKLidType type) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BlockRBMKBase.LID) && state.getValue(BlockRBMKBase.LID) != type) {
            level.setBlock(worldPosition, state.setValue(BlockRBMKBase.LID, type), Block.UPDATE_ALL);
        }
        RBMKManager.context(serverLevel).setLidState(worldPosition, type);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.ENERGY)) {
            energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        }
        if (tag.contains(HBMKey.FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.ENERGY, energy.serializeNBT());
        tag.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new RBMKBaseMenu(containerId, inventory, this, getContainerData());
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_rbmk_base");
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public long getEnergyStored() {
        return energy.getEnergy();
    }

    public long getEnergyCapacity() {
        return energy.getCapacity();
    }

    public int getWaterAmount() {
        return fluidHandler.getFluidTanks().get(0).getFluidAmount();
    }

    public int getWaterCapacity() {
        return fluidHandler.getFluidTanks().get(0).getCapacity();
    }

    public int getSteamAmount() {
        return fluidHandler.getFluidTanks().get(1).getFluidAmount();
    }

    public int getSteamCapacity() {
        return fluidHandler.getFluidTanks().get(1).getCapacity();
    }

    public int receiveWaterFromPort(final int amount, final FluidStack fluidStack) {
        if (amount <= 0 || fluidStack.isEmpty()) {
            return 0;
        }
        final FluidStack insert = fluidStack.copy();
        insert.setAmount(amount);
        final int accepted = fluidHandler.getFluidTanks().get(0).fill(insert,
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        if (accepted > 0) {
            setChanged();
        }
        return accepted;
    }

    public FluidStack extractSteamForPort(final int amount) {
        if (amount <= 0) {
            return FluidStack.EMPTY;
        }
        final FluidStack drained = fluidHandler.getFluidTanks().get(1).drain(amount,
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
            setChanged();
        }
        return drained;
    }

    public void consumeWaterForBoiler(final int amount) {
        if (amount <= 0) {
            return;
        }
        fluidHandler.getFluidTanks().get(0).drain(amount, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    public void produceSteamForBoiler(final int amount) {
        if (amount <= 0) {
            return;
        }
        fluidHandler.getFluidTanks().get(1).fill(
                new FluidStack(ModFluids.STEAM.source().get(), amount),
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    public void triggerAz5() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos controlPos = worldPosition.above();
        BlockState stateAbove = level.getBlockState(controlPos);
        if (level.getBlockEntity(controlPos) instanceof RBMKControlRodEntity controlRodEntity) {
            controlRodEntity.engageAz5();
        } else if (stateAbove.getBlock() instanceof BlockRBMKControlRod controlRod) {
            if (stateAbove.getValue(BlockRBMKControlRod.INSERTION) != BlockRBMKControlRod.MAX_INSERTION) {
                level.setBlock(controlPos, stateAbove.setValue(BlockRBMKControlRod.INSERTION, BlockRBMKControlRod.MAX_INSERTION), Block.UPDATE_ALL);
            }
            level.playSound(null, controlPos, ModSounds.BLOCK_RBMK_AZ5_COVER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.playSound(null, worldPosition, ModSounds.BLOCK_RBMK_AZ5_COVER.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        RBMKManager.context(serverLevel).setControlRodInsertion(worldPosition, 1.0D);
    }

    private void updateClientData(final RBMKLevelContext context, final RBMKColumnState column) {
        dataSlots[2] = (int) Math.min(Integer.MAX_VALUE, energy.getEnergy());
        dataSlots[3] = (int) Math.min(Integer.MAX_VALUE, energy.getCapacity());
        dataSlots[4] = fluidHandler.getFluidTanks().get(0).getFluidAmount();
        dataSlots[5] = fluidHandler.getFluidTanks().get(1).getFluidAmount();

        if (column != null && context != null) {
            dataSlots[0] = (int) Math.round(column.heat() * 10.0D);
            dataSlots[1] = (int) Math.round(column.settings().meltdownHeat() * 10.0D);
            dataSlots[6] = (int) Math.round(column.controlRodInsertion() * 100.0D);
            dataSlots[7] = (int) Math.round(context.controlRodAverage() * 100.0D);
        } else {
            dataSlots[0] = 0;
            dataSlots[1] = 0;
            dataSlots[6] = 0;
            dataSlots[7] = 0;
        }
    }
}
