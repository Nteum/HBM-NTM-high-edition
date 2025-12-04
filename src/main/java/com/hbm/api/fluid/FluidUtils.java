package com.hbm.api.fluid;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.*;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidUtils {
    /** 从物品中吸收流体 */
    public static ItemStack absorbFromItem(BlockEntity pBlockEntity, int tank, ItemStack itemStack){
        if (itemStack.isEmpty())return itemStack;
        IFluidHandler fluidHandler = pBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (fluidHandler != null){
            // hbm流体的情况，直接对tank进行输入
            if (fluidHandler instanceof ISidedFluidHandler<?> sidedFluidHandler){
                // 暂时只考虑桶的情况，其他类型容器再考虑
                if (itemStack.getItem() instanceof BucketItem bucket){
                    IExtendedFluidTank fluidTank = sidedFluidHandler.getFluidTank(tank, null);
                    if (fluidTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000){
                        fluidTank.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE);
                        return Items.BUCKET.getDefaultInstance();
                    }else return ItemStack.EMPTY;
                }
            }else {
                // 默认forge流体的情况，不能指定tank直接控制流体
                if (itemStack.getItem() instanceof BucketItem bucket){
                    FluidStack fluidInTank = fluidHandler.getFluidInTank(tank);
                    if (fluidInTank.getFluid().isSame(bucket.getFluid()) || fluidInTank.isEmpty()){
                        if (fluidHandler.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000){
                            fluidHandler.fill(new FluidStack(bucket.getFluid(), 1000), IFluidHandler.FluidAction.EXECUTE);
                            return Items.BUCKET.getDefaultInstance();
                        }else return ItemStack.EMPTY;
                    }else return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static ItemStack absorbFromItem(IFluidHandler fluidHandler, ItemStack itemStack){
        if (itemStack.isEmpty())return itemStack;
        IFluidHandlerItem itemFluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (itemFluidHandler != null){
            int filled = fluidHandler.fill(itemFluidHandler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE), FluidAction.SIMULATE);
            if (filled > 0){
                fluidHandler.fill(itemFluidHandler.drain(Integer.MAX_VALUE, FluidAction.EXECUTE), FluidAction.EXECUTE);
            }
            return itemFluidHandler.getContainer();
        }
        return ItemStack.EMPTY;
    }
    /** 向物品容器中注入流体 */
    public static ItemStack pourToItem(BlockEntity pBlockEntity, int tank, ItemStack itemStack){
        if (itemStack.isEmpty())return itemStack;
        IFluidHandler fluidHandler = pBlockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (fluidHandler != null){
            if (fluidHandler instanceof ISidedFluidHandler<?> sidedFluidHandler){
                if (itemStack.is(Items.BUCKET) && sidedFluidHandler.drain(tank, 1000, FluidAction.SIMULATE).getAmount() == 1000){
                    FluidStack fluidStack = sidedFluidHandler.drain(tank, 1000, FluidAction.EXECUTE);
                    return fluidStack.getFluid().getBucket().getDefaultInstance();
                }
            }else {
                if (itemStack.is(Items.BUCKET) && fluidHandler.drain(1000, FluidAction.SIMULATE).getAmount() == 1000){
                    FluidStack fluidStack = fluidHandler.drain(1000, FluidAction.EXECUTE);
                    return fluidStack.getFluid().getBucket().getDefaultInstance();
                }
            }
        }
        // 无法注入则直接返回
        return itemStack;
    }
    public static int getAmount(ICapabilityProvider provider){
        return getFluid(provider).getAmount();
    }
    public static FluidStack getFluid(ICapabilityProvider provider){
        IFluidHandler fluidHandler = provider.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (fluidHandler != null){
            return fluidHandler.getFluidInTank(0);
        }
        return FluidStack.EMPTY;
    }
    public static FluidStack absorbOnly(ICapabilityProvider provider, int amount){
        return absorbOnly(provider, amount, true);
    }
    public static FluidStack absorbOnly(ICapabilityProvider provider, int amount, boolean allowPartial){
        IFluidHandler fluidHandler = provider.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (fluidHandler != null){
            if (allowPartial)
                return fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
            else if (!allowPartial && fluidHandler.drain(amount, IFluidHandler.FluidAction.SIMULATE).getAmount() == amount)
                return fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        }
        return FluidStack.EMPTY;
    }
}
