package com.hbm.reactor.rbmk;

import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * Lightweight snapshot describing a single RBMK column core. The heavy
 * simulation logic will live elsewhere; this record simply tracks structural
 * data so that block entities can query or update state without depending on
 * the original 1.7.10 classes.
 */
public final class RBMKColumnState {

    private final BlockPos corePosition;
    private final int columnHeight;
    private RBMKLidType lidType;

    RBMKColumnState(final BlockPos corePosition, final int columnHeight, final RBMKLidType lidType) {
        this.corePosition = Objects.requireNonNull(corePosition, "corePosition");
        this.columnHeight = columnHeight;
        this.lidType = lidType;
    }

    public BlockPos corePosition() {
        return corePosition;
    }

    public int columnHeight() {
        return columnHeight;
    }

    public RBMKLidType lidType() {
        return lidType;
    }

    public void setLidType(final RBMKLidType lidType) {
        this.lidType = lidType;
    }

    @Override
    public String toString() {
        return "RBMKColumnState{" +
                "corePosition=" + corePosition +
                ", columnHeight=" + columnHeight +
                ", lidType=" + lidType +
                '}';
    }
}
