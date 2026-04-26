package com.hbm.blockentity.machine.icf;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.gui.menu.ICFPressMenu;

import com.hbm.item.icf.ItemICFPellet;
import com.hbm.item.icf.ItemICFPellet.FuelType;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ICFPressBlockEntity extends BaseMachineBlockEntity implements MenuProvider {

    public static final int SLOT_EMPTY = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_MUON = 2;
    public static final int SLOT_MUON_RETURN = 3;
    public static final int SLOT_FUEL_LEFT = 4;
    public static final int SLOT_FUEL_RIGHT = 5;
    public static final int SLOT_LEFT_BUFFER = 6;
    public static final int SLOT_RIGHT_BUFFER = 7;
    private static final int MUON_CAPACITY = 16;
    private static final int TANK_CAPACITY = 16_000;
    private static final int FLUID_PER_PELLET = 1_000;

    private int muonCharge = 0;
    private final BasicFluidHandler fluids;
    private final FluidTank leftTank;
    private final FluidTank rightTank;

    private final ContainerData containerData = new SimpleContainerData(3) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> muonCharge;
                case 1 -> leftTank.getFluidAmount();
                case 2 -> rightTank.getFluidAmount();
                default -> 0;
            };
        }
    };

    public ICFPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.ICF_PRESS_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(8, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder().addModes(
                1, Mode.INPUT,
                1, Mode.OUTPUT,
                2, Mode.INPUT,
                2, Mode.OUTPUT,
                2, Mode.INPUT
        ).get();
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.fluids = new BasicFluidHandler()
                .addTank(TANK_CAPACITY, Mode.INPUT)
                .addTank(TANK_CAPACITY, Mode.INPUT);
        this.leftTank = createFuelTank();
        this.rightTank = createFuelTank();
        this.fluids.getFluidTanks().set(0, leftTank);
        this.fluids.getFluidTanks().set(1, rightTank);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluids);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        Level level = getLevel();
        if (level == null) return;
        boolean changed = false;
        changed |= chargeMuon();
        refillBuffers();
        changed |= tryAssemble();
        if (changed) {
            setChanged();
        }
    }

    private boolean chargeMuon() {
        ItemStack muon = items.get(SLOT_MUON);
        if (muonCharge >= MUON_CAPACITY || muon.isEmpty() || !muon.is(ModItems.PARTICLE_MUON.get())) {
            return false;
        }
        ItemStack container = new ItemStack(ModItems.PARTICLE_EMPTY.get());
        ItemStack returnSlot = items.get(SLOT_MUON_RETURN);
        if (!returnSlot.isEmpty() && (!ItemStack.isSameItemSameTags(returnSlot, container) || returnSlot.getCount() >= returnSlot.getMaxStackSize())) {
            return false;
        }
        muon.shrink(1);
        if (muon.isEmpty()) {
            items.set(SLOT_MUON, ItemStack.EMPTY);
        }
        addToSlot(SLOT_MUON_RETURN, container);
        muonCharge = MUON_CAPACITY;
        return true;
    }

    private void refillBuffers() {
        moveReserve(SLOT_FUEL_LEFT, SLOT_LEFT_BUFFER);
        moveReserve(SLOT_FUEL_RIGHT, SLOT_RIGHT_BUFFER);
    }

    private void moveReserve(int bufferSlot, int reserveSlot) {
        ItemStack buffer = items.get(bufferSlot);
        if (!buffer.isEmpty()) return;
        ItemStack reserve = items.get(reserveSlot);
        if (reserve.isEmpty()) return;
        if (ItemICFPellet.fuelFromStack(reserve) == null) return;
        ItemStack single = reserve.split(1);
        items.set(bufferSlot, single);
        if (reserve.isEmpty()) {
            items.set(reserveSlot, ItemStack.EMPTY);
        }
    }

    private boolean tryAssemble() {
        ItemStack empty = items.get(SLOT_EMPTY);
        if (empty.isEmpty() || !empty.is(ModItems.icf_pellet_empty.get())) {
            return false;
        }
        ItemStack output = items.get(SLOT_OUTPUT);
        if (!output.isEmpty()) {
            return false;
        }
        FuelPair pair = findFuels();
        if (pair == null) {
            return false;
        }
        boolean muonCatalysed = muonCharge > 0;
        if (muonCatalysed) {
            muonCharge--;
        }
        ItemStack pellet = ItemICFPellet.createStack(pair.primary.type(), pair.secondary.type(), muonCatalysed);
        items.set(SLOT_OUTPUT, pellet);
        consumeSlot(SLOT_EMPTY);
        consumeSource(pair.primary);
        consumeSource(pair.secondary);
        return true;
    }

    private void consumeSlot(int slot) {
        ItemStack stack = items.get(slot);
        stack.shrink(1);
        if (stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        }
    }

    private FuelPair findFuels() {
        FuelSource left = resolveFuel(SLOT_FUEL_LEFT, leftTank);
        FuelSource right = resolveFuel(SLOT_FUEL_RIGHT, rightTank);
        if (left == null || right == null || left.type() == right.type()) {
            return null;
        }
        return new FuelPair(left, right);
    }

    private FuelSource resolveFuel(int slot, FluidTank tank) {
        ItemStack stack = items.get(slot);
        FuelType type = ItemICFPellet.fuelFromStack(stack);
        if (type != null) {
            return new FuelSource(type, slot, null);
        }
        FuelType fluid = ItemICFPellet.fuelFromFluid(tank.getFluid());
        if (fluid != null && tank.getFluidAmount() >= FLUID_PER_PELLET) {
            return new FuelSource(fluid, -1, tank);
        }
        return null;
    }

    private void consumeSource(FuelSource source) {
        if (source == null) return;
        if (source.usesFluid()) {
            source.tank().drain(FLUID_PER_PELLET, IFluidHandler.FluidAction.EXECUTE);
        } else if (source.slot() >= 0) {
            consumeSlot(source.slot());
        }
    }

    private void addToSlot(int slot, ItemStack addition) {
        ItemStack existing = items.get(slot);
        if (existing.isEmpty()) {
            items.set(slot, addition);
        } else if (ItemStack.isSameItemSameTags(existing, addition)) {
            existing.grow(addition.getCount());
        }
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_EMPTY) return stack.is(ModItems.icf_pellet_empty.get());
        if (index == SLOT_MUON) return stack.is(ModItems.PARTICLE_MUON.get());
        if (index == SLOT_LEFT_BUFFER || index == SLOT_FUEL_LEFT) return ItemICFPellet.fuelFromStack(stack) != null;
        if (index == SLOT_RIGHT_BUFFER || index == SLOT_FUEL_RIGHT) return ItemICFPellet.fuelFromStack(stack) != null;
        return false;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_OUTPUT || index == SLOT_MUON_RETURN;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.muonCharge = tag.getInt(HBMKey.PROGRESS);
        if (tag.contains(HBMKey.FLUIDS)) {
            this.fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(HBMKey.PROGRESS, muonCharge);
        tag.put(HBMKey.FLUIDS, this.fluids.serializeNBT());
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.ICF_PRESS.key());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ICFPressMenu(containerId, inventory, this, containerData);
    }

    public Container getContainer() {
        return this;
    }

    private FluidTank createFuelTank() {
        return new FluidTank(TANK_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack != null && !stack.isEmpty() && ItemICFPellet.fuelFromFluid(stack) != null;
            }

            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    public int getLeftTankAmount() {
        return leftTank.getFluidAmount();
    }

    public int getRightTankAmount() {
        return rightTank.getFluidAmount();
    }

    public BasicFluidHandler getFluids() {
        return fluids;
    }

    private record FuelSource(FuelType type, int slot, FluidTank tank) {
        boolean usesFluid() {
            return tank != null;
        }
    }

    private record FuelPair(FuelSource primary, FuelSource secondary) {
    }
}
