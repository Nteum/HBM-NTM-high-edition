package com.hbm.utils.transport_net;

import com.hbm.block.logistic.AbstractPipeBlock;
import com.hbm.blockentity.machine.PipeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maintains coherent pipe graphs per level. Earlier revisions stored pending work
 * lists but never applied them, which meant networks were always null and any pipe
 * placement crashed the game. This implementation eagerly updates membership so the
 * system is deterministic and safe during runClient sessions.
 */
public class FluidNetworkSystem {

    public static final Map<Level, FluidNetworkSystem> INSTANCES = new HashMap<>();

    private final Level level;
    private final Map<BlockPos, FluidNetwork> pipeIndex = new HashMap<>();
    private final List<FluidNetwork> networks = new ArrayList<>();

    private FluidNetworkSystem(final Level level) {
        this.level = level;
    }

    public static FluidNetworkSystem getOrCreate(final Level level) {
        return INSTANCES.computeIfAbsent(level, FluidNetworkSystem::new);
    }

    public void tick() {
        if (networks.isEmpty()) {
            return;
        }
        networks.forEach(FluidNetwork::tick);
    }

    /**
     * Ensures a pipe at the given position participates in a network. The pipe will
     * either form its own cluster or merge with any compatible neighbours.
     */
    public void rebuildNetwork(final BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        removeFromCurrent(pos);
        if (!(level.getBlockState(pos).getBlock() instanceof AbstractPipeBlock)) {
            return;
        }
        Set<FluidNetwork> neighbouring = collectNeighbourNetworks(pos);
        if (neighbouring.isEmpty()) {
            create(pos);
            return;
        }
        FluidNetwork primary = neighbouring.iterator().next();
        join(pos, primary);
        if (neighbouring.size() > 1) {
            connect(neighbouring.toArray(new FluidNetwork[0]));
        }
    }

    public void load(final ChunkPos chunkPos, final BlockPos pos) {
        rebuildNetwork(pos);
    }

    public void unload(final ChunkPos chunkPos, final BlockPos pos) {
        leave(pos);
    }

    public void create(final BlockPos pos) {
        FluidNetwork network = new FluidNetwork();
        network.addTransmitter(pos);
        networks.add(network);
        pipeIndex.put(pos, network);
        bindPipe(pos, network);
    }

    public void join(final BlockPos pos, final FluidNetwork target) {
        if (target == null) {
            create(pos);
            return;
        }
        removeFromCurrent(pos);
        target.addTransmitter(pos);
        pipeIndex.put(pos, target);
        bindPipe(pos, target);
    }

    public void leave(final BlockPos pos) {
        FluidNetwork current = pipeIndex.get(pos);
        if (current == null) {
            bindPipe(pos, null);
            return;
        }
        removeFromCurrent(pos);
        if (current.isEmpty()) {
            networks.remove(current);
        } else {
            split(current);
        }
    }

    public void connect(final FluidNetwork... candidates) {
        if (candidates == null || candidates.length <= 1) {
            return;
        }
        FluidNetwork primary = null;
        for (FluidNetwork candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            if (primary == null) {
                primary = candidate;
                continue;
            }
            if (!primary.canMergeWith(candidate)) {
                continue;
            }
            transfer(candidate, primary);
        }
    }

    public void split(final FluidNetwork network) {
        if (network == null || network.isEmpty()) {
            return;
        }
        List<Set<BlockPos>> components = findComponents(network);
        if (components.size() <= 1) {
            return;
        }
        Iterator<Set<BlockPos>> iterator = components.iterator();
        Set<BlockPos> base = iterator.next();
        network.replaceTransmitters(base);
        base.forEach(pos -> pipeIndex.put(pos, network));
        bindAll(base, network);
        while (iterator.hasNext()) {
            Set<BlockPos> cluster = iterator.next();
            FluidNetwork splitNetwork = new FluidNetwork();
            splitNetwork.setFluid(network.getFluid());
            cluster.forEach(splitNetwork::addTransmitter);
            networks.add(splitNetwork);
            cluster.forEach(pos -> {
                pipeIndex.put(pos, splitNetwork);
                bindPipe(pos, splitNetwork);
            });
        }
    }

    private void transfer(final FluidNetwork source, final FluidNetwork target) {
        if (source == null || source == target) {
            return;
        }
        source.getTransmitters().forEach(pos -> {
            target.addTransmitter(pos);
            pipeIndex.put(pos, target);
            bindPipe(pos, target);
        });
        if (target.getFluid() == Fluids.EMPTY) {
            target.setFluid(source.getFluid());
        }
        networks.remove(source);
    }

    private void removeFromCurrent(final BlockPos pos) {
        FluidNetwork network = pipeIndex.remove(pos);
        if (network == null) {
            bindPipe(pos, null);
            return;
        }
        network.removeTransmitter(pos);
        bindPipe(pos, null);
    }

    private Set<FluidNetwork> collectNeighbourNetworks(final BlockPos pos) {
        Set<FluidNetwork> networks = new LinkedHashSet<>();
        for (Direction direction : Direction.values()) {
            FluidNetwork neighbour = pipeIndex.get(pos.relative(direction));
            if (neighbour != null) {
                networks.add(neighbour);
            }
        }
        return networks;
    }

    private List<Set<BlockPos>> findComponents(final FluidNetwork source) {
        Set<BlockPos> remaining = new HashSet<>(source.getTransmitters());
        List<Set<BlockPos>> result = new ArrayList<>();
        while (!remaining.isEmpty()) {
            BlockPos start = remaining.iterator().next();
            Set<BlockPos> component = new HashSet<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);
            remaining.remove(start);
            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();
                component.add(current);
                BlockState state = level.getBlockState(current);
                if (!(state.getBlock() instanceof AbstractPipeBlock)) {
                    continue;
                }
                for (Direction direction : Direction.values()) {
                    BooleanProperty prop = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
                    if (!state.hasProperty(prop) || !state.getValue(prop)) {
                        continue;
                    }
                    BlockPos next = current.relative(direction);
                    if (!remaining.remove(next)) {
                        continue;
                    }
                    queue.add(next);
                }
            }
            result.add(component);
        }
        return result;
    }

    private void bindPipe(final BlockPos pos, final FluidNetwork network) {
        if (!(level.getBlockEntity(pos) instanceof PipeEntity pipeEntity)) {
            return;
        }
        pipeEntity.network = network;
    }

    private void bindAll(final Set<BlockPos> positions, final FluidNetwork network) {
        positions.forEach(pos -> bindPipe(pos, network));
    }
}
