package com.hbm.api.fluid;

import com.hbm.HBMKey;
import com.hbm.api.Mode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class SingleFluidHandler implements IExtendedFluidHandler, INBTSerializable<CompoundTag> {
    FluidTank tank;
    Mode mode;
    public SingleFluidHandler(int capacity){
        this(capacity, Mode.BOTH);
    }
    public SingleFluidHandler(int capacity, Mode mode){
        this(new BasicFluidTank(capacity), mode);
    }
    public SingleFluidHandler(FluidTank tank, Mode mode){
        this.tank = tank;
        this.mode = mode;
    }
    @Override
    public List<FluidTank> getFluidTanks() {
        return List.of(tank);
    }
    public Mode getMode(){ return mode;}
    @Override
    public Mode getMode(int tank) {
        return tank == 0 ? mode : Mode.NONE;
    }
    public void setMode(Mode mode){
        this.mode = mode;
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = tank.writeToNBT(new CompoundTag());
        tag.putInt(HBMKey.MODE, mode.ordinal());
//        CompoundTag compoundTag = new CompoundTag();
//        compoundTag.put(HBMKey.FLUIDS, tank.writeToNBT(new CompoundTag()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        tank.readFromNBT(nbt);
        mode = Mode.values()[nbt.getInt(HBMKey.MODE)];
    }

    /**
     * 从流体容器中吸纳流体，并返回吸纳完的空容器
     * 吸纳失败，则返回empty。
     * */
    public ItemStack drainItem(ItemStack itemStack){
        if (itemStack.isEmpty())return itemStack;
        else if (itemStack.getItem() instanceof BucketItem bucketItem){
            int bucketVolume = 1000;
            FluidStack fluidStack = new FluidStack(bucketItem.getFluid(), bucketVolume);
            if (tank.isFluidValid(0,fluidStack) && tank.fill(fluidStack, FluidAction.SIMULATE) == bucketVolume){
                tank.fill(fluidStack, FluidAction.EXECUTE);
                return Items.BUCKET.getDefaultInstance();
            }else return itemStack;
        }
        return itemStack;
    }
    /**
     * 向流体容器中注入流体
     * 如果注入失败，则将输入物品原样返回
     * */
    public ItemStack fillItem(ItemStack itemStack){
        if (itemStack.isEmpty())return itemStack;
        else if (itemStack.is(Items.BUCKET)){
            int bucketVolume = 1000;
            if (tank.drain(bucketVolume, FluidAction.SIMULATE).getAmount() == bucketVolume){
                FluidStack fluidStack = tank.drain(bucketVolume, FluidAction.EXECUTE);
                return fluidStack.getFluid().getBucket().getDefaultInstance();
            }
        }
        return itemStack;
    }
}
