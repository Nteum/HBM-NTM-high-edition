package com.hbm.compat.bigexplosives;

import com.hbm.init.BigExplosivesModEntities;
import com.hbm.init.BigExplosivesModItems;
import com.hbm.init.BigExplosivesModParticleTypes;
import com.hbm.init.BigExplosivesModSounds;
import com.hbm.init.BigExplosivesModTabs;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Internal compat bootstrap for the legacy Big Explosives content that used to
 * live under the MCreator namespace. Besides exposing the {@link #MODID} so
 * data-driven assets can keep their identifiers, we also host the lightweight
 * task scheduler that many generated procedures still call into.
 */
public final class BigExplosivesMod {

    public static final String MODID = "big_explosives";
    public static final Logger LOGGER = LogManager.getLogger("BigExplosives");

    private static final ServerScheduler SERVER_SCHEDULER = new ServerScheduler();

    private BigExplosivesMod() {
    }

    /**
     * Registers the remaining deferred registers that still expose useful
     * content from the legacy data pack.
     */
    public static void register(final IEventBus modEventBus) {
        BigExplosivesModItems.REGISTRY.register(modEventBus);
        BigExplosivesModEntities.REGISTRY.register(modEventBus);
        BigExplosivesModParticleTypes.REGISTRY.register(modEventBus);
        BigExplosivesModSounds.REGISTRY.register(modEventBus);
        BigExplosivesModTabs.REGISTRY.register(modEventBus);
    }

    /**
     * Schedules a runnable to execute on the logical server thread after the
     * specified amount of ticks. Multiple tasks now share a single dispatcher
     * instance which drastically reduces event-bus churn compared to the old
     * one-listener-per-task approach from the MCreator export.
     */
    public static void queueServerWork(final int ticks, final Runnable action) {
        SERVER_SCHEDULER.submit(ticks, Objects.requireNonNull(action, "action"));
    }

    private static final class ServerScheduler {
        private final Object lock = new Object();
        private final List<ScheduledTask> tasks = new ArrayList<>();
        private boolean subscribed;

        void submit(final int ticks, final Runnable action) {
            if (ticks <= 0) {
                action.run();
                return;
            }
            synchronized (lock) {
                tasks.add(new ScheduledTask(ticks, action));
                ensureSubscribed();
            }
        }

        private void ensureSubscribed() {
            if (!subscribed) {
                MinecraftForge.EVENT_BUS.register(this);
                subscribed = true;
            }
        }

        @SubscribeEvent
        public void onServerTick(final TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            synchronized (lock) {
                final Iterator<ScheduledTask> iterator = tasks.iterator();
                while (iterator.hasNext()) {
                    final ScheduledTask task = iterator.next();
                    if (!task.shouldRun()) {
                        continue;
                    }
                    try {
                        task.run();
                    } catch (Throwable ex) {
                        LOGGER.error("Big Explosives scheduled task failed", ex);
                    } finally {
                        iterator.remove();
                    }
                }
                if (tasks.isEmpty() && subscribed) {
                    MinecraftForge.EVENT_BUS.unregister(this);
                    subscribed = false;
                }
            }
        }
    }

    private static final class ScheduledTask {
        private int remainingTicks;
        private final Runnable action;

        private ScheduledTask(final int remainingTicks, final Runnable action) {
            this.remainingTicks = remainingTicks;
            this.action = action;
        }

        boolean shouldRun() {
            return --remainingTicks <= 0;
        }

        void run() {
            action.run();
        }
    }
}
