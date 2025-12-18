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
    private final RBMKSettings settings;
    private RBMKLidType lidType;
    private double controlRodInsertion;
    private double heat;
    private boolean meltedDown;

    RBMKColumnState(final BlockPos corePosition, final RBMKSettings settings, final RBMKLidType lidType) {
        this.corePosition = Objects.requireNonNull(corePosition, "corePosition");
        this.settings = Objects.requireNonNull(settings, "settings");
        this.lidType = lidType;
    }

    public BlockPos corePosition() {
        return corePosition;
    }

    public int columnHeight() {
        return settings.columnHeight();
    }

    public RBMKSettings settings() {
        return settings;
    }

    public RBMKLidType lidType() {
        return lidType;
    }

    public void setLidType(final RBMKLidType lidType) {
        this.lidType = lidType;
    }

    public double controlRodInsertion() {
        return controlRodInsertion;
    }

    public void setControlRodInsertion(final double controlRodInsertion) {
        if (controlRodInsertion < 0.0D) {
            this.controlRodInsertion = 0.0D;
        } else if (controlRodInsertion > 1.0D) {
            this.controlRodInsertion = 1.0D;
        } else {
            this.controlRodInsertion = controlRodInsertion;
        }
    }

    public double heat() {
        return heat;
    }

    public void addHeat(final double delta) {
        heat = Math.max(0.0D, heat + delta);
    }

    public boolean meltedDown() {
        return meltedDown;
    }

    public void markMeltedDown() {
        this.meltedDown = true;
    }

    @Override
    public String toString() {
        return "RBMKColumnState{" +
                "corePosition=" + corePosition +
                ", settings=" + settings +
                ", lidType=" + lidType +
                ", controlRodInsertion=" + controlRodInsertion +
                ", heat=" + heat +
                ", meltedDown=" + meltedDown +
                '}';
    }
}
