package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.block.machine.WoodBurnerBlock;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.WoodBurnerMenu;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoodBurnerBlockEntity extends BaseMachineBlockEntity {

    private static final int FUEL_SLOT = 0;
    private static final int ASH_SLOT = 1;
    private static final int CHARGE_SLOT = 2;
    private static final int[] TOP_SLOTS = new int[]{FUEL_SLOT};
    private static final int[] SIDE_SLOTS = new int[]{CHARGE_SLOT};
    private static final int[] BOTTOM_SLOTS = new int[]{ASH_SLOT, CHARGE_SLOT};

    private static final long CAPACITY = 100_000L;
    private static final long GENERATION_RATE = 50L;
    private static final long MAX_EXTRACT = GENERATION_RATE * 2;

    private final BasicEnergyContainer energy = new BasicEnergyContainer(CAPACITY, 0, MAX_EXTRACT);
    private final net.minecraft.world.inventory.ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> maxBurnTime;
                case 2 -> (int) (energy.getEnergy());
                case 3 -> (int) (energy.getEnergy() >>> 32);
                case 4 -> (int) (energy.getCapacity());
                case 5 -> (int) (energy.getCapacity() >>> 32);
                case 6 -> enabled ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 6) {
                enabled = value != 0;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    private int burnTime;
    private int maxBurnTime;
    private boolean enabled = true;

    public WoodBurnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.WOOD_BURNER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder()
                .addMode(Mode.INPUT)
                .addMode(Mode.OUTPUT)
                .addMode(Mode.BOTH)
                .get();
        energy.setListener(this::setChanged);
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energy));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY,
                new com.hbm.api.energy.HybridEnergyStorage(this.energy));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        boolean wasBurning = isBurning();

        if (enabled && burnTime <= 0 && canStartBurn()) {
            startBurning();
        }

        if (enabled && burnTime > 0) {
            burnTime--;
            energy.receive(GENERATION_RATE, false);
            TransmitUtils.chargeItem(this, getStackInSlot(CHARGE_SLOT));
            if (burnTime == 0) {
                finishBurnCycle();
            }
        }

        TransmitUtils.outputOnly(this);

        if (wasBurning != isBurning()) {
            updateLitState();
        }
    }

    private void updateLitState() {
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof WoodBurnerBlock)) {
            return;
        }
        level.setBlock(worldPosition, state.setValue(WoodBurnerBlock.LIT, isBurning()), 3);
    }

    private boolean canStartBurn() {
        ItemStack fuel = getStackInSlot(FUEL_SLOT);
        return !fuel.isEmpty() && energy.getEnergy() < energy.getCapacity() && ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING) > 0;
    }

    private void startBurning() {
        ItemStack fuel = getStackInSlot(FUEL_SLOT);
        int ticks = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        if (ticks <= 0) {
            return;
        }
        burnTime = ticks;
        maxBurnTime = ticks;

        if (fuel.is(Items.LAVA_BUCKET)) {
            setItem(FUEL_SLOT, new ItemStack(Items.BUCKET));
        } else {
            fuel.shrink(1);
        }
        setChanged();
    }

    private void finishBurnCycle() {
        RandomSource random = level != null ? level.random : RandomSource.create();
        if (random.nextFloat() < 0.5F) {
            ItemStack ash = getStackInSlot(ASH_SLOT);
            if (ash.isEmpty()) {
                setItem(ASH_SLOT, new ItemStack(ModItems.WOOD_ASH_POWDER.get()));
            } else if (ash.is(ModItems.WOOD_ASH_POWDER.get()) && ash.getCount() < ash.getMaxStackSize()) {
                ash.grow(1);
                setChanged();
            }
        }
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggleEnabled() {
        this.enabled = !this.enabled;
        setChanged();
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
        tag.putBoolean("Enabled", enabled);
        tag.put(HBMKey.ENERGY, energy.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
        enabled = tag.contains("Enabled") ? tag.getBoolean("Enabled") : true;
        if (tag.contains(HBMKey.ENERGY)) {
            energy.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        }
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.hbm.machine_wood_burner");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new WoodBurnerMenu(containerId, inventory, this, this.containerData);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == FUEL_SLOT) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        if (index == CHARGE_SLOT) {
            return isChargeable(stack);
        }
        return false;
    }

    private boolean isChargeable(ItemStack stack) {
        return stack.is(ModTags.Items.CHARGEABLE)
                || stack.getCapability(HBMCaps.LONG_ENERGY).isPresent()
                || stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (index == ASH_SLOT) {
            return direction == Direction.DOWN;
        }
        if (index == CHARGE_SLOT) {
            return direction != Direction.UP;
        }
        return index == FUEL_SLOT && direction == Direction.DOWN;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        if (index == FUEL_SLOT) {
            return direction == Direction.UP && ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        if (index == CHARGE_SLOT) {
            return direction != Direction.DOWN && isChargeable(stack);
        }
        return false;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public IEnergyContainer getEnergyContainer() {
        return energy;
    }
}
