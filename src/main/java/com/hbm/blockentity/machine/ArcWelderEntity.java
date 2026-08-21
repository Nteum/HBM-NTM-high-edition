package com.hbm.blockentity.machine;

import com.hbm.Inventory.recipe.ArcWelderRecipe;
import com.hbm.Inventory.recipe.ModRecipes;
import com.hbm.api.energy.TransmitUtils;
import com.hbm.core.blockentity.BEDummyable;
import com.hbm.core.capability.energy.BasicEnergyHandler;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.multiblock.MultiblockData;
import com.hbm.datagen.recipe.ingredient.CountableIngredient;
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
 * 电弧焊机（core 体系多方块机器）。
 * 移植自旧版 TileEntityMachineArcWelder：用电能与可选流体焊接至多 3 种物品。
 * - 8 槽：0-2 输入、3 输出、4 电池、5 流体罐ID、6-7 升级
 * - 1 罐：焊剂/保护气体输入
 * - 配方：ArcWelderRecipe（多物品 + 可选流体 → 物品）
 */
public class ArcWelderEntity extends BEDummyable {
    public static final long MAX_POWER = 2_000;
    public static final int TANK_CAPACITY = 24_000;

    public long power;
    public int progress;
    public int processTime = 1;
    public long consumption = 100;

    private final FluidTank tank;

    public ArcWelderEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.items = new MachineItemHandler(8);
        this.energyContainer = createEnergyHandler(MAX_POWER, BasicEnergyHandler.INPUT);
        this.fluidHandler = new BasicFluidHandler(1, TANK_CAPACITY);
        this.tank = ((BasicFluidHandler) fluidHandler).getFluidTank(0);
        this.setMultiblockData(MultiblockData.mapping.get(ModBlocks.MACHINE_ARC_WELDER.get()));
    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (this.getLevel() == null || this.getLevel().isClientSide) return;

        TransmitUtils.dischargeItem(this, items.getStackInSlot(4));
        this.power = energyContainer.getEnergy();

        // 升级：6-7 槽
        int speed = 0, powerUp = 0, over = 0;
        for (int i = 6; i <= 7; i++){
            ItemStack up = items.getStackInSlot(i);
            if (up.getItem() instanceof com.hbm.item.machine.ItemMachineUpgrade upgrade){
                if (upgrade.type == com.hbm.item.machine.ItemMachineUpgrade.UpgradeType.SPEED) speed += upgrade.tier;
                if (upgrade.type == com.hbm.item.machine.ItemMachineUpgrade.UpgradeType.POWER) powerUp += upgrade.tier;
                if (upgrade.type == com.hbm.item.machine.ItemMachineUpgrade.UpgradeType.OVERDRIVE) over += upgrade.tier;
            }
        }
        double speedMult = 1.0 + speed * 0.25;
        double powerMult = 1.0 - powerUp * 0.15;
        double overdrive = 1.0 + over;

        ArcWelderRecipe recipe = getRecipe();
        if (recipe != null) {
            this.processTime = Math.max(1, (int) (recipe.duration / speedMult));
            this.consumption = (long) (recipe.consumption * powerMult * overdrive);

            if (canProcess(recipe)) {
                this.progress += 1;
                this.energyContainer.extract(consumption, false);
                this.power = energyContainer.getEnergy();

                if (progress >= processTime) {
                    this.progress = 0;
                    this.consumeItems(recipe);
                    ItemStack out = items.getStackInSlot(3);
                    if (out.isEmpty()) items.setStackInSlot(3, recipe.resultItem.copy());
                    else out.grow(recipe.resultItem.getCount());
                    this.setChanged();
                }
            } else {
                this.progress = 0;
            }
        } else {
            this.progress = 0;
            this.consumption = 100;
        }
        this.setChanged();
    }

    @Nullable
    private ArcWelderRecipe getRecipe(){
        return this.getLevel() == null ? null :
                this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.ARC_WELDER.type().get()).stream()
                        .filter(this::matchesRecipe)
                        .findFirst().orElse(null);
    }

    private boolean matchesRecipe(ArcWelderRecipe recipe){
        if (recipe.inputs == null) return false;
        // 3 个输入槽，逐个匹配（顺序无关）
        boolean[] used = new boolean[3];
        int matched = 0;
        for (CountableIngredient ing : recipe.inputs){
            boolean found = false;
            for (int i = 0; i < 3; i++){
                if (used[i]) continue;
                ItemStack stack = items.getStackInSlot(i);
                if (!stack.isEmpty() && ing.test(stack) && stack.getCount() >= ing.value.count){
                    used[i] = true;
                    found = true;
                    matched++;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean canProcess(ArcWelderRecipe recipe){
        if (power < consumption) return false;
        if (recipe.fluid != null && !recipe.fluid.isEmpty()){
            if (!tank.getFluid().isFluidEqual(recipe.fluid)) return false;
            if (tank.getFluidAmount() < recipe.fluid.getAmount()) return false;
        }
        ItemStack out = items.getStackInSlot(3);
        if (!out.isEmpty()){
            if (!out.is(recipe.resultItem.getItem())) return false;
            if (out.getCount() + recipe.resultItem.getCount() > out.getMaxStackSize()) return false;
        }
        return true;
    }

    private void consumeItems(ArcWelderRecipe recipe){
        if (recipe.inputs == null) return;
        boolean[] used = new boolean[3];
        for (CountableIngredient ing : recipe.inputs){
            for (int i = 0; i < 3; i++){
                if (used[i]) continue;
                ItemStack stack = items.getStackInSlot(i);
                if (!stack.isEmpty() && ing.test(stack) && stack.getCount() >= ing.value.count){
                    stack.shrink(ing.value.count);
                    if (stack.getCount() <= 0) items.setStackInSlot(i, ItemStack.EMPTY);
                    used[i] = true;
                    break;
                }
            }
        }
        if (recipe.fluid != null && !recipe.fluid.isEmpty()){
            tank.drain(recipe.fluid.getAmount(), FluidTank.FluidAction.EXECUTE);
        }
    }

    public MachineItemHandler getItemHandler(){ return items; }
    public FluidTank getTank(){ return tank; }
    public List<ArcWelderRecipe> getRecipes(){
        return this.getLevel() == null ? List.of() : this.getLevel().getRecipeManager().getAllRecipesFor(ModRecipes.ARC_WELDER.type().get()).stream().toList();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new com.hbm.gui.menu.ArcWelderMenu(pContainerId, pInventory, this, new net.minecraft.world.inventory.SimpleContainerData(4));
    }
}
