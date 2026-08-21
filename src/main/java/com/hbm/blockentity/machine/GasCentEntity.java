package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.GasCentrifugeRecipe;
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
 * 气体离心机（core 体系多方块机器，简化版）。
 * 移植自旧版 TileEntityMachineGasCent（去掉 PseudoFluidType 链式富集，改为流体 → 物品）。
 * - 7 槽：0-3 输出、4 电池、5 罐ID、6 升级
 * - 1 罐：UF6/PUF6 输入
 */
public class GasCentEntity extends BEDummyable {
    public static final long MAX_POWER = 100_000;
    public static final int TANK_CAPACITY = 8_000;
    public long power;
    public int progress;
    public boolean isProgressing;

    private final FluidTank tank;

    public GasCentEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(7);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(1, TANK_CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_GASCENT.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(4));
        this.power = energyContainer.getEnergy();

        GasCentrifugeRecipe recipe = getRecipe();
        if (recipe != null && canEnrich(recipe)){
            isProgressing = true;
            progress++;
            this.energyContainer.extract(200, false);
            this.power = energyContainer.getEnergy();
            if (power < 0) power = 0;

            if (progress >= recipe.processingSpeed){
                enrich(recipe);
                progress = 0;
            }
        } else {
            isProgressing = false;
            progress = 0;
        }
        this.setChanged();
    }

    @Nullable
    private GasCentrifugeRecipe getRecipe(){
        if (tank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.GAS_CENTRIFUGE.type().get()).stream()
                .filter(r -> r.matchesInput(tank.getFluid()))
                .findFirst().orElse(null);
    }

    private boolean canEnrich(GasCentrifugeRecipe recipe){
        if (power < 200) return false;
        if (tank.getFluidAmount() < recipe.getInputAmount()) return false;
        // 检查输出槽是否有空间
        for (ItemStack out : recipe.getOutputs()){
            if (!canAddItem(out)) return false;
        }
        return true;
    }

    private boolean canAddItem(ItemStack stack){
        for (int i = 0; i < 4; i++){
            ItemStack slot = items.getStackInSlot(i);
            if (slot.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() + stack.getCount() <= slot.getMaxStackSize()) return true;
        }
        return false;
    }

    private void enrich(GasCentrifugeRecipe recipe){
        tank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
        for (ItemStack out : recipe.getOutputs()){
            for (int i = 0; i < 4; i++){
                ItemStack slot = items.getStackInSlot(i);
                if (slot.isEmpty()){
                    items.setStackInSlot(i, out.copy());
                    break;
                } else if (ItemStack.isSameItemSameTags(slot, out) && slot.getCount() + out.getCount() <= slot.getMaxStackSize()){
                    slot.grow(out.getCount());
                    break;
                }
            }
        }
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getTank(){ return tank; }
    public List<GasCentrifugeRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.GAS_CENTRIFUGE.type().get()).stream().toList();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.GasCentMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
