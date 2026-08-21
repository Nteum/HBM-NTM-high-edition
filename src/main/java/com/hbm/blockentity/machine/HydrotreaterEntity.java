package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.HydrotreatingRecipe;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 加氢装置（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineHydrotreater：油 + 氢 → 脱硫油 + 酸气。
 * - 11 槽：0电池/1-2油/3-4氢/5-6脱硫油/7-8酸气/9罐类型/10催化剂
 * - 4 罐：0油、1氢、2脱硫油、3酸气
 * - 配方：HydrotreatingRecipe
 */
public class HydrotreaterEntity extends BEDummyable {
    public static final long MAX_POWER = 1_000_000;
    public long power;

    private final FluidTank oilTank;
    private final FluidTank hydrogenTank;
    private final FluidTank outputTank;
    private final FluidTank sourGasTank;

    public HydrotreaterEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(11);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(4, 64_000);
        this.oilTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.hydrogenTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.outputTank = ((BasicFluidHandler) fluidHandler).getFluidTank(2);
        this.sourGasTank = ((BasicFluidHandler) fluidHandler).getFluidTank(3);
        this.outputTank.setCapacity(24_000);
        this.sourGasTank.setCapacity(24_000);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_HYDROTREATER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(0));
        this.power = energyContainer.getEnergy();

        if (this.getLevel().getGameTime() % 2 == 0) reform();
    }

    @Nullable
    private HydrotreatingRecipe getRecipe(){
        if (oilTank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.HYDROTREATER.type().get()).stream()
                .filter(r -> r.matchesInput(oilTank.getFluid()))
                .findFirst().orElse(null);
    }

    private void reform(){
        HydrotreatingRecipe recipe = getRecipe();
        if (recipe == null) return;
        if (power < 20_000) return;
        if (oilTank.getFluidAmount() < 100) return;
        if (hydrogenTank.getFluidAmount() < recipe.hydrogen.getAmount()) return;

        if (outputTank.getFluidAmount() + recipe.output.getAmount() > outputTank.getCapacity()) return;
        if (sourGasTank.getFluidAmount() + recipe.sourGas.getAmount() > sourGasTank.getCapacity()) return;

        oilTank.drain(100, FluidTank.FluidAction.EXECUTE);
        hydrogenTank.drain(recipe.hydrogen.getAmount(), FluidTank.FluidAction.EXECUTE);
        outputTank.fill(recipe.output.copy(), FluidTank.FluidAction.EXECUTE);
        sourGasTank.fill(recipe.sourGas.copy(), FluidTank.FluidAction.EXECUTE);
        power -= 20_000;
        energyContainer.setEnergy(power);
        this.setChanged();
    }

    public List<HydrotreatingRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.HYDROTREATER.type().get()).stream().toList();
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getOilTank(){ return oilTank; }
    public FluidTank getHydrogenTank(){ return hydrogenTank; }
    public FluidTank getOutputTank(){ return outputTank; }
    public FluidTank getSourGasTank(){ return sourGasTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.HydrotreaterMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(0));
    }
}
