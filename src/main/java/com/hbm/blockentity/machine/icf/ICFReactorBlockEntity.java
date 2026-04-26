package com.hbm.blockentity.machine.icf;

import com.hbm.HBMLang;
import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.gui.menu.ICFMenu;

import com.hbm.item.icf.ItemICFPellet;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * Simplified ICF reactor core. Handles pellet inventory, heat buffer and
 * coolant/hot coolant exchange. Full multiblock/laser logic will be ported in
 * follow-up iterations.
 */
public class ICFReactorBlockEntity extends DummyableBlockEntity implements MenuProvider {

    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 4;
    public static final int SLOT_ACTIVE = 5;
    public static final int SLOT_OUTPUT_START = 6;
    public static final int SLOT_OUTPUT_END = 10;
    public static final int SLOT_COOLANT = 11;

    private static final long MAX_HEAT = 1_000_000_000_000L;
    private static final long HEAT_PER_MB = 25_000L;
    private static final long PASSIVE_DECAY = 250_000L;
    private static final int COOLANT_CAPACITY = 512_000;

    private long heat;
    private long laserInput;
    private long maxLaserInput;
    private long lastLaserInput;

    private final BasicFluidHandler fluids = new BasicFluidHandler()
            .addTank(COOLANT_CAPACITY, Mode.INPUT)
            .addTank(COOLANT_CAPACITY, Mode.OUTPUT);

