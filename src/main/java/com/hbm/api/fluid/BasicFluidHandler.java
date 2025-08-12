package com.hbm.api.fluid;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

//基础流体系统，里面包含多个FluidTank
public class BasicFluidHandler implements IExtendedFluidHandler, INBTSerializable<CompoundTag> {
    List<FluidTank> tanks;
    List<Mode> tankModes;
    public BasicFluidHandler(){
        tanks = new ArrayList<>();
        tankModes = new ArrayList<>();
    }
    public BasicFluidHandler(int tankNum, int capacity){
        tanks = new ArrayList<>();
        tankModes = new ArrayList<>();
        for (int i = 0; i < tankNum; i++) {
            tanks.add(new FluidTank(capacity));
            tankModes.add(Mode.BOTH);
        }
    }

    public BasicFluidHandler setMode(int tank, Mode mode){
        if (tank >= 0 && tank <= tanks.size()) tankModes.set(tank, mode);
        return this;
    }
    public BasicFluidHandler setModes(Mode mode, int ... tankCodes){
        for (int tankCode : tankCodes) {
            setMode(tankCode, mode);
        }
        return this;
    }
    public BasicFluidHandler addTank(int capacity, Mode mode){
        tanks.add(new FluidTank(capacity));
        tankModes.add(mode);
        return this;
    }
    public BasicFluidHandler addTanks(int num, int capacity, Mode mode){
        for (int i = 0; i < num; i++) {
            addTank(capacity, mode);
        }
        return this;
    }

    @Override
    public List<FluidTank> getFluidTanks() {
        return tanks;
    }

    @Override
    public Mode getMode(int tank) {
        return tankModes.get(tank);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        int length = tanks.size();
        compoundTag.putInt(HBMKey.NUM, length);
        for (int i = 0; i < length; i++) {
            CompoundTag tag = tanks.get(i).writeToNBT(new CompoundTag());
            tag.putInt(HBMKey.MODE, tankModes.get(i).ordinal());
            compoundTag.put(String.valueOf(i),tag);
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int length = nbt.getInt(HBMKey.NUM);
        for (int i = 0; i < length; i++) {
            CompoundTag tag = nbt.getCompound(String.valueOf(i));
            tanks.get(i).readFromNBT(tag);
            this.tankModes.set(i, Mode.values()[nbt.getInt(HBMKey.MODE)]);
        }
    }
    /**
     * 从流体容器中吸纳流体，并返回吸纳完的空容器
     * 吸纳失败，则返回empty。
     * */
    public ItemStack drainItem(int tank, ItemStack itemStack){
        if (itemStack.isEmpty() || !allowInput(0))return itemStack;
        else if (itemStack.getItem() instanceof BucketItem bucketItem){
            int bucketVolume = 1000;
            FluidStack fluidStack = new FluidStack(bucketItem.getFluid(), bucketVolume);
            if (isFluidValid(tank,fluidStack) && this.tanks.get(tank).fill(fluidStack, FluidAction.SIMULATE) == bucketVolume){
                this.tanks.get(tank).fill(fluidStack, FluidAction.EXECUTE);
                return Items.BUCKET.getDefaultInstance();
            }else return itemStack;
        }
        return itemStack;
    }
    /**
     * 向流体容器中注入流体
     * 如果注入失败，则将输入物品原样返回
     * */
    public ItemStack fillItem(int tank, ItemStack itemStack){
        if (itemStack.isEmpty() || !allowOutput(0))return itemStack;
        else if (itemStack.is(Items.BUCKET)){
            int bucketVolume = 1000;
            if (this.tanks.get(tank).drain(bucketVolume, FluidAction.SIMULATE).getAmount() == bucketVolume){
                FluidStack fluidStack = this.tanks.get(tank).drain(bucketVolume, FluidAction.EXECUTE);
                return fluidStack.getFluid().getBucket().getDefaultInstance();
            }
        }
        return itemStack;
    }
}
