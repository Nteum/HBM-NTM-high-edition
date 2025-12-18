package com.hbm.reactor.rbmk;

import com.hbm.config.ConfigRBMK;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/**
 * Simplified configuration holder for RBMK-style multiblock columns.
 * <p>
 *     The original 1.7.10 code exposed dozens of "dial" gamerules. To get the
 *     modern port moving without blocking on a full rules port, we keep the
 *     key knobs here with sane defaults and expose a refresh hook so that
 *     future work can wire them into config files or gamerules again.
 * </p>
 */
public final class RBMKSettings {

    public static final RBMKSettings DEFAULT = builder().build();

    private final int columnHeight;
    private final double passiveCooling;
    private final double columnHeatFlow;
    private final double reactivityModifier;
    private final double meltdownHeat;
    private final double meltdownExplosionStrength;

    private RBMKSettings(final Builder builder) {
        this.columnHeight = builder.columnHeight;
        this.passiveCooling = builder.passiveCooling;
        this.columnHeatFlow = builder.columnHeatFlow;
        this.reactivityModifier = builder.reactivityModifier;
        this.meltdownHeat = builder.meltdownHeat;
        this.meltdownExplosionStrength = builder.meltdownExplosionStrength;
    }

    public int columnHeight() {
        return columnHeight;
    }

    public double passiveCooling() {
        return passiveCooling;
    }

    public double columnHeatFlow() {
        return columnHeatFlow;
    }

    public double reactivityModifier() {
        return reactivityModifier;
    }

    public double meltdownHeat() {
        return meltdownHeat;
    }

    public double meltdownExplosionStrength() {
        return meltdownExplosionStrength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder()
                .columnHeight(columnHeight)
                .passiveCooling(passiveCooling)
                .columnHeatFlow(columnHeatFlow)
                .reactivityModifier(reactivityModifier)
                .meltdownHeat(meltdownHeat)
                .meltdownExplosionStrength(meltdownExplosionStrength);
    }

    /**
     * Produces a settings object for the given level. For now we just return
     * {@link #DEFAULT}, but the pluggable hook lets us swap in per-world config
     * later without touching call sites.
     */
    public static RBMKSettings forLevel(final ServerLevel level) {
        Objects.requireNonNull(level, "level");
        return builder()
                .columnHeight(ConfigRBMK.columnHeight)
                .passiveCooling(ConfigRBMK.passiveCooling)
                .columnHeatFlow(ConfigRBMK.columnHeatFlow)
                .reactivityModifier(ConfigRBMK.reactivityModifier)
                .meltdownHeat(ConfigRBMK.meltdownHeat)
                .meltdownExplosionStrength(ConfigRBMK.meltdownExplosionStrength)
                .build();
    }

    public static final class Builder {
        private int columnHeight = 4;
        private double passiveCooling = 1.0D;
        private double columnHeatFlow = 0.2D;
        private double reactivityModifier = 1.0D;
        private double meltdownHeat = 10_000.0D;
        private double meltdownExplosionStrength = 6.0D;

        private Builder() {
        }

        public Builder columnHeight(final int columnHeight) {
            this.columnHeight = Math.max(2, columnHeight);
            return this;
        }

        public Builder passiveCooling(final double passiveCooling) {
            this.passiveCooling = Math.max(0.0D, passiveCooling);
            return this;
        }

        public Builder columnHeatFlow(final double columnHeatFlow) {
            this.columnHeatFlow = Math.max(0.0D, Math.min(1.0D, columnHeatFlow));
            return this;
        }

        public Builder reactivityModifier(final double reactivityModifier) {
            this.reactivityModifier = Math.max(0.0D, reactivityModifier);
            return this;
        }

        public Builder meltdownHeat(final double meltdownHeat) {
            this.meltdownHeat = Math.max(0.0D, meltdownHeat);
            return this;
        }

        public Builder meltdownExplosionStrength(final double meltdownExplosionStrength) {
            this.meltdownExplosionStrength = Math.max(0.0D, meltdownExplosionStrength);
            return this;
        }

        public RBMKSettings build() {
            return new RBMKSettings(this);
        }
    }
}
