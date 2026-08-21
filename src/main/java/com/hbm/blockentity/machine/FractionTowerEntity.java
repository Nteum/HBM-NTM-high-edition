package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.FractionRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * 分馏塔（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineFractionTower：重油 → 沥青/粘稠油。
 * - 3 罐：0 输入、1 输出1、2 输出2
 */
public class FractionTowerEntity extends BEDummyable {
    private final FluidTank inputTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;

    public FractionTowerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.fluidHandler = new BasicFluidHandler(3, 4_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.inputTank.setCapacity(4_000);
        this.outputTank1.setCapacity(4_000);
        this.outputTank2.setCapacity(4_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_FRACTION_TOWER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        setupTanks();
        if (this.getLevel().getGameTime() % 10 == 0) fractionate();
        this.setChanged();
    }

    private void setupTanks(){
        FractionRecipe recipe = getRecipe();
        if (recipe != null){
            outputTank1.setFluid(new FluidStack(recipe.getOutput(0).getFluid(), outputTank1.getFluidAmount()));
            outputTank2.setFluid(new FluidStack(recipe.getOutput(1).getFluid(), outputTank2.getFluidAmount()));
        } else {
            inputTank.setFluid(FluidStack.EMPTY);
            outputTank1.setFluid(FluidStack.EMPTY);
            outputTank2.setFluid(FluidStack.EMPTY);
        }
    }

    @Nullable
    private FractionRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.FRACTION.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void fractionate(){
        FractionRecipe recipe = getRecipe();
        if (recipe == null) return;
        int left = recipe.getOutput(0).getAmount();
        int right = recipe.getOutput(1).getAmount();

        if (inputTank.getFluidAmount() >= recipe.getInputAmount()
                && outputTank1.getFluidAmount() + left <= outputTank1.getCapacity()
                && outputTank2.getFluidAmount() + right <= outputTank2.getCapacity()){
            inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
            outputTank1.fill(recipe.getOutput(0), FluidTank.FluidAction.EXECUTE);
            outputTank2.fill(recipe.getOutput(1), FluidTank.FluidAction.EXECUTE);
        }
    }

    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getOutputTank1(){ return outputTank1; }
    public FluidTank getOutputTank2(){ return outputTank2; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
