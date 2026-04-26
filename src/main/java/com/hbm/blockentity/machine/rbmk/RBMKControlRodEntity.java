package com.hbm.blockentity.machine.rbmk;

import com.hbm.api.Mode;
import com.hbm.block.machine.rbmk.BlockRBMKControlRod;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.gui.menu.RBMKControlRodMenu;
import com.hbm.item.rbmk.ItemRBMKControlRod;
import com.hbm.reactor.rbmk.RBMKColumnState;
import com.hbm.reactor.rbmk.RBMKLevelContext;
import com.hbm.reactor.rbmk.RBMKManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RBMKControlRodEntity extends BaseMachineBlockEntity {

    private static final int CONTROL_ROD_SLOT = 0;
    private static final int SLOT_COUNT = 1;
    private static final int AZ5_LOCK_TICKS = 60;
    private static final int DATA_LENGTH = 14;
    private static final float CONTROL_SPEED = 0.00277F;
    private static final String TAG_LEVEL = "ControlLevel";
    private static final String TAG_TARGET = "TargetLevel";
    private static final String TAG_COLOR = "ControlColor";
    private static final String TAG_FUNCTION = "Function";
    private static final String TAG_LEVEL_LOWER = "LevelLower";
    private static final String TAG_LEVEL_UPPER = "LevelUpper";
    private static final String TAG_HEAT_LOWER = "HeatLower";
    private static final String TAG_HEAT_UPPER = "HeatUpper";

    private final ContainerData containerData = new SimpleContainerData(DATA_LENGTH);
    private final int[] dataSlots = new int[DATA_LENGTH];
    protected float currentLevel;
    protected float targetLevel;
    protected int az5CooldownTicks;
    protected ControlGroup selectedGroup;
    private AutoFunction autoFunction = AutoFunction.LINEAR;
    private double levelLower;
    private double levelUpper = 100.0D;
    private double heatLower = 100.0D;
    private double heatUpper = 600.0D;

    public RBMKControlRodEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.RBMK_CONTROL_ROD_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        this.slotModes = java.util.List.of(Mode.BOTH);
        this.currentLevel = deriveInsertionFromState();
        this.targetLevel = currentLevel;
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        final boolean autoColumn = isAutoColumn();
        final boolean hasRod = hasControlRod();
        if (!hasRod) {
            if (!Mth.equal(currentLevel, 0.0F)) {
                forceLevel(0.0F);
            } else {
                syncBlockStateFromLevel();
            }
            if (!Mth.equal(targetLevel, 0.0F)) {
                setTargetLevel(0.0F);
            }
        } else {
            if (autoColumn) {
                applyAutomaticTarget(serverLevel);
            }
            stepTowardsTarget();
        }

        BlockPos corePos = worldPosition.below();
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        context.setControlRodInsertion(corePos, currentLevel);

        RBMKBaseEntity base = level.getBlockEntity(corePos) instanceof RBMKBaseEntity rbmkBase ? rbmkBase : null;
        Optional<RBMKColumnState> column = context.column(corePos);
        updateClientData(column.orElse(null), base, context);
    }

    private void applyAutomaticTarget(final ServerLevel serverLevel) {
        RBMKLevelContext context = RBMKManager.context(serverLevel);
        RBMKColumnState column = context.column(worldPosition.below()).orElse(null);
        if (column == null) {
            return;
        }
        double computed = computeAutomaticLevel(column.heat());
        setTargetLevel((float) (computed / 100.0D));
        column.setTargetControlRodInsertion(targetLevel);
    }

    private double computeAutomaticLevel(final double heat) {
        double lowerBound = Math.min(heatLower, heatUpper);
        double upperBound = Math.max(heatLower, heatUpper);
        if (heat <= lowerBound) {
            return levelLower;
        }
        if (heat >= upperBound) {
            return levelUpper;
        }
        return switch (autoFunction) {
            case LINEAR -> (heat - heatLower) * ((levelUpper - levelLower) / (heatUpper - heatLower)) + levelLower;
            case QUAD_UP -> Math.pow((heat - heatLower) / (heatUpper - heatLower), 2) * (levelUpper - levelLower) + levelLower;
            case QUAD_DOWN -> Math.pow((heat - heatUpper) / (heatLower - heatUpper), 2) * (levelLower - levelUpper) + levelUpper;
        };
    }

    protected void updateClientData(@Nullable RBMKColumnState column, @Nullable RBMKBaseEntity base,
                                  RBMKLevelContext context) {
        dataSlots[0] = column != null ? (int) Math.round(column.heat() * 10.0D) : 0;
        dataSlots[1] = column != null ? (int) Math.round(column.settings().meltdownHeat() * 10.0D) : 0;
        dataSlots[2] = base != null ? (int) Math.min(Integer.MAX_VALUE, base.getEnergyStored()) : 0;
        dataSlots[3] = base != null ? (int) Math.min(Integer.MAX_VALUE, base.getEnergyCapacity()) : 0;
        dataSlots[4] = base != null ? base.getWaterAmount() : 0;
        dataSlots[5] = base != null ? base.getSteamAmount() : 0;
        dataSlots[6] = column != null ? (int) Math.round(column.controlRodInsertion() * 100.0D) : 0;
        dataSlots[7] = (int) Math.round(context.controlRodAverage() * 100.0D);
        dataSlots[8] = (int) Math.round(currentLevel * 100.0D);
        dataSlots[9] = fractionToStage(currentLevel);
        dataSlots[10] = context.snapshot().size();
        if (az5CooldownTicks > 0) {
            az5CooldownTicks--;
            if (az5CooldownTicks == 0) {
                setChanged();
            }
        }
        dataSlots[11] = az5CooldownTicks;
        dataSlots[12] = selectedGroup != null ? selectedGroup.ordinal() : -1;
        dataSlots[13] = hasControlRod() ? 1 : 0;

        for (int i = 0; i < dataSlots.length; i++) {
            containerData.set(i, dataSlots[i]);
        }
    }

    protected void stepTowardsTarget() {
        if (Mth.equal(currentLevel, targetLevel)) {
            syncBlockStateFromLevel();
            return;
        }
        float next = currentLevel;
        if (currentLevel < targetLevel) {
            next = Math.min(targetLevel, currentLevel + CONTROL_SPEED);
        } else if (currentLevel > targetLevel) {
            next = Math.max(targetLevel, currentLevel - CONTROL_SPEED);
        }
        if (!Mth.equal(currentLevel, next)) {
            currentLevel = next;
            syncBlockStateFromLevel();
            setChanged();
        }
    }

    public boolean isAz5CoolingDown() {
        return az5CooldownTicks > 0;
    }

    public int getAz5CooldownTicks() {
        return az5CooldownTicks;
    }

    public void engageAz5() {
        az5CooldownTicks = AZ5_LOCK_TICKS;
        forceLevel(1.0F);
        setTargetLevel(1.0F);
        setChanged();
    }

    public void toggleColorGroup(int index) {
        ControlGroup[] groups = ControlGroup.values();
        if (index < 0 || index >= groups.length) {
            return;
        }
        ControlGroup newGroup = groups[index];
        if (selectedGroup == newGroup) {
            selectedGroup = null;
        } else {
            selectedGroup = newGroup;
        }
        setChanged();
    }

    public void setColorGroup(@Nullable ControlGroup group) {
        if (selectedGroup != group) {
            selectedGroup = group;
            setChanged();
        }
    }

    @Override
    public void onChunkUnloaded() {
        resetColumn();
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        resetColumn();
        super.setRemoved();
    }

    private void resetColumn() {
        if (level instanceof ServerLevel serverLevel) {
            RBMKManager.context(serverLevel).setControlRodInsertion(worldPosition.below(), 0.0D);
        }
        currentLevel = 0.0F;
        targetLevel = 0.0F;
        selectedGroup = null;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        currentLevel = Mth.clamp(tag.getFloat(TAG_LEVEL), 0.0F, 1.0F);
        targetLevel = tag.contains(TAG_TARGET) ? Mth.clamp(tag.getFloat(TAG_TARGET), 0.0F, 1.0F) : currentLevel;
        if (tag.contains("Az5Cooldown")) {
            az5CooldownTicks = tag.getInt("Az5Cooldown");
        }
        if (tag.contains(TAG_COLOR)) {
            int idx = tag.getInt(TAG_COLOR);
            ControlGroup[] groups = ControlGroup.values();
            if (idx >= 0 && idx < groups.length) {
                selectedGroup = groups[idx];
            }
        } else {
            selectedGroup = null;
        }
        if (tag.contains(TAG_FUNCTION)) {
            int idx = Math.floorMod(tag.getInt(TAG_FUNCTION), AutoFunction.values().length);
            autoFunction = AutoFunction.values()[idx];
        }
        levelLower = tag.getDouble(TAG_LEVEL_LOWER);
        levelUpper = tag.contains(TAG_LEVEL_UPPER) ? tag.getDouble(TAG_LEVEL_UPPER) : 100.0D;
        heatLower = tag.contains(TAG_HEAT_LOWER) ? tag.getDouble(TAG_HEAT_LOWER) : 100.0D;
        heatUpper = tag.contains(TAG_HEAT_UPPER) ? tag.getDouble(TAG_HEAT_UPPER) : 600.0D;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat(TAG_LEVEL, currentLevel);
        tag.putFloat(TAG_TARGET, targetLevel);
        if (az5CooldownTicks > 0) {
            tag.putInt("Az5Cooldown", az5CooldownTicks);
        }
        if (selectedGroup != null) {
            tag.putInt(TAG_COLOR, selectedGroup.ordinal());
        }
        tag.putInt(TAG_FUNCTION, autoFunction.ordinal());
        tag.putDouble(TAG_LEVEL_LOWER, levelLower);
        tag.putDouble(TAG_LEVEL_UPPER, levelUpper);
        tag.putDouble(TAG_HEAT_LOWER, heatLower);
        tag.putDouble(TAG_HEAT_UPPER, heatUpper);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(isAutoColumn()
                ? "block.hbm.machine_rbmk_control_auto"
                : "block.hbm.machine_rbmk_control_rod");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new RBMKControlRodMenu(containerId, inventory, this, getContainerData());
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    public boolean setInsertionPercent(int percent) {
        return setInsertionFraction(percent / 100.0D);
    }

    public boolean setInsertionFraction(double targetFraction) {
        float clamped = Mth.clamp((float) targetFraction, 0.0F, 1.0F);
        if (isAz5CoolingDown() && clamped < 1.0F) {
            return false;
        }
        if (!hasControlRod() && clamped > 0.0F) {
            return false;
        }
        setTargetLevel(clamped);
        return true;
    }

    protected void setTargetLevel(float target) {
        float clamped = Mth.clamp(target, 0.0F, 1.0F);
        if (!Mth.equal(clamped, targetLevel)) {
            targetLevel = clamped;
            setChanged();
        }
    }

    public float getInsertionFraction() {
        return currentLevel;
    }

    public float getTargetInsertionFraction() {
        return targetLevel;
    }

    public int getSelectedColorIndex() {
        return selectedGroup != null ? selectedGroup.ordinal() : -1;
    }

    protected void forceLevel(float levelValue) {
        currentLevel = Mth.clamp(levelValue, 0.0F, 1.0F);
        targetLevel = currentLevel;
        syncBlockStateFromLevel();
    }

    protected void syncBlockStateFromLevel() {
        if (level == null) {
            return;
        }
        BlockState state = getBlockState();
        if (!state.hasProperty(BlockRBMKControlRod.INSERTION)) {
            return;
        }
        int stage = fractionToStage(currentLevel);
        if (state.getValue(BlockRBMKControlRod.INSERTION) != stage) {
            level.setBlock(worldPosition, state.setValue(BlockRBMKControlRod.INSERTION, stage), Block.UPDATE_ALL);
        }
    }

    private float deriveInsertionFromState() {
        BlockState state = getBlockState();
        if (state.hasProperty(BlockRBMKControlRod.INSERTION)) {
            return stageToFraction(state.getValue(BlockRBMKControlRod.INSERTION));
        }
        return 0.0F;
    }

    private static int fractionToStage(float fraction) {
        int max = BlockRBMKControlRod.MAX_INSERTION;
        if (max <= 0) {
            return 0;
        }
        return Mth.clamp(Math.round(fraction * max), 0, max);
    }

    private static float stageToFraction(int stage) {
        int max = BlockRBMKControlRod.MAX_INSERTION;
        if (max <= 0) {
            return 0.0F;
        }
        return Mth.clamp(stage / (float) max, 0.0F, 1.0F);
    }

    protected boolean hasControlRod() {
        ItemStack stack = items.get(CONTROL_ROD_SLOT);
        return isControlRodItem(stack);
    }

    public boolean isAutoColumn() {
        return level != null && net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()).getPath().contains("control_auto");
    }

    public AutoFunction getAutoFunction() {
        return autoFunction;
    }

    public double getLevelLower() {
        return levelLower;
    }

    public double getLevelUpper() {
        return levelUpper;
    }

    public double getHeatLower() {
        return heatLower;
    }

    public double getHeatUpper() {
        return heatUpper;
    }

    private static boolean isControlRodItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemRBMKControlRod;
    }

    private void onControlRodSlotChanged(ItemStack previous, ItemStack current) {
        boolean had = isControlRodItem(previous);
        boolean has = isControlRodItem(current);
        if (had == has) {
            return;
        }
        if (!has) {
            forceLevel(0.0F);
            setTargetLevel(0.0F);
        } else {
            currentLevel = Mth.clamp(targetLevel, 0.0F, 1.0F);
            syncBlockStateFromLevel();
        }
        setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previous = slot >= 0 && slot < items.size() ? items.get(slot).copy() : ItemStack.EMPTY;
        super.setItem(slot, stack);
        if (slot == CONTROL_ROD_SLOT) {
            onControlRodSlotChanged(previous, stack);
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == CONTROL_ROD_SLOT) {
            return isControlRodItem(stack);
        }
        return false;
    }


    public enum ControlGroup {
        RED,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE
    }

    public enum AutoFunction {
        LINEAR,
        QUAD_UP,
        QUAD_DOWN
    }
}
