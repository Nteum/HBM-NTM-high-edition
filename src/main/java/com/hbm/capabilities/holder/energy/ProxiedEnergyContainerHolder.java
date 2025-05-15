package com.hbm.capabilities.holder.energy;

import com.hbm.api.energy.fe.IHBMEnergyStorage;
import com.hbm.capabilities.holder.ProxiedHolder;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ProxiedEnergyContainerHolder extends ProxiedHolder implements IEnergyContainerHolder {

    private final Function<Direction, List<IHBMEnergyStorage>> containerFunction;

    public static ProxiedEnergyContainerHolder create(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
          Function<Direction, List<IHBMEnergyStorage>> containerFunction) {
        return new ProxiedEnergyContainerHolder(insertPredicate, extractPredicate, containerFunction);
    }

    public ProxiedEnergyContainerHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate,
                                        Function<Direction, List<IHBMEnergyStorage>> containerFunction) {
        super(insertPredicate, extractPredicate);
        this.containerFunction = containerFunction;
    }

    @NotNull
    @Override
    public List<IHBMEnergyStorage> getEnergyContainers(@Nullable Direction side) {
        return containerFunction.apply(side);
    }
}