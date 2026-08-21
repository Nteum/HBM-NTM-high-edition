package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.RadiolysisRecipe;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.item.misc.ItemRTGPellet;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.RTGUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 辐射裂解装置（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineRadiolysis：
 * - 15 槽：0-9 RTG 燃料棒、10-11 流体桶 IO、12-13 消毒 IO、14 电池
 * - 3 罐：0 输入、1 输出1、2 输出2
 * - RTG 热量 → 产 HE + 裂解流体；热量≥200 时每百 tick 消毒一次
 * - 配方：RadiolysisRecipe（流体 → 2 流体）
 */
public class RadiolysisEntity extends BEDummyable {
    public static final int MAX_POWER = 1_000_000;
    public static final int[] slot_rtg = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
    public static final int TANK_CAPACITY = 2_000;

    public long power;
    public int heat;

    private final FluidTank inputTank;
    private final FluidTank outputTank1;
    private final FluidTank outputTank2;

    public RadiolysisEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(15);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(3, TANK_CAPACITY);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.outputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_RADIOLYSIS.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(14));
        this.power = energyContainer.getEnergy();

        this.heat = RTGUtil.updateRTGs(this.items, slot_rtg);
        power += heat * 10L;
        if (power > MAX_POWER) power = MAX_POWER;
        energyContainer.setEnergy(power);

        if (heat > 100) {
            int crackTime = (int) Math.max(-0.1 * (heat - 100) + 30, 5);
            if (this.getLevel().getGameTime() % crackTime == 0)
                crack();

            if (heat >= 200 && this.getLevel().getGameTime() % 100 == 0)
                sterilize();
        }
        this.setChanged();
    }

    @Nullable
    private RadiolysisRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.RADIOLYSIS.type().get()).stream()
                .filter(r -> r.matchesInput(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void crack(){
        RadiolysisRecipe recipe = getRecipe();
        if (recipe == null) return;

        int left = recipe.getOutput1().getAmount();
        int right = recipe.getOutput2().getAmount();
        if (inputTank.getFluidAmount() >= recipe.getInputAmount()
                && outputTank1.getFluidAmount() + left <= outputTank1.getCapacity()
                && outputTank2.getFluidAmount() + right <= outputTank2.getCapacity()) {
            inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
            outputTank1.fill(recipe.getOutput1(), FluidTank.FluidAction.EXECUTE);
            outputTank2.fill(recipe.getOutput2(), FluidTank.FluidAction.EXECUTE);
            this.setChanged();
        }
    }

    private void sterilize(){
        ItemStack in = items.getStackInSlot(12);
        if (in.isEmpty()) return;
        ItemStack out = items.getStackInSlot(13);
        if (out.isEmpty()){
            items.setStackInSlot(13, in.copyWithCount(1));
            in.shrink(1);
            if (in.getCount() <= 0) items.setStackInSlot(12, ItemStack.EMPTY);
        } else if (ItemStack.isSameItemSameTags(out, in) && out.getCount() < out.getMaxStackSize()){
            out.grow(1);
            in.shrink(1);
            if (in.getCount() <= 0) items.setStackInSlot(12, ItemStack.EMPTY);
        }
        this.setChanged();
    }

    public List<RadiolysisRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.RADIOLYSIS.type().get()).stream().toList();
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getOutputTank1(){ return outputTank1; }
    public FluidTank getOutputTank2(){ return outputTank2; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.RadiolysisMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
