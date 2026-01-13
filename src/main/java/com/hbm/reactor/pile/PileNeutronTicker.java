package com.hbm.reactor.pile;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Small event hook that drives the Chicago Pile neutron simulation. RBMK uses
 * its own manager, so this class is purpose-built for the pile blocks.
 */
public final class PileNeutronTicker {

    private static boolean registered;

    private PileNeutronTicker() {
    }

    public static void init() {
        if (registered) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(new Events());
        registered = true;
    }

    private static final class Events {

        private int cacheTicker;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            boolean cleanup = ++cacheTicker >= 20;
            if (cleanup) {
                cacheTicker = 0;
            }

            for (var entry : NeutronNodeWorld.worlds()) {
                entry.getValue().runStreams(entry.getKey());
                if (cleanup) {
                    entry.getValue().removeDeadNodes();
                }
            }
        }

        @SubscribeEvent
        public void onLevelUnload(LevelEvent.Unload event) {
            if (!event.getLevel().isClientSide()) {
                NeutronNodeWorld.remove((net.minecraft.world.level.Level) event.getLevel());
            }
        }
    }
}
