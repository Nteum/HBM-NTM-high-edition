package com.hbm.api.inventory;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.interferences.IDefaultFacing;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 处理侧边物品交换的类
 * 参考SidedInvWrapper
 * */
@NothingNullByDefault
public interface ISidedItemHandler extends IItemHandlerModifiable, IDefaultFacing {
    // 所有物品槽的集合
    default List<ItemStack> getItems(){return List.of();}
//    default boolean allowInput(int slot, Direction side){return true;}
//    default boolean allowOutput(int slot, Direction side){return true;}
    /** 不考虑面方向的输入输出控制，面方向由blockentity自身的capability额外控制 */
    default boolean slotIOCtl(int slot, @Nullable ItemStack pStack, boolean isInput){return true;}

    default int getSlots(@Nullable Direction side){
        return getSlots();
    }
    // 总物品槽数
    @Override
    default int getSlots() {return getItems().size();}

    // 从某个方向获取某个slot中的内容
    // 注意：返回的ItemStack不能修改。
    // 我不理解为什么这个要加一个方向参数，按理说get函数又不涉及修改内容，查询难道还要限制吗？
    default ItemStack getStackInSlot(int slot, @Nullable Direction side){
        return getStackInSlot(slot);
    }
    @Override
    default ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < getSlots() ? getItems().get(slot) : ItemStack.EMPTY;
    }
    // 直接设置某个物品槽中的物品
    // 同样感觉加方向参数没意义，这个函数明显是直接顶替的，不会作为玩家操作的游戏逻辑
    default ItemStack setStackInSlot(int slot, ItemStack stack, @Nullable Direction side){
        if (slot >= 0 && slot < getSlots()){
            ItemStack beforeStack = getItems().get(slot);
            getItems().set(slot,stack);
            return beforeStack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    default void setStackInSlot(int slot, ItemStack stack) {
        setStackInSlot(slot, stack, null);
    }

    // 向特定物品槽输入物品，返回未输入的物品，如果全部输入则返回 ItemStack.EMPTY
    // 说明：返回的物品可以被安全地修改
    default ItemStack insertItem(int slot, ItemStack stack, @Nullable Direction side, boolean simulate){
        if (side!=null && slotIOCtl(slot, stack, true))return stack;
        ItemStack beforeStack = getStackInSlot(slot);
        boolean sameType = false;
        if (stack.isEmpty() || !(sameType = ItemHandlerHelper.canItemStacksStack(beforeStack, stack))) {
            return stack;
        }
        int needed = getSlotLimit(slot) - beforeStack.getCount();
        if (needed <= 0) {
            //Fail if we are a full slot
            return stack;
        }

        int toAdd = Math.min(stack.getCount(), needed);
        if (!simulate) {
            beforeStack.grow(toAdd);
        }
        return stack.copyWithCount(stack.getCount() - toAdd);
    }

    @Override
    default ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return insertItem(slot, stack, null, simulate);
    }

    // 从特定物品槽抽取特定数量的物品，返回成功抽取的物品
    // 说明：返回的ItemStack可以被修改。
    default ItemStack extractItem(int slot, int amount, @Nullable Direction side, boolean simulate){
        ItemStack result = ItemStack.EMPTY;
        if (side != null && slotIOCtl(slot, null, false) || amount <= 0) return result;
        ItemStack originStack = getStackInSlot(slot);
        if (originStack.isEmpty())return result;
        int extAmount = Math.min(amount, originStack.getCount());
        result = originStack.copyWithCount(extAmount);
        if (!simulate){
            originStack.shrink(extAmount);
            if (originStack.isEmpty()){
                setStackInSlot(slot, ItemStack.EMPTY);
            }else
                setStackInSlot(slot, originStack);
        }
        return result;
    }

    @Override
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        return extractItem(slot, amount, null, simulate);
    }

    // 获取某个物品槽地最大容量
    // 同上，get函数似乎不需要参数
    default int getSlotLimit(int slot, @Nullable Direction side){
        if (slot < 0 || slot >= getSlots())return 0;
        // 默认最大堆叠，和物品本身最大堆叠的最小值
        return Math.min(Container.LARGE_MAX_STACK_SIZE, getStackInSlot(slot).getMaxStackSize());
    }

    @Override
    default int getSlotLimit(int slot) {
        return getSlotLimit(slot, null);
    }

    // 判断是否可以接收特定类型的物品
    // 说明：只能判断物品类型是否合法，但不能判断插入一定数量物品是否成功，万一这个物品槽已经满了呢。
    default boolean isItemValid(int slot, ItemStack stack, @Nullable Direction side){
        if (side != null && slotIOCtl(slot, stack, true))return false;
        if (slot < 0 || slot >= getSlots())return false;
        return getStackInSlot(slot).is(stack.getItem());
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack) {
        return isItemValid(slot, stack, null);
    }
    default boolean inventoryEmpty(){
        for (ItemStack itemStack : getItems()) {
            if (!itemStack.isEmpty())
                return false;
        }
        return true;
    }
}