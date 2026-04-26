package com.hbm.blockentity.machine.research;

import com.hbm.HBMKey;
import com.hbm.block.base.BlockDummyable;
import com.hbm.api.Mode;
import com.hbm.api.inventory.ModeBuilder;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.gui.menu.BreederReactorMenu;
import com.hbm.registries.ModBlocks;
import com.hbm.Inventory.recipe.BreederRecipes;
import com.hbm.Inventory.recipe.BreederRecipes.BreederRecipe;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BreederReactorBlockEntity extends DummyableBlockEntity implements MenuProvider {

    private static final int SLOT_INPUT = 0;
    private static final int SLOT_OUTPUT = 1;
    private static final double BASE_PROGRESS = 0.0025D;

    private final ContainerData containerData = new SimpleContainerData(2) {
        @Override
        public int get(int index) {
            return index == 0 ? flux : (int) Math.min(100, Math.round(progress * 100));
        }
    };

    private int flux;
    private double progress;

    public BreederReactorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.BREEDER_REACTOR_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_reactor_breeding.get());
        this.slotModes = new ModeBuilder().addModes(1, Mode.INPUT).addModes(1, Mode.OUTPUT).get();
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, this);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!isFormed || level == null) {
            running = false;
            flux = 0;
            return;
        }
        running = true;
        gatherFlux();
        process();
    }

    private void gatherFlux() {
        if (level == null) {
            flux = 0;
            return;
        }
        int total = 0;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() == ModBlocks.machine_research_reactor.get()) {
                BlockPos corePos = ((BlockDummyable) neighborState.getBlock()).getCore(neighborState, level, neighborPos);
                if (corePos != null) {
                    var blockEntity = level.getBlockEntity(corePos);
                    if (blockEntity instanceof ResearchReactorBlockEntity reactor) {
                        total += reactor.getFlux();
                    }
                }
            }
        }
        flux = total;
    }

    private void process() {
        ItemStack input = items.get(SLOT_INPUT);
        if (input.isEmpty()) {
            progress = 0.0D;
            return;
        }
        BreederRecipe recipe = BreederRecipes.getOutput(input);
        if (recipe == null || flux < recipe.flux()) {
            progress = 0.0D;
            return;
        }
        int targetSlot = SLOT_OUTPUT;
        ItemStack output = items.get(targetSlot);
        ItemStack produced = recipe.createOutput(input);
        if (!output.isEmpty() && (!ItemStack.isSameItemSameTags(output, produced) || output.getCount() >= output.getMaxStackSize())) {
            progress = 0.0D;
            return;
        }

        progress += BASE_PROGRESS * ((double) flux / recipe.flux());
        if (progress >= 1.0D) {
            progress = 0.0D;
            items.set(SLOT_INPUT, recipe.consumeInput(input));
            if (output.isEmpty()) {
                items.set(targetSlot, produced);
            } else {
                output.grow(produced.getCount());
            }
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(HBMKey.FLUX, flux);
        tag.putDouble("Progress", progress);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.flux = tag.getInt(HBMKey.FLUX);
        this.progress = tag.getDouble("Progress");
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            Containers.dropContents(level, pos, this);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.hbm.reactor_breeder");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new BreederReactorMenu(containerId, inventory, this, containerData);
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("container.hbm.reactor_breeder");
    }

    public int getFlux() {
        return flux;
    }

    public int getProgressScaled(int pixels) {
        return (int) Math.min(pixels, Math.round(progress * pixels));
    }

    public int getProgressPercent() {
        return (int) Math.min(100, Math.round(progress * 100));
    }
}
