package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.CrackingRecipes;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 催化裂化塔（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineCatalyticCracker：重油/柴油 + 蒸汽 → 轻质馏分 + 废蒸汽。
 * - 5 罐：0 输入油、1 蒸汽、2 输出1、3 输出2、4 废蒸汽
 */
public class CatalyticCrackerEntity extends BEDummyable {
    private final FluidTank inputTank;
    private final FluidTank steamTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;
    private final FluidTank spentTank;

    public CatalyticCrackerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.fluidHandler = new BasicFluidHandler(5, 4_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.steamTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.spentTank = ((BasicFluidHandler) fluidHandler).getFluidTank(4);
        this.inputTank.setCapacity(4_000);
        this.steamTank.setCapacity(8_000);
        this.outputTank1.setCapacity(4_000);
        this.outputTank2.setCapacity(4_000);
        this.spentTank.setCapacity(800);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_CATALYTIC_CRACKER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        setupTanks();
        if (this.getLevel().getGameTime() % 5 == 0) crack();
        this.setChanged();
    }

    private void setupTanks(){
        Optional<CrackingRecipes.RecipeOutput> recipe = getRecipe();
        if (recipe.isPresent()){
            steamTank.setFluid(new FluidStack(HBMFluids.STEAM.source().get(), steamTank.getFluidAmount()));
            outputTank1.setFluid(new FluidStack(recipe.get().left().getFluid(), outputTank1.getFluidAmount()));
            outputTank2.setFluid(new FluidStack(recipe.get().right().getFluid(), outputTank2.getFluidAmount()));
            spentTank.setFluid(new FluidStack(HBMFluids.SPENTSTEAM.source().get(), spentTank.getFluidAmount()));
        } else {
            outputTank1.setFluid(FluidStack.EMPTY);
            outputTank2.setFluid(FluidStack.EMPTY);
            spentTank.setFluid(FluidStack.EMPTY);
        }
    }

    private Optional<CrackingRecipes.RecipeOutput> getRecipe(){
        if (inputTank.getFluid().isEmpty()) return Optional.empty();
        return CrackingRecipes.get(inputTank.getFluid().getFluid());
    }

    private void crack(){
        Optional<CrackingRecipes.RecipeOutput> recipe = getRecipe();
        if (recipe.isEmpty()) return;

        int left = recipe.get().left().getAmount();
        int right = recipe.get().right().getAmount();

        if (inputTank.getFluidAmount() >= 100 && steamTank.getFluidAmount() >= 200 && hasSpace(left, right)){
            inputTank.drain(100, FluidTank.FluidAction.EXECUTE);
            steamTank.drain(200, FluidTank.FluidAction.EXECUTE);
            outputTank1.fill(recipe.get().left(), FluidTank.FluidAction.EXECUTE);
            outputTank2.fill(recipe.get().right(), FluidTank.FluidAction.EXECUTE);
            spentTank.fill(new FluidStack(HBMFluids.SPENTSTEAM.source().get(), 2), FluidTank.FluidAction.EXECUTE);
        }
    }

    private boolean hasSpace(int left, int right){
        return outputTank1.getFluidAmount() + left <= outputTank1.getCapacity()
                && outputTank2.getFluidAmount() + right <= outputTank2.getCapacity()
                && spentTank.getFluidAmount() + 2 <= spentTank.getCapacity();
    }

    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getSteamTank(){ return steamTank; }
    public FluidTank getOutputTank1(){ return outputTank1; }
    public FluidTank getOutputTank2(){ return outputTank2; }
    public FluidTank getSpentTank(){ return spentTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
