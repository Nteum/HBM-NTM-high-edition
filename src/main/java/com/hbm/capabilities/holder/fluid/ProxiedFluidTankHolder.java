package com.hbm.capabilities.holder.fluid;


import com.hbm.api.fluid.mek.IExtendedFluidTank;
import com.hbm.capabilities.holder.ProxiedHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
/**
 * 流体代理
 * 似乎默认里面只能有一个流体槽
 * */
public class ProxiedFluidTankHolder extends ProxiedHolder implements IFluidTankHolder {

    private final Function<Direction, List<IExtendedFluidTank>> tankFunction;

    public static ProxiedFluidTankHolder create(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IExtendedFluidTank>> tankFunction) {
        return new ProxiedFluidTankHolder(insertPredicate, extractPredicate, tankFunction);
    }

    private ProxiedFluidTankHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
                                   Function<Direction, List<IExtendedFluidTank>> tankFunction) {
        super(insertPredicate, extractPredicate);
        this.tankFunction = tankFunction;
    }

    @NotNull
    @Override
    public List<IExtendedFluidTank> getTanks(@Nullable Direction side) {
        return tankFunction.apply(side);
    }
}