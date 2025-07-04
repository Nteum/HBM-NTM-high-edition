package com.hbm.capabilities.network.cache;

import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.capabilities.network.transmitter.Transmitter;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;
import java.util.Map.Entry;
/**
 * from: mek
 * 网络的所有接收者，它和AcceptorCache没有继承关系
 * */
public class NetworkAcceptorCache<ACCEPTOR> {

    private final Map<BlockPos, Map<Direction, LazyOptional<ACCEPTOR>>> cachedAcceptors = new Object2ObjectOpenHashMap<>();
    private final Map<Transmitter<ACCEPTOR, ?, ?>, Set<Direction>> changedAcceptors = new Object2ObjectOpenHashMap<>();
    //更新cachedAcceptors.
    //如果有对应的transmitter则添加，否则从数据中删除。
    public void updateTransmitterOnSide(Transmitter<ACCEPTOR, ?, ?> transmitter, Direction side) {
        LazyOptional<ACCEPTOR> acceptor = transmitter.canConnectToAcceptor(side) ? transmitter.getAcceptor(side) : LazyOptional.empty();
        BlockPos acceptorPos = transmitter.getTilePos().relative(side);
        if (acceptor.isPresent()) {
            cachedAcceptors.computeIfAbsent(acceptorPos, pos -> new EnumMap<>(Direction.class)).put(side.getOpposite(), acceptor);
        } else if (cachedAcceptors.containsKey(acceptorPos)) {
            Map<Direction, LazyOptional<ACCEPTOR>> cached = cachedAcceptors.get(acceptorPos);
            cached.remove(side.getOpposite());
            if (cached.isEmpty()) {
                cachedAcceptors.remove(acceptorPos);
            }
        } else {
            cachedAcceptors.remove(acceptorPos);
        }
    }
    //与另一个network合并
    public void adoptAcceptors(NetworkAcceptorCache<ACCEPTOR> other) {
        for (Entry<BlockPos, Map<Direction, LazyOptional<ACCEPTOR>>> entry : other.cachedAcceptors.entrySet()) {
            BlockPos pos = entry.getKey();
            if (cachedAcceptors.containsKey(pos)) {
                cachedAcceptors.get(pos).putAll(entry.getValue());
            } else {
                cachedAcceptors.put(pos, entry.getValue());
            }
        }
        for (Entry<Transmitter<ACCEPTOR, ?, ?>, Set<Direction>> entry : other.changedAcceptors.entrySet()) {
            Transmitter<ACCEPTOR, ?, ?> transmitter = entry.getKey();
            if (changedAcceptors.containsKey(transmitter)) {
                changedAcceptors.get(transmitter).addAll(entry.getValue());
            } else {
                changedAcceptors.put(transmitter, entry.getValue());
            }
        }
    }
    //添加出现变化的transmitter
    public void acceptorChanged(Transmitter<ACCEPTOR, ?, ?> transmitter, Direction side) {
        changedAcceptors.computeIfAbsent(transmitter, t -> EnumSet.noneOf(Direction.class)).add(side);
        TransmitterNetworkRegistry.registerChangedNetwork(transmitter.getTransmitterNetwork());
    }
    //每tick调用的更新内容，更新changedAcceptors
    public void commit() {
        if (!changedAcceptors.isEmpty()) {
            for (Entry<Transmitter<ACCEPTOR, ?, ?>, Set<Direction>> entry : changedAcceptors.entrySet()) {
                Transmitter<ACCEPTOR, ?, ?> transmitter = entry.getKey();
                if (transmitter.isValid()) {
                    //Update all the changed directions
                    for (Direction side : entry.getValue()) {
                        updateTransmitterOnSide(transmitter, side);
                    }
                }
            }
            changedAcceptors.clear();
        }
    }

    public void deregister() {
        cachedAcceptors.clear();
        changedAcceptors.clear();
    }

    /**
     * @apiNote Listeners should not be added to these LazyOptionals here as they may not correspond to an actual handler and may not get invalidated.
     */
    public Set<Entry<BlockPos, Map<Direction, LazyOptional<ACCEPTOR>>>> getAcceptorEntrySet() {
        return cachedAcceptors.entrySet();
    }

    /**
     * @apiNote Listeners should not be added to these LazyOptionals here as they may not correspond to an actual handler and may not get invalidated.
     */
    public Collection<Map<Direction, LazyOptional<ACCEPTOR>>> getAcceptorValues() {
        return cachedAcceptors.values();
    }

    public int getAcceptorCount() {
        //Count multiple connections to the same position as multiple acceptors
        return cachedAcceptors.values().stream().mapToInt(Map::size).sum();
    }

    public boolean hasAcceptor(BlockPos acceptorPos) {
        return cachedAcceptors.containsKey(acceptorPos);
    }

    public Set<Direction> getAcceptorDirections(BlockPos pos) {
        //TODO: Do this better?
        return cachedAcceptors.get(pos).keySet();
    }
}