    private final ContainerData containerData = new SimpleContainerData(4) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) Math.min(Integer.MAX_VALUE, heat / 1_000L);
                case 1 -> fluids.getFluidTanks().get(0).getFluidAmount();
                case 2 -> fluids.getFluidTanks().get(1).getFluidAmount();
                case 3 -> (int) Math.min(Integer.MAX_VALUE, lastLaserInput / 1_000L);
                default -> 0;
            };
        }
    };

    public ICFReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.ICF_REACTOR_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(12, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder().addModes(
                SLOT_INPUT_END + 1, Mode.INPUT,
                1, Mode.NONE,
                SLOT_OUTPUT_END - SLOT_OUTPUT_START + 1, Mode.OUTPUT,
                1, Mode.BOTH
        ).get();
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_icf.get());
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, fluids);
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
        this.fluids.getFluidTanks().set(0, new FluidTank(COOLANT_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.COOLANT.source().get())
                        || stack.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER);
            }
        });
        this.fluids.getFluidTanks().set(1, new FluidTank(COOLANT_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.COOLANT_HOT.source().get());
            }
        });
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.heat = tag.getLong(HBMKey.HEAT);
        if (tag.contains(HBMKey.FLUIDS)) {
            this.fluids.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong(HBMKey.HEAT, heat);
        tag.put(HBMKey.FLUIDS, this.fluids.serializeNBT());
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!isFormed) {
            laserInput = 0;
            maxLaserInput = 0;
            return;
        }
        tickFluidSlot();
        handlePelletTransfer();
        processActivePellet();
        convertHeatToCoolant();
        passiveCooldown();
        lastLaserInput = laserInput;
        laserInput = 0L;
        maxLaserInput = 0L;
        if (heat > MAX_HEAT) {
            triggerMeltdown();
        }
    }

    private void tickFluidSlot() {
        ItemStack stack = items.get(SLOT_COOLANT);
        if (stack.isEmpty()) {
            return;
        }
        FluidTank coldTank = fluids.getFluidTanks().get(0);
        FluidTank hotTank = fluids.getFluidTanks().get(1);
        if (stack.getItem() instanceof BucketItem bucket) {
            if (bucket.getFluid().isSame(ModFluids.COOLANT.source().get())) {
                if (coldTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
                    coldTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                    items.set(SLOT_COOLANT, Items.BUCKET.getDefaultInstance());
                    setChanged();
                }
            } else if (bucket.getFluid().isSame(ModFluids.COOLANT_HOT.source().get())) {
                if (hotTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
                    hotTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                    items.set(SLOT_COOLANT, Items.BUCKET.getDefaultInstance());
                    setChanged();
                }
            } else if (bucket == Items.BUCKET) {
                // try filling bucket with hot coolant
                FluidStack drained = hotTank.drain(1000, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty() && drained.getAmount() == 1000) {
                    hotTank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
                    items.set(SLOT_COOLANT, drained.getFluid().getBucket().getDefaultInstance());
                    setChanged();
                }
            }
        }
    }

    private void handlePelletTransfer() {
        ItemStack active = items.get(SLOT_ACTIVE);
        if (!active.isEmpty() && active.is(ModItems.icf_pellet_depleted.get())) {
            pushToOutputs(active.copy());
            items.set(SLOT_ACTIVE, ItemStack.EMPTY);
            setChanged();
        }
        if (items.get(SLOT_ACTIVE).isEmpty()) {
            for (int i = SLOT_INPUT_START; i <= SLOT_INPUT_END; i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty() && stack.is(ModItems.icf_pellet.get())) {
                    items.set(SLOT_ACTIVE, stack.copy());
                    items.set(i, ItemStack.EMPTY);
                    setChanged();
                    break;
                }
            }
        }
    }

    private void pushToOutputs(ItemStack stack) {
        for (int i = SLOT_OUTPUT_START; i <= SLOT_OUTPUT_END; i++) {
            ItemStack current = items.get(i);
            if (current.isEmpty()) {
                items.set(i, stack);
                return;
            }
        }
    }

    private void processActivePellet() {
        ItemStack active = items.get(SLOT_ACTIVE);
        if (active.isEmpty() || !active.is(ModItems.icf_pellet.get())) {
            return;
        }
        long difficulty = ItemICFPellet.getFusingDifficulty(active);
        if (laserInput <= 0) {
            return;
        }
        if (laserInput < difficulty) {
            heat = Math.min(MAX_HEAT, heat + Math.max(1L, laserInput / 4L));
            return;
        }
        long produced = ItemICFPellet.react(active, laserInput);
        heat = Math.min(MAX_HEAT, heat + produced);
        if (ItemICFPellet.isSpent(active)) {
            ItemStack depleted = new ItemStack(ModItems.icf_pellet_depleted.get());
            items.set(SLOT_ACTIVE, depleted);
        }
        setChanged();
    }

    private void convertHeatToCoolant() {
        if (heat <= HEAT_PER_MB) {
            return;
        }
        FluidTank cold = fluids.getFluidTanks().get(0);
        FluidTank hot = fluids.getFluidTanks().get(1);
        if (cold.getFluidAmount() <= 0) {
            return;
        }
        int space = hot.getCapacity() - hot.getFluidAmount();
        if (space <= 0) {
            return;
        }
        long maxByHeat = heat / HEAT_PER_MB;
        int transferable = (int) Math.min(Math.min(maxByHeat, cold.getFluidAmount()), space);
        if (transferable <= 0) {
            return;
        }
        FluidStack drained = cold.drain(transferable, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return;
        }
        hot.fill(new FluidStack(ModFluids.COOLANT_HOT.source().get(), drained.getAmount()), IFluidHandler.FluidAction.EXECUTE);
        heat = Math.max(0L, heat - transferable * HEAT_PER_MB);
        setChanged();
    }

    private void passiveCooldown() {
        if (heat <= 0) {
            heat = 0;
            return;
        }
        heat = Math.max(0L, heat - PASSIVE_DECAY);
    }

    private void triggerMeltdown() {
        Level level = getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = getBlockPos();
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10.0F, Level.ExplosionInteraction.TNT);
        level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState());
        heat = 0;
    }

    public void receiveLaser(long energy, long maxEnergy) {
        if (energy <= 0) {
            return;
        }
        this.laserInput = Math.min(MAX_HEAT, this.laserInput + energy);
        this.maxLaserInput = Math.max(this.maxLaserInput, maxEnergy);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.ICF.key());
    }

    @Override
    public Component getDisplayName() {
        return getDefaultName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ICFMenu(containerId, inventory, this, containerData);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot >= SLOT_INPUT_START && slot <= SLOT_INPUT_END) {
            return stack.is(ModItems.icf_pellet.get());
        }
        if (slot >= SLOT_OUTPUT_START && slot <= SLOT_OUTPUT_END) {
            return false;
        }
        if (slot == SLOT_ACTIVE) {
            return false;
        }
        if (slot == SLOT_COOLANT) {
            return stack.getItem() instanceof BucketItem;
        }
        return super.isItemValid(slot, stack);
    }

    public long getHeat() {
        return heat;
    }

    public Container getContainer() {
        return this;
    }

    public ContainerData getContainerData() {
        return containerData;
    }
}
