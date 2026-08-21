package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.VacuumRefineryRecipe;
import com.hbm.api.energy.TransmitUtils;
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

import java.util.List;

/**
 * 真空蒸馏塔（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineVacuumDistill：原油 → 重油/重整产物/轻油/酸性气。
 * - 12 槽：0 电池、1-10 桶出入、11 罐ID
 * - 5 罐：0 输入、1-4 输出
 */
public class VacuumDistillEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000_000;
    public static final int USAGE = 10_000;
    public long power;
    public boolean isOn;

    private final FluidTank inputTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;
    private final FluidTank outputTank3;
    private final FluidTank outputTank4;

    public VacuumDistillEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(12);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(5, 64_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.outputTank3 = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.outputTank4 = ((BasicFluidHandler) fluidHandler).getFluidTank(4);
        this.inputTank.setCapacity(64_000);
        for (int i = 1; i < 5; i++) ((BasicFluidHandler) fluidHandler).getFluidTank(i).setCapacity(24_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_VACUUM_DISTILL.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        this.isOn = false;
        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        refine();
        this.setChanged();
    }

    @Nullable
    private VacuumRefineryRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.VACUUM_REFINERY.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void refine(){
        VacuumRefineryRecipe recipe = getRecipe();
        if (recipe == null){
            for (int i = 1; i < 5; i++) ((BasicFluidHandler) fluidHandler).getFluidTank(i).setFluid(net.minecraftforge.fluids.FluidStack.EMPTY);
            return;
        }
        FluidTank[] outputs = {outputTank1, outputTank2, outputTank3, outputTank4};
        for (int i = 0; i < outputs.length; i++){
            outputs[i].setFluid(new FluidStack(recipe.getOutput(i).getFluid(), outputs[i].getFluidAmount()));
        }

        if (power < USAGE) return;
        if (inputTank.getFluidAmount() < recipe.getInputAmount()) return;
        for (int i = 0; i < outputs.length; i++){
            if (outputs[i].getFluidAmount() + recipe.getOutput(i).getAmount() > outputs[i].getCapacity()) return;
        }

        this.isOn = true;
        inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        for (int i = 0; i < outputs.length; i++){
            outputs[i].fill(recipe.getOutput(i), FluidTank.FluidAction.EXECUTE);
        }
        power -= USAGE;
        energyContainer.setEnergy(power);
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getOutputTank(int index){
        return switch (index){
            case 0 -> outputTank1;
            case 1 -> outputTank2;
            case 2 -> outputTank3;
            case 3 -> outputTank4;
            default -> outputTank1;
        };
    }
    public List<VacuumRefineryRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.VACUUM_REFINERY.type().get()).stream().toList();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.VacuumDistillMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
