package com.hbm.core.contents.machine;

import com.hbm.api.energy.IEnergyContainer;
import com.hbm.core.capability.fluid.BasicFluidHandler;
import com.hbm.core.capability.item.MachineItemHandler;
import com.hbm.core.contents.upgrade.MachineUpgradeHandler;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

/**
 * 机器模块基类。
 *
 * 参考旧版 hbm 的 com.hbm.module.machine.ModuleMachineBase，将"机器处理配方的通用逻辑"
 * 从方块实体中剥离出来，放入一个可复用的模块对象。同时整合我们之前建造的基础设施：
 * - MachineItemHandler / BasicFluidHandler 作为物品/流体存储
 * - IEnergyContainer 作为能量存储
 * - MachineUpgradeHandler 作为升级效果（speed/power 倍率自动套用）
 * - 自动 IO 由 ItemTransferUtils / FluidTransferUtils 处理（见子类）
 *
 * 一个方块实体可以持有多个模块（例如化工厂有 4 个独立的配方槽组），每个模块独立运行。
 * 相比原版把配方逻辑硬编码进 TE，模块化后：
 * 1. 槽位/罐位接线（input/output 映射）只需配置一次
 * 2. 配方校验（hasInput / canFitOutput）统一实现
 * 3. 进度推进与产物生成统一处理
 * 4. 升级效果统一应用（canProcess/process 自动乘上 speed/power 倍率）
 *
 * 子类只需实现 getRecipeSet（从哪查配方），并根据配方结构覆写
 * consumeInput / produceOutput 即可。
 */
public abstract class MachineModuleBase<R extends Recipe<Container>> {
    // 模块在机器内的编号（多模块机器使用）
    protected final int index;
    // 机器持有的能力引用（由机器 TE 传入）
    protected final MachineItemHandler items;
    protected final IEnergyContainer battery;
    protected final BasicFluidHandler fluids;
    // 所属机器方块实体（升级系统的 owner）
    protected final BlockEntity owner;
    // 升级处理（可选）：机器通过 enableUpgrades 开启
    protected MachineUpgradeHandler upgradeHandler;
    // 升级槽范围（含边界）
    protected int upgradeStart = -1;
    protected int upgradeEnd = -1;
    // 基础功耗（升级前），用于倍率计算
    protected long basePowerCost = 0;
    // 配方相关槽位/罐位映射
    protected int[] inputSlots;
    protected int[] outputSlots;
    protected int[] inputTanks;
    protected int[] outputTanks;
    // 运行变量
    protected R recipe;
    protected int progress;
    // 返回值信号
    protected boolean didProcess = false;
    protected boolean markDirty = false;

    public MachineModuleBase(int index, MachineItemHandler items, IEnergyContainer battery, BasicFluidHandler fluids){
        this(index, null, items, battery, fluids);
    }
    public MachineModuleBase(int index, BlockEntity owner, MachineItemHandler items, IEnergyContainer battery, BasicFluidHandler fluids){
        this.index = index;
        this.owner = owner;
        this.items = items;
        this.battery = battery;
        this.fluids = fluids;
    }

    /** 开启升级支持：指定升级槽范围（含边界），并设置每级 speed/power 影响系数 */
    public MachineModuleBase<R> enableUpgrades(int upgradeStart, int upgradeEnd, double speedPerLevel, double powerPerLevel){
        this.upgradeStart = upgradeStart;
        this.upgradeEnd = upgradeEnd;
        this.upgradeHandler = new MachineUpgradeHandler(owner).speed(speedPerLevel).power(powerPerLevel);
        return this;
    }
    /** 开启升级支持（默认每级速度 +25%、功耗 -15%），并指定升级槽范围 */
    public MachineModuleBase<R> enableUpgrades(int upgradeStart, int upgradeEnd){
        return enableUpgrades(upgradeStart, upgradeEnd, 0.25, 0.15);
    }

    public int getIndex(){
        return index;
    }
    public int getProgress(){
        return progress;
    }
    public boolean didProcess(){
        return didProcess;
    }
    public boolean isMarkDirty(){
        return markDirty;
    }
    public void clearMarkDirty(){
        this.markDirty = false;
    }

    /** 获取本模块使用的配方集合（由子类指定） */
    protected abstract List<R> getRecipes(Level level);

    /** 根据输入槽的内容挑选当前配方，无配方返回 null */
    public R findRecipe(Level level){
        List<R> recipes = getRecipes(level);
        if (recipes == null || recipes.isEmpty()) return null;
        for (R r : recipes){
            if (r.matches(this.asContainer(), level)) return r;
        }
        return null;
    }

