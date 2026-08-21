package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.CokerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.blockentity.interfaces.IHeatSource;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 焦化装置（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineCoker：热裂化重油 → 焦炭 + 副产品。
 * - 2 槽：0 罐类型、1 输出
 * - 2 罐：0 输入（重油）、1 输出（油焦/副产品）
 * - 热源：从下方方块拉取热量
 * - 配方：CokerRecipe（流体→物品+流体）
 */
public class CokerEntity extends BEDummyable {
    public static final int PROCESS_TIME = 20_000;
    public static final int MAX_HEAT = 100_000;
    public static final double DIFFUSION = 0.25D;

    public boolean wasOn;
    public int progress;
    public int heat;

    private final FluidTank inputTank;
    private final FluidTank outputTank;

    public CokerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(2);
        this.fluidHandler = new BasicFluidHandler(2, 16_000);
        this.inputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.outputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.inputTank.setCapacity(16_000);
        this.outputTank.setCapacity(8_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_COKER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        tryPullHeat();

        this.wasOn = false;
        if (canProcess()){
            int burn = heat / 100;
            if (burn > 0){
                wasOn = true;
                progress += burn;
                heat -= burn;
                if (progress >= PROCESS_TIME){
                    progress -= PROCESS_TIME;
                    process();
                    this.setChanged();
                }
            }
        }
    }

    private void tryPullHeat(){
        BlockEntity below = this.getLevel().getBlockEntity(this.worldPosition.below());
        if (below instanceof IHeatSource source){
            int diff = source.getHeatStored() - this.heat;
            int toPull = (int) Math.min(diff, this.MAX_HEAT - this.heat);
            if (toPull > 0){
                source.useUpHeat(toPull);
                this.heat += toPull;
            }
        }
        // 向四周扩散
        for (Direction dir : Direction.values()){
            int target = this.getLevel().getBlockEntity(this.worldPosition.relative(dir)) instanceof CokerEntity ? heat : heat;
            int transfer = (int) (heat * DIFFUSION);
            if (transfer > 0 && heat > target){
                heat -= transfer;
            }
        }
    }

    @Nullable
    private CokerRecipe getRecipe(){
        if (inputTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.COKER.type().get()).stream()
                .filter(r -> r.matchesFluid(inputTank.getFluid()))
                .findFirst().orElse(null);
    }

    private boolean canProcess(){
        CokerRecipe recipe = getRecipe();
        if (recipe == null) return false;
        if (recipe.getInputAmount() > inputTank.getFluidAmount()) return false;
        ItemStack out = items.getStackInSlot(1);
        ItemStack result = recipe.getOutputItem();
        if (!result.isEmpty()){
            if (!out.isEmpty()){
                if (!out.is(result.getItem())) return false;
                if (out.getCount() + result.getCount() > out.getMaxStackSize()) return false;
            }
        }
        return true;
    }

    private void process(){
        CokerRecipe recipe = getRecipe();
        if (recipe == null) return;
        inputTank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        ItemStack result = recipe.getOutputItem();
        if (!result.isEmpty()){
            ItemStack out = items.getStackInSlot(1);
            if (out.isEmpty()) items.setStackInSlot(1, result);
            else out.grow(result.getCount());
        }
        FluidStack byproduct = recipe.getByproduct();
        if (!byproduct.isEmpty()){
            outputTank.setFluid(new FluidStack(byproduct.getFluid(), outputTank.getFluidAmount() + byproduct.getAmount()));
        }
    }

    public List<CokerRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.COKER.type().get()).stream().toList();
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank(){ return inputTank; }
    public FluidTank getOutputTank(){ return outputTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return null; // 无 GUI
    }
}
