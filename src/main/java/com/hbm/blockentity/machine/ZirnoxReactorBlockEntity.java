package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.block.machine.BlockZirnoxReactor;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.gui.menu.ZirnoxMenu;

import com.hbm.item.zirnox.ItemZirnoxRod;
import com.hbm.item.zirnox.ItemZirnoxRod.ZirnoxRodType;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.multiblock.DummableHelper;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ZirnoxReactorBlockEntity extends DummyableBlockEntity {
    public static final int MAX_HEAT = 100_000;
    public static final int MAX_PRESSURE = 100_000;
    public static final int STEAM_CAPACITY = 8_000;
    public static final int CO2_CAPACITY = 16_000;
    public static final int WATER_CAPACITY = 32_000;

    private static final int SLOT_ROD_END = 24;
    private static final int SLOT_CO2_IN = 24;
    private static final int SLOT_WATER_IN = 25;
    private static final int SLOT_CO2_OUT = 26;
    private static final int SLOT_WATER_OUT = 27;

    public int heat;
    public int pressure;
    public boolean isOn;
    private int output;

    private final BasicFluidHandler fluidHandler = new BasicFluidHandler()
            .addTank(STEAM_CAPACITY, Mode.BOTH)
            .addTank(CO2_CAPACITY, Mode.INPUT)
            .addTank(WATER_CAPACITY, Mode.INPUT);

    private final ContainerData containerData = new SimpleContainerData(6) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> heat;
                case 1 -> pressure;
                case 2 -> fluidHandler.getFluidTanks().get(0).getFluidAmount();
                case 3 -> fluidHandler.getFluidTanks().get(1).getFluidAmount();
                case 4 -> fluidHandler.getFluidTanks().get(2).getFluidAmount();
                case 5 -> isOn ? 1 : 0;
                default -> 0;
            };
        }
    };

    public ZirnoxReactorBlockEntity(BlockPos pPos, BlockState pState) {
        super(ModBlockEntityType.ZIRNOX_REACTOR_ENTITY.get(), pPos, pState);
        this.items = NonNullList.withSize(28, ItemStack.EMPTY);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_zirnox.get());
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
        this.fluidHandler.getFluidTanks().set(0, new FluidTank(STEAM_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.SUPERHOT_STEAM.source().get());
            }
        });
        this.fluidHandler.getFluidTanks().set(1, new FluidTank(CO2_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(ModFluids.CARBON_DIOXIDE.source().get());
            }
        });
        this.fluidHandler.getFluidTanks().set(2, new FluidTank(WATER_CAPACITY) {
            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(Fluids.WATER);
            }
        });
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot < SLOT_ROD_END) {
            return stack.getItem() instanceof ItemZirnoxRod;
        }
        if (slot == SLOT_CO2_IN) {
            return isFluidContainer(stack, ModFluids.CARBON_DIOXIDE.source().get());
        }
        if (slot == SLOT_WATER_IN) {
            return isFluidContainer(stack, Fluids.WATER);
        }
        return false;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level == null) {
            return;
        }

        output = 0;

        InventoryUtils.handleItems(this, stack -> fluidHandler.drainItem(1, stack), SLOT_CO2_IN, SLOT_CO2_OUT);
        InventoryUtils.handleItems(this, stack -> fluidHandler.drainItem(2, stack), SLOT_WATER_IN, SLOT_WATER_OUT);

        if (isOn) {
            for (int i = 0; i < SLOT_ROD_END; i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemZirnoxRod) {
                    decay(i);
                }
            }
        }

        int co2Fill = fluidHandler.getFluidTanks().get(1).getFluidAmount();
        pressure = (co2Fill * 2) + (int) ((float) heat * ((float) co2Fill / (float) CO2_CAPACITY));

        if (heat > 0 && heat < MAX_HEAT) {
            if (fluidHandler.getFluidTanks().get(2).getFluidAmount() > 0
                    && co2Fill > 0
                    && fluidHandler.getFluidTanks().get(0).getFluidAmount() < STEAM_CAPACITY) {
                generateSteam();
                heat -= (int) ((float) heat * (float) pressure / 1_000_000F);
            } else {
                heat -= 10;
            }
        }

        if (heat < 0) {
            heat = 0;
        }

        checkIfMeltdown();
        sendUpdatePacket();
    }

    private void generateSteam() {
        if (heat <= 10_256) {
            return;
        }
        int co2Fill = fluidHandler.getFluidTanks().get(1).getFluidAmount();
        int waterFill = fluidHandler.getFluidTanks().get(2).getFluidAmount();
        int steamFill = fluidHandler.getFluidTanks().get(0).getFluidAmount();

        int cycle = (int) ((((float) heat - 10_256F) / (float) MAX_HEAT)
                * Math.min(((float) co2Fill / 14_000F), 1F) * 25F * 5F);
        output = cycle;

        int drained = Math.min(cycle, waterFill);
        int space = STEAM_CAPACITY - steamFill;
        int produced = Math.min(drained, space);

        fluidHandler.getFluidTanks().get(2).drain(produced, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        fluidHandler.getFluidTanks().get(0).fill(new FluidStack(ModFluids.SUPERHOT_STEAM.source().get(), produced),
                net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean hasFuelRod(int id) {
        ItemStack stack = items.get(id);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemZirnoxRod) {
            ZirnoxRodType type = ItemZirnoxRod.getRodType(stack);
            return !type.breeding;
        }
        return false;
    }

    private int getNeighbourCount(int id) {
        int[] neighbours = getNeighbouringSlots(id);
        if (neighbours == null) {
            return 0;
        }
        int count = 0;
        for (int neighbour : neighbours) {
            if (hasFuelRod(neighbour)) {
                count++;
            }
        }
        return count;
    }

    private void decay(int id) {
        ItemStack stack = items.get(id);
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemZirnoxRod)) {
            return;
        }
        ZirnoxRodType type = ItemZirnoxRod.getRodType(stack);
        int decay = getNeighbourCount(id);
        if (!type.breeding) {
            decay++;
        }
        for (int i = 0; i < decay; i++) {
            heat += type.heat;
            ItemZirnoxRod.incrementLifeTime(stack);
            if (ItemZirnoxRod.getLifeTime(stack) > type.maxLife) {
                items.set(id, getDepletedStack(type));
                break;
            }
        }
    }

    private ItemStack getDepletedStack(ZirnoxRodType type) {
        return switch (type) {
            case NATURAL_URANIUM_FUEL -> new ItemStack(ModItems.rod_zirnox_natural_uranium_fuel_depleted.get());
            case URANIUM_FUEL -> new ItemStack(ModItems.rod_zirnox_uranium_fuel_depleted.get());
            case TH232 -> ItemZirnoxRod.createStack(ModItems.rod_zirnox.get(), ZirnoxRodType.THORIUM_FUEL);
            case THORIUM_FUEL -> new ItemStack(ModItems.rod_zirnox_thorium_fuel_depleted.get());
            case MOX_FUEL -> new ItemStack(ModItems.rod_zirnox_mox_fuel_depleted.get());
            case PLUTONIUM_FUEL -> new ItemStack(ModItems.rod_zirnox_plutonium_fuel_depleted.get());
            case U233_FUEL -> new ItemStack(ModItems.rod_zirnox_u233_fuel_depleted.get());
            case U235_FUEL -> new ItemStack(ModItems.rod_zirnox_u235_fuel_depleted.get());
            case LES_FUEL -> new ItemStack(ModItems.rod_zirnox_les_fuel_depleted.get());
            case LITHIUM -> new ItemStack(ModItems.rod_zirnox_tritium.get());
            case ZFB_MOX -> new ItemStack(ModItems.rod_zirnox_zfb_mox_depleted.get());
        };
    }

    private void checkIfMeltdown() {
        if (pressure > MAX_PRESSURE || heat > MAX_HEAT) {
            meltdown();
        }
    }

    private boolean isFluidContainer(ItemStack stack, net.minecraft.world.level.material.Fluid fluid) {
        if (stack.getItem() instanceof BucketItem bucket) {
            return bucket.getFluid().isSame(fluid);
        }
        FluidStack contained = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
        return !contained.isEmpty() && contained.getFluid().isSame(fluid);
    }

    private void meltdown() {
        if (level == null || level.isClientSide) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        BlockState state = getBlockState();
        DummableHelper.clearSpace(level, worldPosition, state, state.getValue(BlockZirnoxReactor.FACING));
        level.setBlock(worldPosition, ModBlocks.zirnox_destroyed.get().defaultBlockState(), Block.UPDATE_ALL);
        level.explode(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 2.5D, worldPosition.getZ() + 0.5D, 12.0F, Level.ExplosionInteraction.BLOCK);
        setChanged();
    }

    public void toggleActive() {
        this.isOn = !this.isOn;
        setChanged();
    }

    public void ventCarbonDioxide() {
        FluidTank tank = fluidHandler.getFluidTanks().get(1);
        tank.drain(1_000, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        heat = tag.getInt(HBMKey.HEAT);
        pressure = tag.getInt(HBMKey.PRESSURE);
        isOn = tag.getBoolean(HBMKey.RUNNING);
        if (tag.contains(HBMKey.FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(HBMKey.HEAT, heat);
        tag.putInt(HBMKey.PRESSURE, pressure);
        tag.putBoolean(HBMKey.RUNNING, isOn);
        tag.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.zirnox");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new ZirnoxMenu(pContainerId, pInventory, this, containerData);
    }

    private int[] getNeighbouringSlots(int id) {
        return switch (id) {
            case 0 -> new int[]{1, 7};
            case 1 -> new int[]{0, 2, 8};
            case 2 -> new int[]{1, 9};
            case 3 -> new int[]{4, 10};
            case 4 -> new int[]{3, 5, 11};
            case 5 -> new int[]{4, 6, 12};
            case 6 -> new int[]{5, 13};
            case 7 -> new int[]{0, 8, 14};
            case 8 -> new int[]{1, 7, 9, 15};
            case 9 -> new int[]{2, 8, 16};
            case 10 -> new int[]{3, 11, 17};
            case 11 -> new int[]{4, 10, 12, 18};
            case 12 -> new int[]{5, 11, 13, 19};
            case 13 -> new int[]{6, 12, 20};
            case 14 -> new int[]{7, 15, 21};
            case 15 -> new int[]{8, 14, 16, 22};
            case 16 -> new int[]{9, 15, 23};
            case 17 -> new int[]{10, 18};
            case 18 -> new int[]{11, 17, 19};
            case 19 -> new int[]{12, 18, 20};
            case 20 -> new int[]{13, 19};
            case 21 -> new int[]{14, 22};
            case 22 -> new int[]{15, 21, 23};
            case 23 -> new int[]{16, 22};
            default -> null;
        };
    }
}
