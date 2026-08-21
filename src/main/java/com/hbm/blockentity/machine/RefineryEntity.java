package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RefineryRecipe;
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
 * 炼油厂（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineRefinery：热油 → 重油/石脑油/轻油/石油气。
 * - 13 槽：0电池、1-2输入桶、3-4重油、5-6石脑油、7-8轻油、9-10石油气、11罐类型、12空
 * - 5 罐：0输入（热油）、1重油、2石脑油、3轻油、4石油气
 * - 简化：移除过压爆炸/火灾，保留核心精炼
 */
public class RefineryEntity extends BEDummyable {
    public static final long MAX_POWER = 100_000;
    public long power;

    private final FluidTank inputTank;
    private final FluidTank heavyTank;
    private final FluidTank naphthaTank;
    private final FluidTank lightTank;
    private final FluidTank petroleumTank;

    public RefineryEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(13);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(5, 64_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.heavyTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.naphthaTank = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.lightTank = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.petroleumTank = ((BasicFluidHandler) fluidHandler).getFluidTank(4);
        for (int i = 1; i < 5; i++) ((BasicFluidHandler) fluidHandler).getFluidTank(i).setCapacity(24_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_REFINERY.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        refine();
        this.setChanged();
    }

    @Nullable
    private RefineryRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.REFINERY.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void refine(){
        RefineryRecipe recipe = getRecipe();
        if (recipe == null) return;
        if (power < 500) return;
        if (inputTank.getFluidAmount() < recipe.getInputAmount()) return;

        // 检查所有输出罐容量
        FluidTank[] outputs = {heavyTank, naphthaTank, lightTank, petroleumTank};
        for (int i = 0; i < outputs.length; i++){
            FluidStack out = recipe.getOutput(i);
            if (out.isEmpty()) continue;
            if (outputs[i].getFluidAmount() + out.getAmount() > outputs[i].getCapacity()) return;
        }

        inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        for (int i = 0; i < outputs.length; i++){
            FluidStack out = recipe.getOutput(i);
            if (out.isEmpty()) continue;
            outputs[i].fill(out, FluidTank.FluidAction.EXECUTE);
        }
        power -= 500;
        energyContainer.setEnergy(power);
        this.setChanged();
    }

    public List<RefineryRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.REFINERY.type().get()).stream().toList();
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getHeavyTank(){ return heavyTank; }
    public FluidTank getNaphthaTank(){ return naphthaTank; }
    public FluidTank getLightTank(){ return lightTank; }
    public FluidTank getPetroleumTank(){ return petroleumTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.RefineryMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
