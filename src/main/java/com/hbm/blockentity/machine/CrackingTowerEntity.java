package com.hbm.blockentity.machine;

import com.hbm.HBMKey;
import com.hbm.Inventory.fluid.ModFluids;
import com.hbm.Inventory.recipe.CrackingRecipes;
import com.hbm.api.Mode;
import com.hbm.api.fluid.BasicFluidHandler;
import com.hbm.api.fluid.VisitRestrictWrapper;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.base.DummyableBlockEntity;
import com.hbm.blockentity.base.TileProxyBase;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;
import java.util.Set;

/**
 * Simplified catalytic cracking tower that converts heavy feedstocks plus steam into lighter fractions.
 */
public class CrackingTowerEntity extends DummyableBlockEntity {

    private static final int FEED_TANK = 0;
    private static final int STEAM_TANK = 1;
    private static final int OUTPUT_PRIMARY = 2;
    private static final int OUTPUT_SECONDARY = 3;
    private static final int OUTPUT_SPENT = 4;

    private static final Vec3i PORT_FEED = new Vec3i(0, 0, -2);
    private static final Vec3i PORT_STEAM = new Vec3i(2, 0, 0);
    private static final Vec3i PORT_OUTPUT_PRIMARY = new Vec3i(0, 0, 2);
    private static final Vec3i PORT_OUTPUT_SECONDARY = new Vec3i(-2, 0, 0);
    private static final Vec3i PORT_SPENT = new Vec3i(1, 0, 2);

    private final BasicFluidHandler fluidHandler;
    private int tickCounter;

    public CrackingTowerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityType.CRACKING_TOWER_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(0, ItemStack.EMPTY);
        this.slotModes = List.of();
        this.fluidHandler = new BasicFluidHandler()
                .addTank(4_000, Mode.INPUT)
                .addTank(8_000, Mode.INPUT)
                .addTank(4_000, Mode.OUTPUT)
                .addTank(4_000, Mode.OUTPUT)
                .addTank(1_000, Mode.OUTPUT);
        this.capabilitiesContent.addCapability(ForgeCapabilities.FLUID_HANDLER, this.fluidHandler);
        this.multiblockData = MultiblockData.mapping.get(ModBlocks.machine_cracking_tower.get());
        this.isFormed = true;
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (!this.isFormed || level == null) {
            return;
        }
        tickCounter++;
        if (tickCounter % 5 == 0) {
            process();
        }
        sendUpdatePacket();
    }

    private void process() {
        List<FluidTank> tanks = this.fluidHandler.getFluidTanks();
        FluidTank feed = tanks.get(FEED_TANK);
        FluidTank steam = tanks.get(STEAM_TANK);
        if (feed.getFluidAmount() < CrackingRecipes.INPUT_CONSUMPTION || steam.getFluidAmount() < CrackingRecipes.STEAM_CONSUMPTION) {
            return;
        }
        var recipeOpt = CrackingRecipes.get(feed.getFluid().getFluid());
        if (recipeOpt.isEmpty()) {
            return;
        }
        CrackingRecipes.RecipeOutput recipe = recipeOpt.get();
        if (!hasSpace(recipe)) {
            return;
        }
        feed.drain(CrackingRecipes.INPUT_CONSUMPTION, IFluidHandler.FluidAction.EXECUTE);
        steam.drain(CrackingRecipes.STEAM_CONSUMPTION, IFluidHandler.FluidAction.EXECUTE);
        fillTank(OUTPUT_PRIMARY, recipe.left());
        fillTank(OUTPUT_SECONDARY, recipe.right());
        fillTank(OUTPUT_SPENT, new FluidStack(ModFluids.SPENT_STEAM.source().get(), CrackingRecipes.SPENT_STEAM_OUTPUT));
    }

    private boolean hasSpace(CrackingRecipes.RecipeOutput recipe) {
        return canAccept(OUTPUT_PRIMARY, recipe.left()) &&
                canAccept(OUTPUT_SECONDARY, recipe.right()) &&
                canAccept(OUTPUT_SPENT, new FluidStack(ModFluids.SPENT_STEAM.source().get(), CrackingRecipes.SPENT_STEAM_OUTPUT));
    }

    private boolean canAccept(int tankIndex, FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        FluidTank tank = this.fluidHandler.getFluidTanks().get(tankIndex);
        if (tank.isEmpty()) {
            return tank.getCapacity() >= stack.getAmount();
        }
        return tank.getFluid().isFluidEqual(stack) && tank.getSpace() >= stack.getAmount();
    }

    private void fillTank(int tankIndex, FluidStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        FluidTank tank = this.fluidHandler.getFluidTanks().get(tankIndex);
        tank.fill(stack.copy(), IFluidHandler.FluidAction.EXECUTE);
    }

    public BasicFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("block.hbm.machine_cracking_tower");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
    }

    @Override
    public CompoundTag getReducedUpdateTag() {
        CompoundTag tag = super.getReducedUpdateTag();
        tag.put(HBMKey.FLUIDS, fluidHandler.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdatePacket(CompoundTag tag) {
        super.handleUpdatePacket(tag);
        if (tag.contains(HBMKey.FLUIDS)) {
            fluidHandler.deserializeNBT(tag.getCompound(HBMKey.FLUIDS));
        }
    }

    @Override
    public void giveProxyCapabilities(Vec3i offset, TileProxyBase proxy, Capability<?> cap, Set<Direction> directions) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            VisitRestrictWrapper wrapper = null;
            if (offset.equals(PORT_FEED)) {
                wrapper = new VisitRestrictWrapper(this.fluidHandler, false, FEED_TANK);
            } else if (offset.equals(PORT_STEAM)) {
                wrapper = new VisitRestrictWrapper(this.fluidHandler, false, STEAM_TANK);
            } else if (offset.equals(PORT_OUTPUT_PRIMARY)) {
                wrapper = new VisitRestrictWrapper(this.fluidHandler, false, OUTPUT_PRIMARY);
            } else if (offset.equals(PORT_OUTPUT_SECONDARY)) {
                wrapper = new VisitRestrictWrapper(this.fluidHandler, false, OUTPUT_SECONDARY);
            } else if (offset.equals(PORT_SPENT)) {
                wrapper = new VisitRestrictWrapper(this.fluidHandler, false, OUTPUT_SPENT);
            }
            if (wrapper != null) {
                proxy.capabilitiesContent.addCapability(cap, wrapper, directions);
                return;
            }
        }
        super.giveProxyCapabilities(offset, proxy, cap, directions);
    }
}
