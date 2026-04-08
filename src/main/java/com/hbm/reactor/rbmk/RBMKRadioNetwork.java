package com.hbm.reactor.rbmk;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Lightweight server-side radio bus used by RBMK keypad/gauge/controller
 * components. Signals are short-lived and keyed by world + channel.
 */
public final class RBMKRadioNetwork {

    private static final long RETENTION_TICKS = 20L * 30L;
    private static final Map<ResourceKey<Level>, Map<String, Signal>> SIGNALS = new HashMap<>();

    private RBMKRadioNetwork() {
    }

    public static void broadcast(ServerLevel level, String channel, String signal) {
        String key = sanitizeChannel(channel);
        String payload = sanitizeSignal(signal);
        if (key.isEmpty() || payload.isEmpty()) {
            return;
        }

        Map<String, Signal> dimensionSignals = SIGNALS.computeIfAbsent(level.dimension(), unused -> new HashMap<>());
        long tick = level.getGameTime();
        dimensionSignals.put(key, new Signal(payload, tick));

        if (tick % 200L == 0L) {
            purge(level.dimension(), tick);
        }
    }

    public static Signal listen(ServerLevel level, String channel) {
        String key = sanitizeChannel(channel);
        if (key.isEmpty()) {
            return null;
        }
        Map<String, Signal> dimensionSignals = SIGNALS.get(level.dimension());
        if (dimensionSignals == null) {
            return null;
        }
        Signal signal = dimensionSignals.get(key);
        if (signal == null) {
            return null;
        }
        if (signal.tick() + RETENTION_TICKS < level.getGameTime()) {
            dimensionSignals.remove(key);
            if (dimensionSignals.isEmpty()) {
                SIGNALS.remove(level.dimension());
            }
            return null;
        }
        return signal;
    }

    private static void purge(ResourceKey<Level> dimension, long currentTick) {
        Map<String, Signal> dimensionSignals = SIGNALS.get(dimension);
        if (dimensionSignals == null || dimensionSignals.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<String, Signal>> iterator = dimensionSignals.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Signal> entry = iterator.next();
            if (entry.getValue().tick() + RETENTION_TICKS < currentTick) {
                iterator.remove();
            }
        }
        if (dimensionSignals.isEmpty()) {
            SIGNALS.remove(dimension);
        }
    }

    private static String sanitizeChannel(String channel) {
        return channel == null ? "" : channel.trim();
    }

    private static String sanitizeSignal(String signal) {
        return signal == null ? "" : signal.trim();
    }

    public record Signal(String signal, long tick) {
    }
}
