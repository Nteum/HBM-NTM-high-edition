package com.hbm.item.tool;

import com.hbm.Inventory.fluid.ExtendedFluidType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Supplier;

public class FluidBucketItem extends BucketItem {
    public FluidBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder.stacksTo(16));
    }
    /**
     * 用于给流体桶渲染对应颜色
     * */
    public static int getColor(ItemStack pStack, int pTintIndex){
        // tintIndex 对应 layerX（layer0=0, layer1=1, ...）
        if (pTintIndex == 1){
            // 获取流体颜色，这里举例从 NBT 读取流体
            if (pStack.getItem() instanceof FluidBucketItem item){
                if (item.getFluid().getFluidType() instanceof ExtendedFluidType fluidType1){
                    return fluidType1.tintColor;
                }
            }
            return 0xFFFFFFFF; // 默认白色
        }else
            return 0xFFFFFFFF; // 不染色的层返回白色
    }
}
