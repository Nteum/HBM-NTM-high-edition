package com.hbm.reactor.rbmk;

import net.minecraft.network.chat.Component;

public enum RBMKScreenType {
    NONE(0),
    COL_TEMP(18),
    ROD_EXTRACTION(36),
    FUEL_DEPLETION(54),
    FUEL_POISON(72),
    FUEL_TEMP(90);

    private final int spriteU;

    RBMKScreenType(final int spriteU) {
        this.spriteU = spriteU;
    }

    public int spriteU() {
        return spriteU;
    }

    public RBMKScreenType next() {
        RBMKScreenType[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public String translationKey() {
        return "gui.hbm.rbmk.screen." + name().toLowerCase(java.util.Locale.ROOT);
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
