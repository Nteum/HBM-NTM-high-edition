package net.mcreator.nuclearcraft;

import net.mcreator.nuclearcraft.init.BigExplosivesModEntities;
import net.mcreator.nuclearcraft.init.BigExplosivesModItems;
import net.mcreator.nuclearcraft.init.BigExplosivesModParticleTypes;
import net.mcreator.nuclearcraft.init.BigExplosivesModSounds;
import net.mcreator.nuclearcraft.init.BigExplosivesModTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lightweight compatibility bootstrap for the legacy Big Explosives content.
 * <p>
 * The original content was generated via MCreator and relied on a standalone
 * {@code BigExplosivesMod} entrypoint. For the HBM integration we expose only
 * the pieces we still need: registration of the remaining data-driven content
 * and a tiny task scheduler used by many of the generated procedures.
 */
public final class BigExplosivesMod {

    public static final String MODID = "big_explosives";
    public static final Logger LOGGER = LogManager.getLogger("BigExplosives");

    private BigExplosivesMod() {
    }

    /**
     * Registers all deferred registers that still provide usable content.
     */
    public static void register(final IEventBus modEventBus) {
        BigExplosivesModItems.REGISTRY.register(modEventBus);
        BigExplosivesModEntities.REGISTRY.register(modEventBus);
        BigExplosivesModParticleTypes.REGISTRY.register(modEventBus);
        BigExplosivesModSounds.REGISTRY.register(modEventBus);
        BigExplosivesModTabs.REGISTRY.register(modEventBus);
    }

    /**
     * Schedules a runnable to execute on the server thread after the specified
     * number of ticks. This mirrors the helper available in the original mod so
     * the generated procedures continue to function.
     */
    public static void queueServerWork(final int ticks, final Runnable action) {
        if (ticks <= 0) {
            action.run();
            return;
        }
        MinecraftForge.EVENT_BUS.register(new DelayedTask(ticks, action));
    }

    private static final class DelayedTask {
        private int waitTicks;
        private final Runnable action;

        private DelayedTask(final int waitTicks, final Runnable action) {
            this.waitTicks = waitTicks;
            this.action = action;
        }

        @SubscribeEvent
        public void onServerTick(final TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            if (--waitTicks > 0) {
                return;
            }
            try {
                action.run();
            } finally {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
