package com.hbm.blockentity.interfaces;


import com.hbm.HBMKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

/** 那些能处理配方的机器的功能的简单抽象 */
public interface IRecipeProcess<R extends Recipe<Container>> {
    static int maxProgress = 1000;
    int getProgress();
    void setProgress(int n);
    R getRecipeNow();
    void setRecipeNow(R recipeNow);
    Level getLevel();
    default void addProgress(){
        setProgress(getProgress()+1);
    }
    default void updateServer(){
        if (canProcess()){
            addProgress();
            onProcess();
            if (getProgress() >= maxProgress){
                setProgress(0);
                onEndRunning();
            }else if (getProgress() == 1){
                onStartRunning();
            }
        }else {
            setProgress(0);
            onNotRunning();
        }
    }
    // 判断是否可以运行，主要判断物品以及必要的能源等是否充足，如果可以则启动处理过程
    boolean canProcess();
    // 机器运行中，但尚未获得最终结果，在这里处理能量、污染、声音等处理过程产生的问题
    void onProcess();
    // 计时结束，机器生成处理结果，在这里根据配方生成产物，也可以添加机器处理结束时的内容。
    void onEndRunning();
    // 开启机器时的动作，比如让机器生成火焰粒子等
    default void onStartRunning(){};
    // 机器未处理配方时的动作。
    default void onNotRunning(){};

    default void loadData(CompoundTag pTag){
        if (pTag.contains(HBMKey.RECIPE_NOW, Tag.TAG_STRING)){
            ResourceLocation resourceLocation = new ResourceLocation(pTag.getString(HBMKey.RECIPE_NOW));
            setRecipeNow((R) getLevel().getRecipeManager().byKey(resourceLocation).orElse(null));
        }
        setProgress(pTag.getInt(HBMKey.PROGRESS));
    }
    default void saveData(CompoundTag pTag){
        if (getRecipeNow() != null)
            pTag.putString(HBMKey.RECIPE_NOW, getRecipeNow().getId().toString());
        pTag.putInt(HBMKey.PROGRESS, getProgress());
    }
}
