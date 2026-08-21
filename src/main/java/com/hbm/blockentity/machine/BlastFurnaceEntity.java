package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.BlastFurnaceRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.fluid.HBMFluids;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 高炉（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineBlastFurnace：烧燃料 + 鼓风 → 2 输入物品合成。
 * - 5 槽：0 燃料、1-2 输入、3-4 输出
 * - 2 罐：0 鼓风输入、1 废气输出
 */
public class BlastFurnaceEntity extends BEDummyable {
    public static final int FUEL_RATE = 200 * 4;
    public static final int MAX_FUEL = 200 * 8 * 24;
    public static final int FLUE_GAS = 100;
    public static final int TANK_CAPACITY = 4_000;

    public float progress;
    public float speed;
    public int fuel;
    public boolean isProgressing;

    private final FluidTank airblastTank;
    private final FluidTank flueTank;
    private final RecipeManager.CachedCheck<net.minecraft.world.Container, BlastFurnaceRecipe> cachedCheck;

    public BlastFurnaceEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(5);
        this.fluidHandler = new BasicFluidHandler(2, TANK_CAPACITY);
        this.airblastTank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.flueTank = ((BasicFluidHandler) fluidHandler).getFluidTank(1);
        this.airblastTank.setCapacity(4_000);
        this.flueTank.setCapacity(1_000);
        this.cachedCheck = RecipeManager.createCheck(ModRecipes.BLAST.type().get());
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_BLAST_FURNACE.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        // 加燃料
        ItemStack fuelStack = items.getStackInSlot(0);
        int capacity = MAX_FUEL - fuel;
        if (!fuelStack.isEmpty()){
            int burnValue = ForgeHooks.getBurnTime(fuelStack, ModRecipes.BLAST.type().get());
            if (burnValue > 0 && burnValue <= capacity){
                this.fuel += burnValue;
                fuelStack.shrink(1);
                if (fuelStack.getCount() <= 0) items.setStackInSlot(0, ItemStack.EMPTY);
            }
        }

        this.speed = 0F;
        Optional<BlastFurnaceRecipe> recipe = getRecipe();

        if (recipe.isPresent() && this.fuel >= FUEL_RATE && canOutput(recipe.get())){
            this.speed = (float)Math.max(0.5, Math.min(0.5 + this.airblastTank.getFluidAmount() * 8F / this.airblastTank.getCapacity(), 5));
            this.isProgressing = true;
            this.progress += speed;

            if (this.progress >= 1.0F){
                this.process(recipe.get());
                this.progress = 0F;
                this.fuel -= FUEL_RATE;
                this.flueTank.fill(new FluidStack(HBMFluids.FLUE.source().get(), FLUE_GAS), FluidTank.FluidAction.EXECUTE);
                if (this.flueTank.getFluidAmount() > this.flueTank.getCapacity()){
                    this.flueTank.setFluid(new FluidStack(HBMFluids.FLUE.source().get(), this.flueTank.getCapacity()));
                }
            }
        } else {
            this.isProgressing = false;
            this.progress = 0F;
        }

        if (this.airblastTank.getFluidAmount() > 0){
            this.airblastTank.setFluid(new FluidStack(this.airblastTank.getFluid().getFluid(), (int)(this.airblastTank.getFluidAmount() * 0.95)));
        }
        this.setChanged();
    }

    private Optional<BlastFurnaceRecipe> getRecipe(){
        return cachedCheck.getRecipeFor(new net.minecraft.world.SimpleContainer(items.getStackInSlot(1), items.getStackInSlot(2)), this.getLevel());
    }

    private boolean canOutput(BlastFurnaceRecipe recipe){
        ItemStack result = recipe.getResultItem(this.getLevel().registryAccess());
        for (int i = 3; i < 5; i++){
            ItemStack slot = items.getStackInSlot(i);
            if (slot.isEmpty()) return true;
            if (!slot.is(result.getItem())) continue;
            if (slot.getCount() + result.getCount() <= slot.getMaxStackSize()) return true;
        }
        return false;
    }

    private void process(BlastFurnaceRecipe recipe){
        ItemStack result = recipe.getResultItem(this.getLevel().registryAccess());
        boolean placed = false;
        for (int i = 3; i < 5; i++){
            ItemStack slot = items.getStackInSlot(i);
            if (slot.isEmpty()){
                items.setStackInSlot(i, result.copy());
                placed = true;
                break;
            } else if (slot.is(result.getItem()) && slot.getCount() + result.getCount() <= slot.getMaxStackSize()){
                slot.grow(result.getCount());
                placed = true;
                break;
            }
        }
        if (!placed) return;
        items.extractItem(1, 1, false);
        items.extractItem(2, 1, false);
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getAirblastTank(){ return airblastTank; }
    public FluidTank getFlueTank(){ return flueTank; }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.BlastFurnaceMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
