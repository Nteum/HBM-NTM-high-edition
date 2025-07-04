package com.hbm.capabilities.network.cache;


import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.capabilities.network.accepterInfo.AbstractAcceptorInfo;
import com.hbm.blockentity.base.TransmitterBlockEntity;
import com.hbm.capabilities.network.transmitter.Transmitter;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
/**
 * from:mek
 * transmitter对应的所有接收者
 * */
@NothingNullByDefault
public abstract class AbstractAcceptorCache<ACCEPTOR, INFO extends AbstractAcceptorInfo> {
    //所有准备处理的程序
    private final Map<Direction, NonNullConsumer<LazyOptional<ACCEPTOR>>> cachedListeners = new EnumMap<>(Direction.class);
    //
    protected final Map<Direction, INFO> cachedAcceptors = new EnumMap<>(Direction.class);
    protected final Transmitter<ACCEPTOR, ?, ?> transmitter;
    private final TransmitterBlockEntity transmitterTile;
    public byte currentAcceptorConnections = 0x00;

    protected AbstractAcceptorCache(Transmitter<ACCEPTOR, ?, ?> transmitter, TransmitterBlockEntity transmitterTile) {
        this.transmitter = transmitter;
        this.transmitterTile = transmitterTile;
    }

    public void clear() {
        cachedListeners.clear();
        cachedAcceptors.clear();
    }

    public void invalidateCachedAcceptor(Direction side) {
        if (cachedAcceptors.containsKey(side)) {
            //Fully remove the cached acceptor, both the tile, and the value
            cachedAcceptors.remove(side);
            transmitter.markDirtyAcceptor(side);
            //Note: as our listeners don't care about the actual implementation of the tile on a given side
            // we don't bother invalidating and getting rid of the old listener when we remove the cached
            // acceptor as we don't want to accidentally flood the acceptor with listeners if something
            // breaks on our end.
        }
    }

    /**
     * @implNote Grabs the acceptors from cache, ensuring that the connection map contains the side
     */
    public LazyOptional<ACCEPTOR> getCachedAcceptor(Direction side) {
        return Transmitter.connectionMapContainsSide(currentAcceptorConnections, side) ? getConnectedAcceptor(side) : LazyOptional.empty();
    }

    public List<ACCEPTOR> getConnectedAcceptors(Set<Direction> sides) {
        List<ACCEPTOR> acceptors = new ArrayList<>(sides.size());
        for (Direction side : sides) {
            getConnectedAcceptor(side).ifPresent(acceptors::add);
        }
        return acceptors;
    }

    protected abstract LazyOptional<ACCEPTOR> getConnectedAcceptor(Direction side);

    /**
     * Gets the listener that will refresh connections on a given side.
     */
    protected NonNullConsumer<LazyOptional<ACCEPTOR>> getRefreshListener(@NotNull Direction side) {
        return cachedListeners.computeIfAbsent(side, s -> new RefreshListener<>(transmitterTile, s));
    }

    private static class RefreshListener<ACCEPTOR> implements NonNullConsumer<LazyOptional<ACCEPTOR>> {

        //Note: We only keep a weak reference to the tile from inside the listener so that if it gets unloaded it can be released from memory
        // instead of being referenced by the listener still in the tile in a neighboring chunk
        private final WeakReference<TransmitterBlockEntity> tile;
        private final Direction side;

        private RefreshListener(TransmitterBlockEntity tile, Direction side) {
            this.tile = new WeakReference<>(tile);
            this.side = side;
        }

        @Override
        public void accept(@NotNull LazyOptional<ACCEPTOR> ignored) {
            TransmitterBlockEntity transmitterTile = tile.get();
            //Check to make sure the transmitter is still valid and that the position we are going to check is actually still loaded
            if (transmitterTile != null && !transmitterTile.isRemoved() && transmitterTile.hasLevel() && transmitterTile.isLoaded() &&
                WorldUtils.isBlockLoaded(transmitterTile.getLevel(), transmitterTile.getBlockPos().relative(side))) {
                //If it is, then refresh the connection
                transmitterTile.getTransmitter().refreshConnections(side);
            }
        }
    }
}