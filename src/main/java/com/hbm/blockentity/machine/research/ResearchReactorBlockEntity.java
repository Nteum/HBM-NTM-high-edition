package com.hbm.blockentity.machine.research;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.gui.menu.ResearchReactorMenu;
import com.hbm.handler.radiation.ChunkRadiationManager;

import com.hbm.item.research.ItemResearchFuelPlate;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class ResearchReactorBlockEntity extends DummyableBlockEntity implements MenuProvider {

    public static final int SLOT_COUNT = 12;
    private static final int MAX_HEAT = 50_000;
    private static final double CONTROL_SPEED = 0.04D;
    private static final int[][] NEIGHBORS = {
            {1, 5},
            {0, 6},
            {3, 7},
            {2, 4, 8},
            {3, 9},
            {0, 6, 10},
            {1, 5, 11},
            {2, 8},
            {3, 7, 9},
            {4, 8},
            {5, 11},
            {6, 10}
    };

    private static final Map<Supplier<Item>, Supplier<Item>> WASTE_MAP = Map.of(
            ModItems.plate_fuel_u233, ModItems.waste_plate_u233,
            ModItems.plate_fuel_u235, ModItems.waste_plate_u235,
            ModItems.plate_fuel_mox, ModItems.waste_plate_mox,
            ModItems.plate_fuel_pu239, ModItems.waste_plate_pu239,
            ModItems.plate_fuel_sa326, ModItems.waste_plate_sa326,
            ModItems.plate_fuel_ra226be, ModItems.waste_plate_ra226be,
            ModItems.plate_fuel_pu238be, ModItems.waste_plate_pu238be
    );

    private final int[] slotFlux = new int[SLOT_COUNT];
    private final ContainerData containerData = new SimpleContainerData(5) {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> Math.min(9999, totalFlux);
                case 1 -> heat;
                case 2 -> (int) Math.round(controlLevel * 100.0D);
                case 3 -> (int) Math.round(targetLevel * 100.0D);
                case 4 -> waterLevel & 0xFF;
                default -> 0;
            };
        }
    };

    private double controlLevel;
    private double targetLevel;
    private double lastLevel;
    private int heat;
    private int totalFlux;
    private byte waterLevel;

    public ResearchReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RESEARCH_REACTOR_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_research_reactor.get());
        this.slotModes = new ModeBuilder().addModes(SLOT_COUNT, Mode.BOTH).get();
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("hbmControl", controlLevel);
        tag.putDouble("hbmTarget", targetLevel);
        tag.putDouble("hbmLastControl", lastLevel);
        tag.putInt(HBMKey.HEAT, heat);
        tag.putInt("hbmFlux", totalFlux);
        tag.putByte("hbmWater", waterLevel);
        tag.putIntArray("hbmSlotFlux", slotFlux);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.controlLevel = tag.getDouble("hbmControl");
        this.targetLevel = tag.getDouble("hbmTarget");
        this.lastLevel = tag.getDouble("hbmLastControl");
        this.heat = tag.getInt(HBMKey.HEAT);
        this.totalFlux = tag.getInt("hbmFlux");
        this.waterLevel = tag.getByte("hbmWater");
        int[] savedFlux = tag.getIntArray("hbmSlotFlux");
        if (savedFlux.length == SLOT_COUNT) {
            System.arraycopy(savedFlux, 0, slotFlux, 0, SLOT_COUNT);
        }
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!isFormed || level == null) {
            totalFlux = 0;
            running = false;
            return;
        }
        running = true;
        tickControl();
        performReaction();
        dissipateHeat();
        if (heat > MAX_HEAT) {
            triggerMeltdown();
        }
    }

    private void tickControl() {
        lastLevel = controlLevel;
        if (controlLevel < targetLevel) {
            controlLevel = Math.min(targetLevel, controlLevel + CONTROL_SPEED);
        } else if (controlLevel > targetLevel) {
            controlLevel = Math.max(targetLevel, controlLevel - CONTROL_SPEED);
        }
    }

    private void performReaction() {
        totalFlux = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                slotFlux[i] = 0;
                continue;
            }
            if (stack.getItem() instanceof ItemResearchFuelPlate plate) {
                int produced = plate.react(stack, slotFlux[i]);
                slotFlux[i] = 0;
                totalFlux += produced;
                heat += produced * 2;
                if (plate.isSpent(stack)) {
                    items.set(i, getWasteForPlate(stack));
                }
                int[] neighbors = NEIGHBORS[i];
                for (int neighbor : neighbors) {
                    slotFlux[neighbor] += (int) (produced * controlLevel);
                }
            } else {
                slotFlux[i] = 0;
            }
        }
        setChanged();
    }

    private ItemStack getWasteForPlate(ItemStack stack) {
        for (Map.Entry<Supplier<Item>, Supplier<Item>> entry : WASTE_MAP.entrySet()) {
            if (stack.is(entry.getKey().get())) {
                return entry.getValue().get().getDefaultInstance();
            }
        }
        return ItemStack.EMPTY;
    }

    private void dissipateHeat() {
        if (heat <= 0) {
            heat = 0;
            return;
        }
        waterLevel = computeWaterLevel();
        if (waterLevel > 0) {
            heat -= (int) (heat * 0.07D * waterLevel / 12D);
        } else {
            heat = Math.max(0, heat - 1);
        }
    }

    private byte computeWaterLevel() {
        if (level == null) {
            return 0;
        }
        byte water = 0;
        BlockPos pos = getBlockPos();
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP || dir == Direction.DOWN) {
                BlockPos check = pos.relative(Direction.UP, 1 + dir.getStepY() * 2);
                if (isWater(check)) {
                    water++;
                }
            } else {
                BlockPos column = pos.relative(dir);
                for (int i = 0; i < 3; i++) {
                    if (isWater(column.above(i))) {
                        water++;
                    }
                }
            }
        }
        return water;
    }

    private boolean isWater(BlockPos check) {
        if (level == null) {
            return false;
        }
        return level.getBlockState(check).getFluidState().isSource()
                && level.getBlockState(check).getFluidState().getType() == net.minecraft.world.level.material.Fluids.WATER;
    }

    private void triggerMeltdown() {
        if (level == null) {
            return;
        }
        BlockPos pos = getBlockPos();
        Containers.dropContents(level, pos, this);
        level.removeBlock(pos, false);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 8.0F, Level.ExplosionInteraction.BLOCK);
            ChunkRadiationManager.proxy.incrementRad(serverLevel, pos, 50.0F);
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        BlockState below = level.getBlockState(pos.below());
        if (!below.isAir()) {
            level.setBlock(pos.below(), Blocks.LAVA.defaultBlockState(), 3);
        }
        running = false;
    }

    public boolean isSubmerged() {
        if (level == null) {
            return false;
        }
        BlockPos pos = getBlockPos();
        return isWater(pos.east().above())
                || isWater(pos.west().above())
                || isWater(pos.north().above())
                || isWater(pos.south().above());
    }

    public double getRenderLevel(double partialTicks) {
        return lastLevel + (controlLevel - lastLevel) * partialTicks;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public void setTargetLevel(double percent) {
        targetLevel = Mth.clamp(percent, 0.0D, 1.0D);
        setChanged();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ResearchReactorMenu(containerId, inventory, this, containerData);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.hbm.research_reactor");
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.hbm.research_reactor");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            Containers.dropContents(level, pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return new AABB(getBlockPos()).inflate(1.0D, 2.5D, 1.0D);
    }

    public int getFlux() {
        return totalFlux;
    }

    public int getHeat() {
        return heat;
    }

    public double getControlLevel() {
        return controlLevel;
    }

    public double getTargetLevelPercent() {
        return targetLevel;
    }

    public byte getWaterLevel() {
        return waterLevel;
    }
}
