package com.hbm.compat.ballistix;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Minimal bootstrap that exposes the Ballistix namespace and registers the
 * small set of ported explosives.
 */
public final class BallistixCompat {

    public static final String MODID = "ballistix";

    private BallistixCompat() {
    }

    public static void register(final IEventBus modEventBus) {
        BallistixBlocks.REGISTRY.register(modEventBus);
        BallistixItems.REGISTRY.register(modEventBus);
        BallistixEntities.REGISTRY.register(modEventBus);
        BallistixTabs.REGISTRY.register(modEventBus);
    }
}
