package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.ReformingRecipe;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 催化重整器（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineCatalyticReformer：石脑油/加热油 → 重整产物/石油/氢气。
 * - 11 槽：0 电池、1-8 桶出入、9 罐ID、10 催化剂
 * - 4 罐：0 输入、1-3 输出
 */
public class CatalyticReformerEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000_000;
    public static final int USAGE = 20_000;
    public long power;

    private final FluidTank inputTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;
    private final FluidTank outputTank3;

    public CatalyticReformerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(11);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(4, 64_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.outputTank3 = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.inputTank.setCapacity(64_000);
        this.outputTank1.setCapacity(24_000);
        this.outputTank2.setCapacity(24_000);
        this.outputTank3.setCapacity(24_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_CATALYTIC_REFORMER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        reform();
        this.setChanged();
    }

    @Nullable
    private ReformingRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.REFORMING.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void reform(){
        ReformingRecipe recipe = getRecipe();
        if (recipe == null){
            outputTank1.setFluid(net.minecraftforge.fluids.FluidStack.EMPTY);
            outputTank2.setFluid(net.minecraftforge.fluids.FluidStack.EMPTY);
            outputTank3.setFluid(net.minecraftforge.fluids.FluidStack.EMPTY);
            return;
        }
        outputTank1.setFluid(new net.minecraftforge.fluids.FluidStack(recipe.getOutput(0).getFluid(), outputTank1.getFluidAmount()));
        outputTank2.setFluid(new net.minecraftforge.fluids.FluidStack(recipe.getOutput(1).getFluid(), outputTank2.getFluidAmount()));
        outputTank3.setFluid(new net.minecraftforge.fluids.FluidStack(recipe.getOutput(2).getFluid(), outputTank3.getFluidAmount()));

        if (power < USAGE) return;
        if (inputTank.getFluidAmount() < recipe.getInputAmount()) return;
        if (items.getStackInSlot(10).isEmpty() || !items.getStackInSlot(10).is(ModItems.CATALYTIC_CONVERTER.get())) return;
        if (outputTank1.getFluidAmount() + recipe.getOutput(0).getAmount() > outputTank1.getCapacity()) return;
        if (outputTank2.getFluidAmount() + recipe.getOutput(1).getAmount() > outputTank2.getCapacity()) return;
        if (outputTank3.getFluidAmount() + recipe.getOutput(2).getAmount() > outputTank3.getCapacity()) return;

        inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        outputTank1.fill(recipe.getOutput(0), FluidTank.FluidAction.EXECUTE);
        outputTank2.fill(recipe.getOutput(1), FluidTank.FluidAction.EXECUTE);
        outputTank3.fill(recipe.getOutput(2), FluidTank.FluidAction.EXECUTE);
        power -= USAGE;
        energyContainer.setEnergy(power);
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getOutputTank1(){ return outputTank1; }
    public FluidTank getOutputTank2(){ return outputTank2; }
    public FluidTank getOutputTank3(){ return outputTank3; }
    public List<ReformingRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.REFORMING.type().get()).stream().toList();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.CatalyticReformerMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
