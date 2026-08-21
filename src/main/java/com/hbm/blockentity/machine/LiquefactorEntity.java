package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.LiquefactionRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.blockentity.HBMTiles;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 工业液化机（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineLiquefactor：用电能把固体物品液化为流体。
 * - 4 槽：0 输入（可液化物品）、1 电池、2/3 升级
 * - 能量：HBMEnergyHandler（BasicEnergyHandler）
 * - 流体：1 个输出罐
 * - 配方：LiquefactionRecipe（AutoRecipe 子类）
 */
public class LiquefactorEntity extends BEDummyable {
    public static final long MAX_POWER = 100_000;
    public static final int USAGE_BASE = 250;
    public static final int PROCESS_TIME_BASE = 100;
    public static final int TANK_CAPACITY = 24_000;

    public long power;
    public int progress;
    public int usage;
    public int processTime;

    private final FluidTank tank;
    private final RecipeManager.CachedCheck<net.minecraft.world.Container, LiquefactionRecipe> cachedCheck;

    public LiquefactorEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(4);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(1, TANK_CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.cachedCheck = RecipeManager.createCheck(ModRecipes.LIQUEFACTOR.type().get());
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_LIQUEFACTOR.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        // 电池充电
        TransmitUtils.dischargeItem(this, items.getStackInSlot(1));
        this.power = energyContainer.getEnergy();

        // 升级
        int speed = 0, powerUp = 0;
        for (int i = 2; i <= 3; i++){
            ItemStack up = items.getStackInSlot(i);
            if (up.is(com.hbm.registries.ModTags.Items.UPGRADE)){
                if (up.getItem() instanceof com.hbm.item.machine.ItemMachineUpgrade upgrade){
                    if (upgrade.type == com.hbm.item.machine.ItemMachineUpgrade.UpgradeType.SPEED) speed += upgrade.tier;
                    if (upgrade.type == com.hbm.item.machine.ItemMachineUpgrade.UpgradeType.POWER) powerUp += upgrade.tier;
                }
            }
        }
        this.processTime = PROCESS_TIME_BASE - (PROCESS_TIME_BASE / 4) * speed;
        this.usage = (USAGE_BASE + USAGE_BASE * speed) / (powerUp + 1);
        if (this.processTime <= 0) this.processTime = 1;

        if (canProcess()) process();
        else progress = 0;

        this.setChanged();
    }

    @Nullable
    private LiquefactionRecipe getRecipe(){
        if (items.getStackInSlot(0).isEmpty()) return null;
        return cachedCheck.getRecipeFor(new net.minecraft.world.SimpleContainer(items.getStackInSlot(0)), this.getLevel()).orElse(null);
    }

    private boolean canProcess(){
        if (power < usage) return false;
        LiquefactionRecipe recipe = getRecipe();
        if (recipe == null) return false;
        FluidStack out = recipe.getOutputFluid();
        if (!tank.getFluid().isEmpty() && !tank.getFluid().isFluidEqual(out)) return false;
        return out.getAmount() + tank.getFluidAmount() <= tank.getCapacity();
    }

    private void process(){
        power -= usage;
        energyContainer.setEnergy(power);
        progress++;

        if (progress >= processTime){
            LiquefactionRecipe recipe = getRecipe();
            if (recipe != null){
                FluidStack out = recipe.getOutputFluid();
                tank.fill(out, FluidTank.FluidAction.EXECUTE);
                ItemStack input = items.getStackInSlot(0);
                input.shrink(1);
                if (input.getCount() <= 0) items.setStackInSlot(0, ItemStack.EMPTY);
            }
            progress = 0;
            this.setChanged();
        }
    }

    public List<LiquefactionRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.LIQUEFACTOR.type().get()).stream().toList();
    }

    public MachineItemHandler getItemHandler(){
        return this.items;
    }

    public FluidTank getTank(){
        return this.tank;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.LiquefactorMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
