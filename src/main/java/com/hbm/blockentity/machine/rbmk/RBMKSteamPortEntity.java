package com.hbm.blockentity.machine.rbmk;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.rbmk.BlockRBMKBase;
import com.hbm.block.machine.rbmk.BlockRBMKSteamPort;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.utils.EnumUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class RBMKSteamPortEntity extends BaseMachineBlockEntity {

    private static final int TANK_CAPACITY = 32_000;
    private static final int TRANSFER_PER_TICK = 100;

    private final BasicFluidHandler fluids = new BasicFluidHandler().addTank(TANK_CAPACITY, Mode.BOTH);

    public RBMKSteamPortEntity(final BlockPos pos, final BlockState state) {
        super(ModBlockEntityType.RBMK_STEAM_PORT_ENTITY.get(), pos, state);
        this.items = NonNullList.create();
        this.slotModes = java.util.List.of();
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, fluids);
        final FluidTank tank = this.fluids.getFluidTanks().get(0);
        tank.setValidator(stack -> isInlet() ? isAcceptedInput(stack) : stack.getFluid().isSame(ModFluids.STEAM.source().get()));
    }

    public void serverTick() {
        if (level == null) {
            return;
        }
        if (isInlet()) {
            pullFromAdjacentHandlers();
            pushWaterIntoAdjacentColumns();
        } else {
            pullSteamFromAdjacentColumns();
            pushSteamToAdjacentHandlers();
        }
    }

    private void pullFromAdjacentHandlers() {
        if (level == null) {
            return;
        }
        final FluidTank tank = fluids.getFluidTanks().get(0);
        final int space = tank.getCapacity() - tank.getFluidAmount();
        if (space <= 0) {
            return;
        }
        for (Direction direction : EnumUtils.DIRECTIONS) {
            final BlockPos targetPos = worldPosition.relative(direction);
            if (isRbmkColumn(targetPos)) {
                continue;
            }
            final BlockEntity neighbour = level.getBlockEntity(targetPos);
            if (neighbour == null) {
                continue;
            }
            neighbour.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                final FluidStack simulated = handler.drain(TRANSFER_PER_TICK, IFluidHandler.FluidAction.SIMULATE);
                if (!simulated.isEmpty() && isAcceptedInput(simulated)) {
                    final int fillable = tank.fill(simulated, IFluidHandler.FluidAction.SIMULATE);
                    if (fillable > 0) {
                        final FluidStack drained = handler.drain(fillable, IFluidHandler.FluidAction.EXECUTE);
                        if (!drained.isEmpty()) {
                            tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                            setChanged();
                        }
                    }
                }
            });
        }
    }

    private void pushWaterIntoAdjacentColumns() {
        final FluidTank tank = fluids.getFluidTanks().get(0);
        if (tank.isEmpty()) {
            return;
        }
        for (RBMKBaseEntity base : adjacentColumns()) {
            if (tank.isEmpty()) {
                break;
            }
            final int moved = base.receiveWaterFromPort(Math.min(TRANSFER_PER_TICK, tank.getFluidAmount()), tank.getFluid());
            if (moved > 0) {
                tank.drain(moved, IFluidHandler.FluidAction.EXECUTE);
                setChanged();
            }
        }
    }

    private void pullSteamFromAdjacentColumns() {
        final FluidTank tank = fluids.getFluidTanks().get(0);
        final int space = tank.getCapacity() - tank.getFluidAmount();
        if (space <= 0) {
            return;
        }
        for (RBMKBaseEntity base : adjacentColumns()) {
            if (tank.getCapacity() - tank.getFluidAmount() <= 0) {
                break;
            }
            final FluidStack drained = base.extractSteamForPort(Math.min(TRANSFER_PER_TICK, tank.getCapacity() - tank.getFluidAmount()));
            if (!drained.isEmpty()) {
                tank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                setChanged();
            }
        }
    }

    private void pushSteamToAdjacentHandlers() {
        if (level == null) {
            return;
        }
        final FluidTank tank = fluids.getFluidTanks().get(0);
        if (tank.isEmpty()) {
            return;
        }
        for (Direction direction : EnumUtils.DIRECTIONS) {
            if (tank.isEmpty()) {
                break;
            }
            final BlockPos targetPos = worldPosition.relative(direction);
            if (isRbmkColumn(targetPos)) {
                continue;
            }
            final BlockEntity neighbour = level.getBlockEntity(targetPos);
            if (neighbour == null) {
                continue;
            }
            neighbour.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(handler -> {
                final FluidStack simulated = tank.drain(TRANSFER_PER_TICK, IFluidHandler.FluidAction.SIMULATE);
                if (!simulated.isEmpty()) {
                    final int filled = handler.fill(simulated, IFluidHandler.FluidAction.EXECUTE);
                    if (filled > 0) {
                        tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        setChanged();
                    }
                }
            });
        }
    }

    private List<RBMKBaseEntity> adjacentColumns() {
        final List<RBMKBaseEntity> result = new ArrayList<>();
        if (level == null) {
            return result;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            final BlockPos targetPos = worldPosition.relative(direction);
            final BlockState state = level.getBlockState(targetPos);
            if (state.getBlock() instanceof BlockRBMKBase baseBlock) {
                final BlockPos core = baseBlock.getCore(state, level, targetPos);
                final BlockEntity blockEntity = level.getBlockEntity(core);
                if (blockEntity instanceof RBMKBaseEntity rbmkBase) {
                    result.add(rbmkBase);
                }
            }
        }
        return result;
    }

    private boolean isRbmkColumn(final BlockPos pos) {
        if (level == null) {
            return false;
        }
        return level.getBlockState(pos).getBlock() instanceof BlockRBMKBase;
    }

    private boolean isAcceptedInput(final FluidStack stack) {
        return stack.getFluid().isSame(Fluids.WATER) || stack.getFluid().isSame(ModFluids.IRRADIATED_WATER.source().get());
    }

    private boolean isInlet() {
        return getBlockState().getBlock() instanceof BlockRBMKSteamPort port && port.isInlet();
    }

    public String debugSummary() {
        final FluidTank tank = fluids.getFluidTanks().get(0);
        final String kind = isInlet() ? "RBMK进水口" : "RBMK蒸汽出口";
        final String fluid = tank.isEmpty() ? "empty" : tank.getFluid().getDisplayName().getString();
        return kind + " | tank=" + tank.getFluidAmount() + "/" + tank.getCapacity() + " | fluid=" + fluid + " | links=" + adjacentColumns().size();
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, fluids.serializeNBT());
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(isInlet() ? "block.hbm.rbmk_steam_inlet" : "block.hbm.rbmk_steam_outlet");
    }

    @Override
    public AbstractContainerMenu createMenu(final int pContainerId, final Inventory pInventory) {
        return null;
    }
}
