package com.hbm.reactor.pile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Keeps track of per-level neutron caches and pending stream lists. Every
 * tick the {@link StreamWorld} for each server level is asked to process and
 * then it clears the queued streams.
 */
public final class NeutronNodeWorld {

    private static final Map<Level, StreamWorld> STREAM_WORLDS = new IdentityHashMap<>();

    private NeutronNodeWorld() {
    }

    public static StreamWorld getOrCreate(final Level level) {
        return STREAM_WORLDS.computeIfAbsent(level, ignored -> new StreamWorld());
    }

    public static void remove(final Level level) {
        STREAM_WORLDS.remove(level);
    }

    public static void clear() {
        STREAM_WORLDS.clear();
    }

    public static Collection<Map.Entry<Level, StreamWorld>> worlds() {
        return Collections.unmodifiableCollection(STREAM_WORLDS.entrySet());
    }

    public static final class StreamWorld {

        private final List<NeutronStream> streams = new ArrayList<>();
        private final Map<BlockPos, NeutronNode> nodeCache = new HashMap<>();

        public void queue(NeutronStream stream) {
            streams.add(stream);
        }

        public void runStreams(Level level) {
            if (streams.isEmpty()) {
                return;
            }
            for (NeutronStream stream : streams) {
                stream.run(level, this);
            }
            streams.clear();
        }

        public NeutronNode getNode(BlockPos pos) {
            return nodeCache.get(pos);
        }

        public void addNode(NeutronNode node) {
            nodeCache.put(node.pos(), node);
        }

        public void removeNode(BlockPos pos) {
            nodeCache.remove(pos);
        }

        public void removeDeadNodes() {
            nodeCache.entrySet().removeIf(entry -> entry.getValue().blockEntity().isRemoved());
        }
    }
}
