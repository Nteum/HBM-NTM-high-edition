package com.hbm.api.inventory;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.interferences.IDefaultFacing;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
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
    // 获取所有面的访问控制信息
    // 返回：一个map，key是方向，value是允许的每个槽的访问控制信息
    // 这个默认选项不可取，实际上只是为了避免一定要继承的情况。
    default Map<Direction, SlotAccCtl[]> getAccCtl(){
        return Map.of(Direction.UP,new SlotAccCtl[0],Direction.DOWN,new SlotAccCtl[0]
                ,Direction.NORTH,new SlotAccCtl[0],Direction.SOUTH,new SlotAccCtl[0]
                ,Direction.WEST,new SlotAccCtl[0],Direction.EAST,new SlotAccCtl[0]);
    }
    // 某个面是否可以输入或输出
    // 第二个参数true表示输入，false表示输出
    default SlotAccCtl allowAcc(Direction side, boolean inOrOut){
        for (SlotAccCtl slotAccCtl : getAccCtl().get(side)) {
            if ((slotAccCtl.allowIn()&&inOrOut) || (slotAccCtl.allowOut()&&!inOrOut)){
                return slotAccCtl;
            }
        }
        return SlotAccCtl.SlotAccCtlBase.EMPTY;
    }

    // 某个方向可用的slot个数
    default int getSlots(@Nullable Direction side){
        if (side == null)return getItems().size();
        else return getAccCtl().get(side).length;
    }
    // 总物品槽数
    @Override
    default int getSlots() {return getItems().size();}

    // 从某个方向获取某个slot中的内容
    // 注意：返回的ItemStack不能修改。
    // 我不理解为什么这个要加一个方向参数，按理说get函数又不涉及修改内容，查询难道还要限制吗？
    default ItemStack getStackInSlot(int slot, @Nullable Direction side){
        return slot >= 0 && slot < getSlots() ? getStackInSlot(slot) : ItemStack.EMPTY;
    }
    @Override
    default ItemStack getStackInSlot(int slot) {
        return getStackInSlot(slot, null);
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
        if (side!=null && allowAcc(side, true) == SlotAccCtl.EMPTY)return stack;
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
        SlotAccCtl accCtl = null;
        if (side != null && (accCtl = allowAcc(side, false)) == SlotAccCtl.EMPTY || amount <= 0) return ItemStack.EMPTY;
        ItemStack originStack = getStackInSlot(slot);
        if (originStack.isEmpty())return ItemStack.EMPTY;
        assert accCtl != null;
        int extAmount = Math.min(accCtl.out(),Math.min(amount, originStack.getCount()));
        if (!simulate){
            originStack.shrink(extAmount);
            if (originStack.isEmpty()){
                setStackInSlot(slot, ItemStack.EMPTY);
            }else
                setStackInSlot(slot, originStack);
        }
        return originStack.copyWithCount(extAmount);
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
        if (side != null && allowAcc(side, true) == SlotAccCtl.EMPTY)return false;
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