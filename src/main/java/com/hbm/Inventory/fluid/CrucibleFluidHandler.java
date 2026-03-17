package com.hbm.Inventory.fluid;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class CrucibleFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag> {
    private final NonNullList<FluidStack> content;
    private final int capacity;
    public CrucibleFluidHandler(int capacity){
        this.content = NonNullList.create();
        this.capacity = capacity;
    }
    @Override
    public int getTanks() {
        return 1;
    }

    public int getSize(){
        return content.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return tank < content.size() ? content.get(tank) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }
    // 暂时没想好怎么检测流体类型
    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    public int getTotalAmount(){
        return content.stream().mapToInt(FluidStack::getAmount).sum();
    }

    public int getNeeded(){
        return this.capacity - this.getTotalAmount();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;

        int space = capacity - getTotalAmount();
        int toFill = Math.min(resource.getAmount(), space);

        if (action.execute()) {
            // 查找是否已有同类流体进行合并
            for (FluidStack stack : content) {
                if (stack.isFluidEqual(resource)) {
                    stack.grow(toFill);
                    return toFill;
                }
            }
            // 没有则在列表顶端新增一层
            content.add(new FluidStack(resource, toFill));
        }
        return toFill;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (content.isEmpty() || resource.isEmpty()) return FluidStack.EMPTY;
        for (int i = 0; i < content.size(); i++) {
            FluidStack fluidStack = content.get(i);
            if (fluidStack.isFluidEqual(resource)){
                int toDrain = Math.min(fluidStack.getAmount(), resource.getAmount());
                FluidStack result = new FluidStack(fluidStack, toDrain);
                if (action.execute()){
                    fluidStack.shrink(toDrain);
                    if (fluidStack.isEmpty()) content.remove(i);
                }
                return result;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (content.isEmpty() || maxDrain <= 0) return FluidStack.EMPTY;

        // 默认从底层（第0位）抽取
        FluidStack bottom = content.get(0);
        int toDrain = Math.min(bottom.getAmount(), maxDrain);
        FluidStack result = new FluidStack(bottom, toDrain);

        if (action.execute()) {
            bottom.shrink(toDrain);
            if (bottom.isEmpty()) content.remove(0); // 如果抽干了，移除该层
        }
        return result;
    }

    // 玩家点击 GUI 调整顺序的方法
    public void moveToBottom(int index) {
        if (index > 0 && index < content.size()) {
            FluidStack stack = content.remove(index);
            content.add(0, stack);
        }
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("num", content.size());
        for (int i = 0; i < content.size(); i++) {
            tag.put("" + i, content.get(i).writeToNBT(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.content.clear();
        if (! nbt.contains("num", Tag.TAG_INT)) return;
        int num = nbt.getInt("num");
        for (int i = 0; i < num; i++) {
            content.add(i, FluidStack.loadFluidStackFromNBT(nbt.getCompound("" + i)));
        }
    }
}
