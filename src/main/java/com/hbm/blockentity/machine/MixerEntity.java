package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.MixerRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 混合机（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineMixer：至多 2 种流体 + 可选固体 → 输出流体。
 * - 5 槽：0 电池、1 固体输入、2 流体罐ID、3-4 升级
 * - 3 罐：0 输入1、1 输入2、2 输出
 */
public class MixerEntity extends BEDummyable {
    public static final long MAX_POWER = 10_000;
    public static final int TANK_CAPACITY = 24_000;
    public static final int USAGE_BASE = 50;
    public static final int PROCESS_TIME_BASE = 50;

    public long power;
    public int progress;
    public int processTime;
    public int recipeIndex;
    public boolean wasOn;

    private final FluidTank inputTank1;
    private final FluidTank inputTank2;
    private final FluidTank outputTank;

    public MixerEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(5);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(3, TANK_CAPACITY);
        this.inputTank1 = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.inputTank2 = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.inputTank1.setCapacity(16_000);
        this.inputTank2.setCapacity(16_000);
        this.outputTank.setCapacity(24_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_MIXER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        this.processTime = PROCESS_TIME_BASE;
        this.wasOn = canProcess();
        if (wasOn){
            progress++;
            this.energyContainer.extract(getConsumption(), false);
            this.power = energyContainer.getEnergy();

            if (progress >= processTime){
                process();
                progress = 0;
            }
        } else {
            progress = 0;
        }
        this.setChanged();
    }

    private int getConsumption(){
        return USAGE_BASE;
    }

    private boolean canProcess(){
        if (power < getConsumption()) return false;
        if (outputTank.getFluid().isEmpty()) return false;

        MixerRecipe recipe = getCurrentRecipe();
        if (recipe == null) return false;
        if (recipe.input1 != null && inputTank1.getFluidAmount() < recipe.input1.getAmount()) return false;
        if (recipe.input2 != null && inputTank2.getFluidAmount() < recipe.input2.getAmount()) return false;
        if (recipe.solidInput != null){
            ItemStack solid = items.getStackInSlot(1);
            if (solid.isEmpty() || !recipe.solidInput.test(solid) || solid.getCount() < recipe.solidInput.value.count) return false;
        }
        return outputTank.getFluidAmount() + recipe.getOutputAmount() <= outputTank.getCapacity();
    }

    @Nullable
    private MixerRecipe getCurrentRecipe(){
        if (outputTank.getFluid().isEmpty()) return null;
        net.minecraftforge.fluids.FluidStack outFluid = outputTank.getFluid();
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.MIXER.type().get()).stream()
                .filter(r -> r.outputAmount > 0)
                .filter(r -> {
                    // 输出流体与配方输出量匹配（配方本身不带输出流体类型，按输出罐当前类型处理）
                    boolean in1 = r.input1 == null || (inputTank1.getFluid().isFluidEqual(r.input1) && inputTank1.getFluidAmount() >= r.input1.getAmount());
                    boolean in2 = r.input2 == null || (inputTank2.getFluid().isFluidEqual(r.input2) && inputTank2.getFluidAmount() >= r.input2.getAmount());
                    boolean solid = r.solidInput == null || !items.getStackInSlot(1).isEmpty();
                    return in1 && in2 && solid;
                })
                .findFirst().orElse(null);
    }

    private void process(){
        MixerRecipe recipe = getCurrentRecipe();
        if (recipe == null) return;
        if (recipe.input1 != null) inputTank1.drain(recipe.input1.getAmount(), FluidTank.FluidAction.EXECUTE);
        if (recipe.input2 != null) inputTank2.drain(recipe.input2.getAmount(), FluidTank.FluidAction.EXECUTE);
        if (recipe.solidInput != null){
            ItemStack solid = items.getStackInSlot(1);
            solid.shrink(recipe.solidInput.value.count);
            if (solid.getCount() <= 0) items.setStackInSlot(1, ItemStack.EMPTY);
        }
        // 输出类型沿用输出罐当前的流体
        outputTank.fill(new net.minecraftforge.fluids.FluidStack(outputTank.getFluid().getFluid(), recipe.getOutputAmount()), FluidTank.FluidAction.EXECUTE);
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getInputTank1(){ return inputTank1; }
    public FluidTank getInputTank2(){ return inputTank2; }
    public FluidTank getOutputTank(){ return outputTank; }
    public List<MixerRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.MIXER.type().get()).stream().toList();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.MixerMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
