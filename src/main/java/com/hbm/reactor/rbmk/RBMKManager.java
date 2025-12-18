package com.hbm.reactor.rbmk;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Entry point for the new RBMK service layer. It currently tracks per-level
 * {@link RBMKLevelContext} instances and wipes them when levels unload or the
 * server stops. Later on it will also host the neutron simulation scheduler.
 */
public final class RBMKManager {

    private static final Map<ResourceKey<Level>, RBMKLevelContext> CONTEXTS = new ConcurrentHashMap<>();
    private static boolean registered;

    private RBMKManager() {
    }

    public static void init() {
        if (registered) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(new Events());
        registered = true;
    }

    public static RBMKLevelContext context(final ServerLevel level) {
        return CONTEXTS.computeIfAbsent(level.dimension(), key -> new RBMKLevelContext(level));
    }

    public static void remove(final ServerLevel level) {
        CONTEXTS.remove(level.dimension());
    }

    public static void clear() {
        CONTEXTS.clear();
    }

    private static final class Events {

        @SubscribeEvent
        public void onLevelUnload(final LevelEvent.Unload event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                RBMKManager.remove(serverLevel);
            }
        }

        @SubscribeEvent
        public void onServerStopped(final ServerStoppedEvent event) {
            RBMKManager.clear();
        }

        @SubscribeEvent
        public void onLevelTick(final TickEvent.LevelTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            if (event.level instanceof ServerLevel serverLevel) {
                final RBMKLevelContext context = CONTEXTS.get(serverLevel.dimension());
                if (context != null) {
                    context.tick();
                }
            }
        }
    }
}
