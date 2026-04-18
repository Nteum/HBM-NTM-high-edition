package com.hbm.reactor.rbmk;

import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Compact metric set used by external RBMK numeric displays and future graph
 * panels. Values are taken directly from a single column snapshot.
 */
public enum RBMKMonitorMetric {
    HEAT("HT"),
    TOTAL_FLUX("FX"),
    CONTROL("CR"),
    WATER("H2O"),
    STEAM("STM"),
    XENON("XE"),
    DEPLETION("DEP"),
    CORE_HEAT("FUEL");

    private final String shortLabel;

    RBMKMonitorMetric(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public String shortLabel() {
        return shortLabel;
    }

    public Component displayName() {
        return Component.literal(shortLabel);
    }

    public RBMKMonitorMetric next() {
        RBMKMonitorMetric[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public int sampleValue(RBMKColumnState state) {
        return switch (this) {
            case HEAT -> Math.round((float) state.heat());
            case TOTAL_FLUX -> Math.round((float) (state.fastFlux() + state.slowFlux()));
            case CONTROL -> Math.round((float) (state.controlRodInsertion() * 100.0D));
            case WATER -> state.waterAmount();
            case STEAM -> state.steamAmount();
            case XENON -> Math.round((float) state.xenon());
            case DEPLETION -> Math.round((float) ((1.0D - state.enrichment()) * 100.0D));
            case CORE_HEAT -> Math.round((float) state.coreHeat());
        };
    }

    public String format(RBMKColumnState state) {
        return switch (this) {
            case HEAT, TOTAL_FLUX, WATER, STEAM, CORE_HEAT -> String.format(Locale.ROOT, "%04d", sampleValue(state));
            case CONTROL, XENON, DEPLETION -> String.format(Locale.ROOT, "%03d%%", sampleValue(state));
        };
    }

    public int color(RBMKColumnState state) {
        return switch (this) {
            case HEAT, CORE_HEAT -> state.heat() / Math.max(1.0D, state.maxHeat()) > 0.85D ? 0xFF5555 : 0x55FF55;
            case TOTAL_FLUX -> 0x66FF66;
            case CONTROL -> 0x66FFFF;
            case WATER -> 0x6EC6FF;
            case STEAM -> 0xF0F0F0;
            case XENON -> 0xFFD166;
            case DEPLETION -> 0xFF9F43;
        };
    }
}
