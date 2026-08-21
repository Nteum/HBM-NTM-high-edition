package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.AlkylationRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.block.machine.BlockAlkylation;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
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
 * 烷基化装置（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineAlkylation：氯甲烷/不饱和烃 → 芳香烃/石油气。
 * - 4 罐：0 输入、1 酸、2 输出1、3 输出2
 */
public class AlkylationEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000_000;
    public static final int USAGE = 4_000;
    public long power;

    private final FluidTank inputTank;
    private final FluidTank acidTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;

    public AlkylationEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(0);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(4, 8_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.acidTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.inputTank.setCapacity(8_000);
        this.acidTank.setCapacity(4_000);
        this.outputTank1.setCapacity(8_000);
        this.outputTank2.setCapacity(8_000);
        this.setMultiblockData(BlockAlkylation.class);
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        if (this.getLevel().getGameTime() % 2 == 0) alkylate();
        this.setChanged();
    }

    @Nullable
    private AlkylationRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.ALKYLATION.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void alkylate(){
        AlkylationRecipe recipe = getRecipe();
        if (recipe == null){
            outputTank1.setFluid(FluidStack.EMPTY);
            outputTank2.setFluid(FluidStack.EMPTY);
            return;
        }
        outputTank1.setFluid(new FluidStack(recipe.getOutput(0).getFluid(), outputTank1.getFluidAmount()));
        outputTank2.setFluid(new FluidStack(recipe.getOutput(1).getFluid(), outputTank2.getFluidAmount()));

        if (power < USAGE) return;
        if (inputTank.getFluidAmount() < recipe.getInputAmount()) return;
        if (recipe.acid != null && !recipe.acid.isEmpty() && acidTank.getFluidAmount() < recipe.acid.getAmount()) return;
        if (outputTank1.getFluidAmount() + recipe.getOutput(0).getAmount() > outputTank1.getCapacity()) return;
        if (outputTank2.getFluidAmount() + recipe.getOutput(1).getAmount() > outputTank2.getCapacity()) return;

        inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        if (recipe.acid != null && !recipe.acid.isEmpty()) acidTank.drain(recipe.acid.getAmount(), FluidTank.FluidAction.EXECUTE);
        outputTank1.fill(recipe.getOutput(0), FluidTank.FluidAction.EXECUTE);
        outputTank2.fill(recipe.getOutput(1), FluidTank.FluidAction.EXECUTE);
        power -= USAGE;
        energyContainer.setEnergy(power);
    }

    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getAcidTank(){ return acidTank; }
    public FluidTank getOutputTank1(){ return outputTank1; }
    public FluidTank getOutputTank2(){ return outputTank2; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
