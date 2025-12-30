package com.hbm.reactor.rbmk;

import net.minecraft.network.chat.Component;

/**
 * Identifies RBMK peripheral blocks (consoles, reflectors, etc.) so shared logic
 * can expose the correct translations and diagnostics.
 */
public enum RBMKPeripheralType {
    CONSOLE("machine_rbmk_console"),
    ELEMENT("machine_rbmk_element"),
    REFLECTOR("machine_rbmk_reflector"),
    DEBRIS("machine_rbmk_debris"),
    CRANE_CONSOLE("machine_rbmk_crane_console"),
    AUTOLOADER("machine_rbmk_autoloader");

    private final String blockTranslationKey;

    RBMKPeripheralType(final String blockTranslationKey) {
        this.blockTranslationKey = blockTranslationKey;
    }

    public String translationKey() {
        return "block.hbm." + blockTranslationKey;
    }

    public Component displayName() {
        return Component.translatable(translationKey());
    }

    public String registryName() {
        return blockTranslationKey;
    }
}