    /** 本模块作为 Container 供配方匹配（默认直接暴露物品 handler） */
    protected Container asContainer(){
        return new ModuleContainer(this);
    }

    /** 当前配方 */
    public R getRecipe(){
        return recipe;
    }
    public void setRecipe(R recipe){
        this.recipe = recipe;
    }

    /** 根据当前配方调整罐类型/容量，默认不做 */
    protected void setupTanks(){}

    /** 判断当前配方所需输入是否充足 */
    public boolean hasInput(Level level){
        if (recipe == null) return false;
        return recipe.matches(asContainer(), level);
    }

    /** 判断输出槽能否容纳配方产物（物品与流体） */
    public boolean canFitOutput(){
        return true;
    }

    /** 每 tick 更新升级缓存（开启升级后由 tick 调用） */
    public void updateUpgrades(){
        if (upgradeHandler != null && items != null && upgradeStart >= 0 && upgradeEnd >= upgradeStart){
            List<ItemStack> stacks = new java.util.ArrayList<>();
            for (int i = upgradeStart; i <= upgradeEnd; i++) stacks.add(items.getStackInSlot(i));
            upgradeHandler.check(stacks, 0, stacks.size() - 1);
        }
    }
    /** 升级后的速度倍率 */
    public double getSpeedMultiplier(){
        return upgradeHandler == null ? 1.0 : upgradeHandler.getSpeedMultiplier();
    }
    /** 升级后的功耗倍率 */
    public double getPowerMultiplier(){
        return upgradeHandler == null ? 1.0 : upgradeHandler.getPowerMultiplier();
    }

    /** 判断是否可以开始处理：有配方 + 输入充足 + 输出可容纳 + 电力充足（考虑升级倍率） */
    public boolean canProcess(Level level, long basePowerCost){
        if (recipe == null) return false;
        long powerCost = getPowerCost(basePowerCost);
        if (battery != null && powerCost > 0 && battery.getEnergy() < powerCost) return false;
        if (!hasInput(level)) return false;
        return canFitOutput();
    }

    /** 升级后的实际功耗 */
    public long getPowerCost(long basePowerCost){
        return (long) (basePowerCost * getPowerMultiplier());
    }

    /** 推进一个 tick 的进度（自动应用升级速度倍率） */
    public void process(Level level, long basePower){
        updateUpgrades();
        long power = getPowerCost(basePower);
        this.battery.extract(power, false);
        double step = Math.min(getSpeedMultiplier(), 1D);
        this.progress += (int) (step * 1000);
        if (this.progress >= 1000){
            consumeInput(level);
            produceOutput(level);
            if (this.canProcess(level, basePower)) this.progress -= 1000;
            else this.progress = 0;
            this.markDirty = true;
        }
    }

    /** 消耗输入（物品与流体），由子类按配方结构覆写 */
    protected void consumeInput(Level level){}

    /** 生成产物（物品与流体），由子类按配方结构覆写 */
    protected void produceOutput(Level level){}

    /** 输入槽 */
    public int[] getInputSlots(){
        return inputSlots;
    }
    /** 输出槽 */
    public int[] getOutputSlots(){
        return outputSlots;
    }
    /** 输入罐 */
    public int[] getInputTanks(){
        return inputTanks;
    }
    /** 输出罐 */
    public int[] getOutputTanks(){
        return outputTanks;
    }
    /** 获取输入罐实例 */
    public FluidTank getInputTank(int i){
        if (fluids == null || inputTanks == null || i < 0 || i >= inputTanks.length) return null;
        return fluids.getFluidTank(inputTanks[i]);
    }
    /** 获取输出罐实例 */
    public FluidTank getOutputTank(int i){
        if (fluids == null || outputTanks == null || i < 0 || i >= outputTanks.length) return null;
        return fluids.getFluidTank(outputTanks[i]);
    }
    /** 获取输入槽物品 */
    public ItemStack getInputItem(int i){
        if (items == null || inputSlots == null || i < 0 || i >= inputSlots.length) return ItemStack.EMPTY;
        return items.getStackInSlot(inputSlots[i]);
    }
    /** 获取输出槽物品 */
    public ItemStack getOutputItem(int i){
        if (items == null || outputSlots == null || i < 0 || i >= outputSlots.length) return ItemStack.EMPTY;
        return items.getStackInSlot(outputSlots[i]);
    }

    /** 判断某个槽是否可以作为该模块的输入（用于 isItemValid） */
    public boolean isItemValid(int slot, ItemStack stack){
        if (inputSlots == null) return false;
        for (int in : inputSlots) if (in == slot) return true;
        return false;
    }
}

