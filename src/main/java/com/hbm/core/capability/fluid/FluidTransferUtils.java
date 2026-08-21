package com.hbm.core.capability.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import java.util.List;

/**
 * 流体功能工具类，全部基于 Forge 标准 IFluidHandler。
 * 提供物品流体交互、容器间传输、多容器输出等常用功能。
 */
public class FluidTransferUtils {
    private FluidTransferUtils() {}

    //====================== 物品交互 ======================

    /** 从物品中抽取流体并注入机器（物品 -> 机器）。成功则返回处理后的物品，失败返回原物品。 */
    public static ItemStack extractFromItem(IFluidHandler machine, ItemStack itemStack){
        if (machine == null || itemStack == null || itemStack.isEmpty()) return itemStack;
        IFluidHandlerItem itemFluid = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (itemFluid == null) return itemStack;
        FluidStack toDrain = itemFluid.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        if (toDrain.isEmpty()) return itemStack;
        int filled = machine.fill(toDrain, FluidAction.SIMULATE);
        if (filled <= 0) return itemStack;
        itemFluid.drain(filled, FluidAction.EXECUTE);
        machine.fill(new FluidStack(toDrain, filled), FluidAction.EXECUTE);
        return itemFluid.getContainer();
    }
    /** 从机器中抽取流体并注入物品（机器 -> 物品）。成功则返回处理后的物品，失败返回原物品。 */
    public static ItemStack pourToItem(IFluidHandler machine, ItemStack itemStack){
        if (machine == null || itemStack == null || itemStack.isEmpty()) return itemStack;
        IFluidHandlerItem itemFluid = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (itemFluid == null) return itemStack;
        FluidStack toFill = machine.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        if (toFill.isEmpty()) return itemStack;
        int filled = itemFluid.fill(toFill, FluidAction.SIMULATE);
        if (filled <= 0) return itemStack;
        machine.drain(new FluidStack(toFill, filled), FluidAction.EXECUTE);
        itemFluid.fill(new FluidStack(toFill, filled), FluidAction.EXECUTE);
        return itemFluid.getContainer();
    }

    //====================== 容器间传输 ======================

    /** 将源容器的流体输出到目标容器，不限数量，尽可能传输。返回实际传输量。 */
    public static int push(IFluidHandler from, IFluidHandler to){
        return push(from, to, Integer.MAX_VALUE);
    }
    /** 将源容器的流体输出到目标容器，限定数量。返回实际传输量。 */
    public static int push(IFluidHandler from, IFluidHandler to, int maxAmount){
        if (from == null || to == null || maxAmount <= 0) return 0;
        FluidStack toPush = from.drain(maxAmount, FluidAction.SIMULATE);
        if (toPush.isEmpty()) return 0;
        int filled = to.fill(toPush, FluidAction.SIMULATE);
        if (filled <= 0) return 0;
        from.drain(new FluidStack(toPush, filled), FluidAction.EXECUTE);
        to.fill(new FluidStack(toPush, filled), FluidAction.EXECUTE);
        return filled;
    }
    /** 从目标容器拉取流体到本容器，不限数量。返回实际传输量。 */
    public static int pull(IFluidHandler to, IFluidHandler from){
        return push(from, to);
    }
    /** 从目标容器拉取流体到本容器，限定数量。返回实际传输量。 */
    public static int pull(IFluidHandler to, IFluidHandler from, int maxAmount){
        return push(from, to, maxAmount);
    }
    /** 将源容器的流体输出到多个容器，依次填充直到耗尽。返回实际传输总量。 */
    public static int pushToMultiple(IFluidHandler from, List<IFluidHandler> targets){
        return pushToMultiple(from, targets, Integer.MAX_VALUE);
    }
    /** 将源容器的流体输出到多个容器，限定总数量。返回实际传输总量。 */
    public static int pushToMultiple(IFluidHandler from, List<IFluidHandler> targets, int maxAmount){
        if (from == null || targets == null || targets.isEmpty() || maxAmount <= 0) return 0;
        int total = 0;
        for (IFluidHandler target : targets){
            if (total >= maxAmount) break;
            total += push(from, target, maxAmount - total);
        }
        return total;
    }

    //====================== BlockEntity 便捷方法 ======================

    /** 从方块获取流体 handler。 */
    public static IFluidHandler getFluidHandler(BlockEntity be){
        return be == null ? null : be.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
    }
}
