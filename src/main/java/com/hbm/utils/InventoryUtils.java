package com.hbm.utils;

import com.hbm.blockentity.base.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/** 使用itemhandler抽取物品
 * ref:forge - VanillaInventoryCodeHooks
 * */
public class InventoryUtils {
    static Function<ItemStack,Boolean> allowAll = (itemStack -> true);
    private InventoryUtils() {
    }
    @Nullable
    public static Boolean extractItem(Level level, BaseMachineBlockEntity dest, Direction interactDir)
    {
        return extractItem(level,dest,null,null,interactDir,allowAll);
    }
    @Nullable
    public static Boolean extractItem(Level level, BaseMachineBlockEntity dest,List<Integer> extSlots, Direction interactDir)
    {
        return extractItem(level,dest,null,extSlots, interactDir,allowAll);
    }
    /** 从外界拉取物品
     * 参考TileEntityHopper#captureDroppedItems，并添加能力支持
     * @param interactDir 交互方向，一般机器接口就一个，因此方向只有一个
     * @param proxyPos 代理方块的位置，一般用于多方块结构，如果非多方快结构可以不使用
     * @param extCond 拉取规则，有些物品不会从外界向内拉取。
     * @return Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
     */
    @Nullable
    public static Boolean extractItem(Level level, BaseMachineBlockEntity dest, @Nullable BlockPos proxyPos, @Nullable List<Integer> extSlots, Direction interactDir,
                                      Function<ItemStack,Boolean> extCond)
    {
        BlockPos destPos = proxyPos==null?dest.getBlockPos():proxyPos;
        List<Integer> slotList = extSlots==null? IntStream.range(0,dest.getContainerSize()).boxed().toList():extSlots;
        return getItemHandler(level, destPos.relative(interactDir), interactDir.getOpposite())
                .map(itemHandlerResult -> {
                    IItemHandler handler = itemHandlerResult.getKey();

                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        ItemStack extractItem = handler.extractItem(i, 1, true);
                        if (!extractItem.isEmpty() && extCond.apply(extractItem))
                        {
//                            for (int j = 0; j < dest.getContainerSize(); j++)
                            for (int j : slotList)
                            {
                                ItemStack destStack = dest.getItem(j);
                                if (dest.canPlaceItem(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack)))
                                {
                                    extractItem = handler.extractItem(i, 1, false);
                                    if (destStack.isEmpty())
                                        dest.setItem(j, extractItem);
                                    else
                                    {
                                        destStack.grow(1);
                                        dest.setItem(j, destStack);
                                    }
                                    dest.setChanged();
                                    return true;
                                }
                            }
                        }
                    }

                    return false;
                })
                .orElse(null); // TODO bad null
    }

    /**
     * Copied from BlockDropper#dispense and added capability support
     */
    public static boolean dropperInsertHook(Level level, BlockPos pos, DispenserBlockEntity dropper, int slot, @NotNull ItemStack stack)
    {
        Direction enumfacing = level.getBlockState(pos).getValue(DropperBlock.FACING);
        BlockPos blockpos = pos.relative(enumfacing);
        return getItemHandler(level, blockpos, enumfacing.getOpposite())
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    ItemStack dispensedStack = stack.copy().split(1);
                    ItemStack remainder = putStackInInventoryAllSlots(dropper, destination, itemHandler, dispensedStack);

                    if (remainder.isEmpty())
                    {
                        remainder = stack.copy();
                        remainder.shrink(1);
                    }
                    else
                    {
                        remainder = stack.copy();
                    }

                    dropper.setItem(slot, remainder);
                    return false;
                })
                .orElse(true);
    }
    public static boolean insertItem(BaseMachineBlockEntity src, Direction interactDir)
    {
        return insertItem(src,null,null,interactDir);
    }
    public static boolean insertItem(BaseMachineBlockEntity src,List<Integer> intSlots, Direction interactDir)
    {
        return insertItem(src,null,intSlots,interactDir);
    }
    /** 向外界实体输出物品
     * Copied from TileEntityHopper#transferItemsOut and added capability support
     */
    public static boolean insertItem(BaseMachineBlockEntity src,@Nullable BlockPos proxyPos,@Nullable List<Integer> intSlots, Direction interactDir)
    {
        List<Integer> slotList = intSlots==null? IntStream.range(0,src.getContainerSize()).boxed().toList():intSlots;
        BlockPos srcPos = proxyPos==null?src.getBlockPos():proxyPos;
        return getItemHandler(src.getLevel(), srcPos.relative(interactDir), interactDir.getOpposite())
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    if (isFull(itemHandler))
                    {
                        return false;
                    }
                    else
                    {
//                        for (int i = 0; i < src.getContainerSize(); ++i)
                        for (int i : slotList)
                        {
                            if (!src.getItem(i).isEmpty())
                            {
                                ItemStack originalSlotContents = src.getItem(i).copy();
                                ItemStack insertStack = src.removeItem(i, 1);
                                ItemStack remainder = putStackInInventoryAllSlots(src, destination, itemHandler, insertStack);

                                if (remainder.isEmpty())
                                {
                                    return true;
                                }

                                src.setItem(i, originalSlotContents);
                            }
                        }

                        return false;
                    }
                })
                .orElse(false);
    }

    private static ItemStack putStackInInventoryAllSlots(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
    {
        //逐个向目标container输出物品
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
        {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    /**
     * Copied from TileEntityHopper#insertStack and added capability support
     */
    private static ItemStack insertStack(BlockEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
    {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty())  //可以向其中输出物品
        {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty())
            {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            }
            else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
            {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem)
            {   //如果是漏斗则更新一下冷却时间
                if (inventoryWasEmpty && destination instanceof HopperBlockEntity)
                {
                    HopperBlockEntity destinationHopper = (HopperBlockEntity)destination;

                    if (!destinationHopper.isOnCustomCooldown())
                    {
                        int k = 0;
                        if (source instanceof HopperBlockEntity)
                        {
                            if (destinationHopper.getLastUpdateTime() >= ((HopperBlockEntity) source).getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }
                        destinationHopper.setCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    private static Optional<Pair<IItemHandler, Object>> getItemHandler(Level level, BaseContainerBlockEntity src, Direction facing)
    {
        return getItemHandler(level, src.getBlockPos().relative(facing), facing.getOpposite());
    }

    private static boolean isFull(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0)
            {
                return false;
            }
        }
        return true;
    }

    public static Optional<Pair<IItemHandler, Object>> getItemHandler(Level worldIn, BlockPos blockpos, final Direction side)
    {
        net.minecraft.world.level.block.state.BlockState state = worldIn.getBlockState(blockpos);

        if (state.hasBlockEntity())
        {
            BlockEntity blockEntity = worldIn.getBlockEntity(blockpos);
            if (blockEntity != null)
            {
                return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, side)
                        .map(capability -> ImmutablePair.<IItemHandler, Object>of(capability, blockEntity));
            }
        }

        return Optional.empty();
    }
    // 减少大小，最多减到0，输出变化后的itemstack
    // 使用这个函数不在乎amount是否被减完
    public static ItemStack shrink(int amount, ItemStack itemStack){
        itemStack.shrink(amount);
        return itemStack.isEmpty() ? ItemStack.EMPTY : itemStack;
    }
    // 增加大小，最多增加到预设的最大堆叠数
    public static ItemStack grow(int amount, ItemStack itemStack){
        itemStack.grow(amount);
        if (itemStack.getCount() > itemStack.getMaxStackSize())
            itemStack.setCount(itemStack.getMaxStackSize());
        return itemStack;
    }
    // 判断两个itemstack是否可以不损失地加在一起
    // 用于判断机器输出口是否可以加工。
    public static boolean canAddItemEntirely(ItemStack originStack, ItemStack toPut){
        return originStack.isEmpty() || ItemHandlerHelper.canItemStacksStack(originStack,toPut) && originStack.getCount()+toPut.getCount() <= originStack.getMaxStackSize();
    }
    // 在一个itemstack的基础上增长物品
    // 使用这个函数已经暗示itemstack和itemstack2是同类物品了，或者第一个itemstack是空的
    public static ItemStack growNoCheck(int amount, ItemStack itemStack, ItemStack itemStack2){
        if (itemStack.isEmpty())itemStack = itemStack2.copy();
        else itemStack.grow(amount);
        return itemStack;
    }

    /**
     * 物品处理需要解决的问题：
     * 1.
     * */
    public static void handleItems(BlockEntity be, Function<ItemStack, ItemStack> processFunction, int slot1, int slot2){
        IItemHandler itemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (itemHandler == null)return;
        ItemStack itemStack = itemHandler.extractItem(slot1, 1, true);
        ItemStack output = processFunction.apply(itemStack);
        if (!ItemStack.isSameItemSameTags(itemStack,output) && itemHandler.insertItem(slot2, output, true) == ItemStack.EMPTY){
            itemHandler.extractItem(slot1, 1, false);
            itemHandler.insertItem(slot2, output, false);
        }
    }
    public static boolean insertNoCheckSlots(IItemHandler from, IItemHandler to){
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack stackInSlot = from.getStackInSlot(i);
            if (insertNoCheckSlots(from, to, i, stackInSlot.getCount()) > 0){
                return true;
            }
        }
        return false;
    }
    public static int insertNoCheckSlots(IItemHandler from, IItemHandler to, int amount){
        int cnt = amount;
        for (int i = 0; i < from.getSlots(); i++) {
            cnt -= insertNoCheckSlots(from, to, i, cnt);
            if (cnt == 0) return amount;
        }
        return amount - cnt;
    }
    // 返回输出值
    public static int insertNoCheckSlots(IItemHandler from, IItemHandler to, int slot, int amount){
        ItemStack itemStack = from.extractItem(slot, amount, true);
        ItemStack itemStack1 = itemStack.copy();
        for (int i = 0; i < to.getSlots() && !itemStack1.isEmpty(); i++) {
            itemStack1 = to.insertItem(i, itemStack1, false);
        }
        int count1 = itemStack1.getCount();
        int count = itemStack.getCount();
        if (count1 < count){
            from.extractItem(slot, count - count1, false);
            return count - count1;
        }
        return 0;
    }

    public static int insertNoCheckSlots(ItemStack from, IItemHandler to){
        return insertNoCheckSlots(from,to,false);
    }
    public static int insertNoCheckSlots(ItemStack from, IItemHandler to, boolean simulate){
        ItemStack itemStack1 = from.copy();
        for (int i = 0; i < to.getSlots() && !itemStack1.isEmpty(); i++) {
            itemStack1 = to.insertItem(i, itemStack1, simulate);
        }
        int count1 = itemStack1.getCount();
        int count = from.getCount();
        if (count1 < count){
            return count - count1;
        }
        return 0;
    }
}
