package com.hbm.core.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.function.Predicate;

/**
 * 物流功能工具类，全部基于 Forge 标准 IItemHandler。
 * 所有方法统一约定：第一个参数是源（from），第二个参数是目标（to）。
 */
public class ItemTransferUtils {
    private ItemTransferUtils() {}

    //====================== 输出（push：from 向 to 输出）======================

    /** 输出不特定物品，不限数量，尽可能移出直到目标放不下。返回实际移动数量。 */
    public static int pushAll(IItemHandler from, IItemHandler to){
        return pushAmount(from, to, Integer.MAX_VALUE);
    }
    /** 输出不特定物品，限定数量，到达规定数量停止，未到达则尽力而为。返回实际移动数量。 */
    public static int pushAmount(IItemHandler from, IItemHandler to, int maxAmount){
        return pushWithFilter(from, to, stack -> true, maxAmount);
    }
    /** 输出特定物品，不限数量。搜索源中是否有对应物品，有则输出，无则不输出。 */
    public static int pushAll(IItemHandler from, IItemHandler to, ItemStack filter){
        return pushWithFilter(from, to, stack -> ItemStack.isSameItemSameTags(stack, filter), Integer.MAX_VALUE);
    }
    /** 输出特定物品，不限数量（谓词过滤版）。 */
    public static int pushAll(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter){
        return pushWithFilter(from, to, filter, Integer.MAX_VALUE);
    }
    /** 输出特定物品，限定数量。 */
    public static int pushAmount(IItemHandler from, IItemHandler to, ItemStack filter, int maxAmount){
        return pushWithFilter(from, to, stack -> ItemStack.isSameItemSameTags(stack, filter), maxAmount);
    }
    /** 输出特定物品，限定数量（谓词过滤版）。 */
    public static int pushAmount(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter, int maxAmount){
        return pushWithFilter(from, to, filter, maxAmount);
    }
    private static int pushWithFilter(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter, int maxAmount){
        if (from == null || to == null || maxAmount <= 0) return 0;
        int moved = 0;
        for (int i = 0; i < from.getSlots() && moved < maxAmount; i++) {
            ItemStack slotStack = from.getStackInSlot(i);
            if (slotStack.isEmpty() || !filter.test(slotStack)) continue;
            int toMove = Math.min(maxAmount - moved, slotStack.getCount());
            ItemStack simulated = from.extractItem(i, toMove, true);
            if (simulated.isEmpty()) continue;
            ItemStack remaining = pushStack(simulated, to);
            int pushed = simulated.getCount() - remaining.getCount();
            if (pushed > 0){
                from.extractItem(i, pushed, false);
                moved += pushed;
            }
        }
        return moved;
    }

    /** 向 to1 输入给定物品，溢出则输入 to2，再溢出则返回剩余。 */
    public static ItemStack insertWithOverflow(ItemStack stack, IItemHandler to1, IItemHandler to2){
        ItemStack remaining = ItemHandlerHelper.insertItemStacked(to1, stack, false);
        if (!remaining.isEmpty() && to2 != null)
            remaining = ItemHandlerHelper.insertItemStacked(to2, remaining, false);
        return remaining;
    }

    /** 将单个堆叠尽可能插入目标容器，返回未能插入的剩余部分。 */
    public static ItemStack pushStack(ItemStack stack, IItemHandler to){
        if (stack.isEmpty() || to == null) return stack;
        return ItemHandlerHelper.insertItemStacked(to, stack, false);
    }

    //====================== 拉取（pull：从 from 拉取到 to）======================

    /** 拉取不特定物品，不限数量。返回实际移动数量。 */
    public static int pullAll(IItemHandler from, IItemHandler to){
        return pullAmount(from, to, Integer.MAX_VALUE);
    }
    /** 拉取不特定物品，限定数量。 */
    public static int pullAmount(IItemHandler from, IItemHandler to, int maxAmount){
        return pullWithFilter(from, to, stack -> true, maxAmount);
    }
    /** 拉取特定物品，不限数量。 */
    public static int pullAll(IItemHandler from, IItemHandler to, ItemStack filter){
        return pullWithFilter(from, to, stack -> ItemStack.isSameItemSameTags(stack, filter), Integer.MAX_VALUE);
    }
    /** 拉取特定物品，不限数量（谓词过滤版）。 */
    public static int pullAll(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter){
        return pullWithFilter(from, to, filter, Integer.MAX_VALUE);
    }
    /** 拉取特定物品，限定数量。 */
    public static int pullAmount(IItemHandler from, IItemHandler to, ItemStack filter, int maxAmount){
        return pullWithFilter(from, to, stack -> ItemStack.isSameItemSameTags(stack, filter), maxAmount);
    }
    /** 拉取特定物品，限定数量（谓词过滤版）。 */
    public static int pullAmount(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter, int maxAmount){
        return pullWithFilter(from, to, filter, maxAmount);
    }
    private static int pullWithFilter(IItemHandler from, IItemHandler to, Predicate<ItemStack> filter, int maxAmount){
        if (from == null || to == null || maxAmount <= 0) return 0;
        int moved = 0;
        for (int i = 0; i < from.getSlots() && moved < maxAmount; i++) {
            ItemStack slotStack = from.getStackInSlot(i);
            if (slotStack.isEmpty() || !filter.test(slotStack)) continue;
            int toMove = Math.min(maxAmount - moved, slotStack.getCount());
            ItemStack simulated = from.extractItem(i, toMove, true);
            if (simulated.isEmpty()) continue;
            ItemStack remaining = ItemHandlerHelper.insertItemStacked(to, simulated, true);
            int canInsert = simulated.getCount() - remaining.getCount();
            if (canInsert > 0){
                from.extractItem(i, canInsert, false);
                ItemHandlerHelper.insertItemStacked(to, simulated.copyWithCount(canInsert), false);
                moved += canInsert;
            }
        }
        return moved;
    }

    /** 从 from1 拉取指定物品，数量不足则从 from2 补足。返回拉取到的物品。 */
    public static ItemStack pullWithOverflow(ItemStack wanted, IItemHandler from1, IItemHandler from2){
        if (wanted.isEmpty() || from1 == null) return ItemStack.EMPTY;
        ItemStack gathered = pullStack(wanted, from1);
        int need = wanted.getCount() - gathered.getCount();
        if (need > 0 && from2 != null){
            ItemStack needStack = wanted.copyWithCount(need);
            ItemStack more = pullStack(needStack, from2);
            if (!more.isEmpty()){
                if (gathered.isEmpty()) gathered = more;
                else gathered.grow(more.getCount());
            }
        }
        return gathered;
    }

    /** 从目标容器提取指定物品（尽可能提取 wanted.getCount() 个），返回实际提取到的物品。 */
    public static ItemStack pullStack(ItemStack wanted, IItemHandler from){
        if (wanted.isEmpty() || from == null) return ItemStack.EMPTY;
        int need = wanted.getCount();
        ItemStack result = ItemStack.EMPTY;
        for (int i = 0; i < from.getSlots() && need > 0; i++) {
            ItemStack slotStack = from.getStackInSlot(i);
            if (slotStack.isEmpty() || !ItemStack.isSameItemSameTags(slotStack, wanted)) continue;
            ItemStack extracted = from.extractItem(i, need, true);
            if (extracted.isEmpty()) continue;
            from.extractItem(i, extracted.getCount(), false);
            if (result.isEmpty()) result = extracted;
            else result.grow(extracted.getCount());
            need -= extracted.getCount();
        }
        return result;
    }
}
