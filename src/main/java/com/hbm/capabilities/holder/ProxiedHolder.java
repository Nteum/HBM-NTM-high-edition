package com.hbm.capabilities.holder;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
/**
 * 最简单的实现，仅仅用于控制每个面是否可以交互
 * */
public abstract class ProxiedHolder implements IHolder {

    private final Predicate<Direction> insertPredicate;
    private final Predicate<Direction> extractPredicate;

    protected ProxiedHolder(Predicate<Direction> insertPredicate, Predicate<Direction> extractPredicate) {
        this.insertPredicate = insertPredicate;
        this.extractPredicate = extractPredicate;
    }

    @Override
    public boolean canInsert(@Nullable Direction side) {
        return insertPredicate.test(side);
    }

    @Override
    public boolean canExtract(@Nullable Direction side) {
        return extractPredicate.test(side);
    }
}