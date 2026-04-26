package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.HBMLang;
import com.hbm.Inventory.recipe.ShredderRecipe;
import com.hbm.api.Mode;
import com.hbm.api.energy.BasicEnergyContainer;
import com.hbm.api.energy.HybridEnergyStorage;
import com.hbm.api.energy.ProxyEnergyHandler;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.registries.HBMCaps;
import com.hbm.gui.menu.ShredderMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShredderEntity extends BaseMachineBlockEntity {
    public static final int DEFAULT_PROCESS_TICKS = 60;
    public static final long MAX_POWER = 10_000;
    private static final int POWER_PER_TICK = 5;
    private static final int INPUT_SLOTS = 9;
    private static final int OUTPUT_START = 9;
    private static final int OUTPUT_END = 26;
    private static final int LEFT_BLADE_SLOT = 27;
    private static final int RIGHT_BLADE_SLOT = 28;
    private static final int BATTERY_SLOT = 29;

    private final BasicEnergyContainer energyContainer = new BasicEnergyContainer(MAX_POWER, MAX_POWER, MAX_POWER);
    private final HybridEnergyStorage forgeEnergy = new HybridEnergyStorage(this.energyContainer);
    private final SimpleContainer recipeContainer = new SimpleContainer(1);
    private int progress;
    private int processTarget = DEFAULT_PROCESS_TICKS;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> processTarget;
                case 2 -> (int) Math.min(Integer.MAX_VALUE, energyContainer.getEnergy());
                case 3 -> (int) Math.min(Integer.MAX_VALUE, energyContainer.getCapacity());
                case 4 -> getBladeState(LEFT_BLADE_SLOT);
                case 5 -> getBladeState(RIGHT_BLADE_SLOT);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // client-only, noop
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public ShredderEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.SHREDDER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(30, ItemStack.EMPTY);
        this.slotModes = new ModeBuilder()
                .addModes(INPUT_SLOTS, Mode.BOTH)
                .addModes(OUTPUT_END - OUTPUT_START + 1, Mode.OUTPUT)
                .addModes(2, Mode.BOTH) // blades
                .addModes(1, Mode.BOTH) // battery
                .get();
        this.capabilitiesContent.addCapability(HBMCaps.LONG_ENERGY, new ProxyEnergyHandler(this.energyContainer));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ENERGY, this.forgeEnergy);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (level == null) return;

        TransmitUtils.dischargeItem(this, this.items.get(BATTERY_SLOT));

        boolean canProcess = canProcess();
        if (canProcess && energyContainer.extract(POWER_PER_TICK, true) == POWER_PER_TICK) {
            progress++;
            energyContainer.extract(POWER_PER_TICK, false);
            if (progress >= processTarget) {
                progress = 0;
                processInputs();
                damageBlade(LEFT_BLADE_SLOT);
                damageBlade(RIGHT_BLADE_SLOT);
            }
            setChanged(level, worldPosition, getBlockState());
        } else {
            if (progress != 0) {
                progress = 0;
                setChanged(level, worldPosition, getBlockState());
            }
            processTarget = DEFAULT_PROCESS_TICKS;
        }
    }

    private boolean canProcess() {
        if (level == null) return false;
        if (!hasHealthyBlades()) {
            processTarget = DEFAULT_PROCESS_TICKS;
            return false;
        }
        if (energyContainer.getEnergy() < POWER_PER_TICK) return false;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;
            Optional<ShredderRecipe> recipe = getRecipe(stack);
            if (recipe.isEmpty()) continue;
            if (!hasOutputSpace(recipe.get().getOutput())) continue;
            if (stack.getCount() >= recipe.get().getIngredientCount()) {
                processTarget = Math.max(1, recipe.get().getProcessTime());
                return true;
            }
        }
        processTarget = DEFAULT_PROCESS_TICKS;
        return false;
    }

    private void processInputs() {
        if (level == null) return;
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;
            Optional<ShredderRecipe> recipeOptional = getRecipe(stack);
            if (recipeOptional.isEmpty()) continue;
            ShredderRecipe recipe = recipeOptional.get();
            int required = recipe.getIngredientCount();
            if (stack.getCount() < required) continue;
            ItemStack output = recipe.getOutput();
            if (!hasOutputSpace(output)) continue;
            if (addOutput(output.copy())) {
                stack.shrink(required);
                if (stack.isEmpty()) {
                    items.set(i, ItemStack.EMPTY);
                } else {
                    items.set(i, stack);
                }
            }
        }
    }

    private Optional<ShredderRecipe> getRecipe(ItemStack stack) {
        if (level == null || stack.isEmpty()) return Optional.empty();
        recipeContainer.setItem(0, stack);
        return level.getRecipeManager().getRecipeFor(ModRecipes.SHREDDER.type().get(), recipeContainer, level);
    }

    private boolean hasOutputSpace(ItemStack output) {
        for (int slot = OUTPUT_START; slot <= OUTPUT_END; slot++) {
            ItemStack existing = items.get(slot);
            if (existing.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(existing, output) && existing.getCount() + output.getCount() <= existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private boolean addOutput(ItemStack output) {
        for (int slot = OUTPUT_START; slot <= OUTPUT_END; slot++) {
            ItemStack existing = items.get(slot);
            if (existing.isEmpty()) {
                items.set(slot, output);
                return true;
            }
            if (ItemStack.isSameItemSameTags(existing, output)) {
                int transfer = Math.min(output.getCount(), existing.getMaxStackSize() - existing.getCount());
                if (transfer > 0) {
                    existing.grow(transfer);
                    output.shrink(transfer);
                    if (output.isEmpty()) {
                        items.set(slot, existing);
                        return true;
                    }
                }
            }
        }
        return output.isEmpty();
    }

    private boolean hasHealthyBlades() {
        int leftState = getBladeState(LEFT_BLADE_SLOT);
        int rightState = getBladeState(RIGHT_BLADE_SLOT);
        return leftState > 0 && leftState < 3 && rightState > 0 && rightState < 3;
    }

    private void damageBlade(int slot) {
        ItemStack blade = items.get(slot);
        if (blade.isEmpty() || !blade.isDamageableItem()) return;
        int damage = blade.getDamageValue() + 1;
        if (damage >= blade.getMaxDamage()) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            blade.setDamageValue(damage);
            items.set(slot, blade);
        }
    }

    private int getBladeState(int slot) {
        ItemStack blade = items.get(slot);
        if (blade.isEmpty()) return 0;
        if (!blade.isDamageableItem() || blade.getMaxDamage() == 0) return 1;
        if (blade.getDamageValue() < blade.getMaxDamage() / 2) return 1;
        if (blade.getDamageValue() < blade.getMaxDamage()) return 2;
        return 3;
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.progress = tag.getInt(HBMKey.PROGRESS);
        this.processTarget = tag.contains(HBMKey.DURATION) ? tag.getInt(HBMKey.DURATION) : DEFAULT_PROCESS_TICKS;
        if (tag.contains(HBMKey.ENERGY)) {
            this.energyContainer.deserializeNBT(tag.getCompound(HBMKey.ENERGY));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(HBMKey.PROGRESS, this.progress);
        tag.putInt(HBMKey.DURATION, this.processTarget);
        tag.put(HBMKey.ENERGY, this.energyContainer.serializeNBT());
    }

    @Override
    public BasicEnergyContainer getEnergyContainer() {
        return energyContainer;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable(HBMLang.SHREDDER.key());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ShredderMenu(containerId, inventory, this, containerData);
    }
}
