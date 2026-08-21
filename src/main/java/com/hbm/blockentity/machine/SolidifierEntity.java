package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.Inventory.recipe.SolidificationRecipe;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.item.machine.ItemMachineUpgrade;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 工业固化机（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineSolidifier：用电能把流体固化为物品。
 * - 5 槽：0 输出、1 电池、2/3 升级、4 罐类型标识
 * - 能量：HBMEnergyHandler
 * - 流体：1 个输入罐
 * - 配方：SolidificationRecipe（AutoRecipe，流体→物品）
 */
public class SolidifierEntity extends BEDummyable {
    public static final long MAX_POWER = 100_000;
    public static final int USAGE_BASE = 250;
    public static final int PROCESS_TIME_BASE = 100;
    public static final int TANK_CAPACITY = 24_000;

    public long power;
    public int progress;
    public int usage;
    public int processTime;

    private final FluidTank tank;

    public SolidifierEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(5);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(1, TANK_CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_SOLIDIFIER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(1));
        this.power = energyContainer.getEnergy();

        int speed = 0, powerUp = 0;
        for (int i = 2; i <= 3; i++){
            ItemStack up = items.getStackInSlot(i);
            if (up.getItem() instanceof ItemMachineUpgrade upgrade){
                if (upgrade.type == ItemMachineUpgrade.UpgradeType.SPEED) speed += upgrade.tier;
                if (upgrade.type == ItemMachineUpgrade.UpgradeType.POWER) powerUp += upgrade.tier;
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
    private SolidificationRecipe getRecipe(){
        if (tank.getFluid().isEmpty()) return null;
        return this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.SOLIDIFIER.type().get()).stream()
                .filter(r -> r.matches(tank.getFluid()))
                .findFirst().orElse(null);
    }

    private boolean canProcess(){
        if (power < usage) return false;
        SolidificationRecipe recipe = getRecipe();
        if (recipe == null) return false;
        if (recipe.getInputAmount() > tank.getFluidAmount()) return false;
        ItemStack out = items.getStackInSlot(0);
        ItemStack result = recipe.getOutput();
        if (!out.isEmpty()){
            if (!out.is(result.getItem())) return false;
            if (out.getCount() + result.getCount() > out.getMaxStackSize()) return false;
        }
        return true;
    }

    private void process(){
        power -= usage;
        energyContainer.setEnergy(power);
        progress++;

        if (progress >= processTime){
            SolidificationRecipe recipe = getRecipe();
            if (recipe != null){
                tank.drain(recipe.getInputAmount(), FluidTank.FluidAction.EXECUTE);
                ItemStack out = items.getStackInSlot(0);
                ItemStack result = recipe.getOutput();
                if (out.isEmpty()) items.setStackInSlot(0, result);
                else out.grow(result.getCount());
            }
            progress = 0;
            this.setChanged();
        }
    }

    public List<SolidificationRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.SOLIDIFIER.type().get()).stream().toList();
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
        return new com.hbm.gui.menu.SolidifierMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
