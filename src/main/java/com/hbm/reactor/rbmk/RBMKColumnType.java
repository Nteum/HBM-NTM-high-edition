package com.hbm.reactor.rbmk;

import net.minecraft.network.chat.Component;

/**
 * Legacy RBMK console/DODD column categories.
 */
public enum RBMKColumnType {
    BLANK(0, "rbmk_blank"),
    FUEL(10, "rbmk_rod"),
    FUEL_SIM(90, "rbmk_rod_reasim"),
    CONTROL(20, "rbmk_control"),
    CONTROL_AUTO(30, "rbmk_control_auto"),
    BOILER(40, "rbmk_boiler"),
    MODERATOR(50, "rbmk_moderator"),
    ABSORBER(60, "rbmk_absorber"),
    REFLECTOR(70, "rbmk_reflector"),
    OUTGASSER(80, "rbmk_outgasser"),
    BREEDER(100, "rbmk_breeder"),
    STORAGE(110, "rbmk_storage"),
    COOLER(120, "rbmk_cooler"),
    HEATEX(130, "rbmk_heatex");

    private final int consoleSpriteU;
    private final String legacyName;

    RBMKColumnType(final int consoleSpriteU, final String legacyName) {
        this.consoleSpriteU = consoleSpriteU;
        this.legacyName = legacyName;
    }

    public int consoleSpriteU() {
        return consoleSpriteU;
    }

    public String legacyName() {
        return legacyName;
    }

    public String translationKey() {
        return "gui.hbm.rbmk.column." + name().toLowerCase(java.util.Locale.ROOT);
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }
}
