package com.hbm.blockentity.base2;

import com.hbm.HBMKey;
import com.hbm.api.energy.IEnergyContainer;
import com.hbm.api.fluid.IExtendedFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

// 抽象了处理配方的主要逻辑
public abstract class RecipeProcessBlockEntity<R extends Recipe<Container>> extends BaseMachineBlockEntity{
    protected static int maxProgress;
    protected static long maxPower;
    public int progress = 0;
    public int cooldown = 0;
    protected R recipeNow = null;
    // 能量和流体槽按需初始化
    protected IEnergyContainer energyContainer = null;
    protected List<IExtendedFluidTank> tanks = List.of();
    protected RecipeManager.CachedCheck<Container, R> quickCheck;
    protected RecipeProcessBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

    }

    @Override
    protected void onUpdateServer() {
        super.onUpdateServer();
        if (canProcess()){
            progress ++;
            onProcess();
            if (progress >= maxProgress){
                this.progress = 0;
                onEndRunning();
            }else if (this.progress == 1){
                onStartRunning();
            }
        }else {
            this.progress = 0;
            onNotRunning();
        }
    }
    // 判断是否可以运行，主要判断物品以及必要的能源等是否充足，如果可以则启动处理过程
    protected abstract boolean canProcess();
    // 机器运行中，但尚未获得最终结果，在这里处理能量、污染、声音等处理过程产生的问题
    protected abstract void onProcess();
    // 计时结束，机器生成处理结果，在这里根据配方生成产物，也可以添加机器处理结束时的内容。
    protected abstract void onEndRunning();
    // 开启机器时的动作，比如让机器生成火焰粒子等
    protected abstract void onStartRunning();
    // 机器未处理配方时的动作。
    protected abstract void onNotRunning();

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains(HBMKey.RECIPE_NOW, Tag.TAG_STRING)){
            ResourceLocation resourceLocation = new ResourceLocation(pTag.getString(HBMKey.RECIPE_NOW));
            this.recipeNow = (R) this.level.getRecipeManager().byKey(resourceLocation).orElse(null);
        }
        this.progress = pTag.getInt(HBMKey.PROGRESS);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (this.recipeNow != null)
            pTag.putString(HBMKey.RECIPE_NOW, this.recipeNow.getId().toString());
        pTag.putInt(HBMKey.PROGRESS, this.progress);
    }
}
