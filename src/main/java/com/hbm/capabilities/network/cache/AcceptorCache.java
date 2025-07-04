package com.hbm.capabilities.network.cache;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.capabilities.network.accepterInfo.AbstractAcceptorInfo;
import com.hbm.blockentity.base.TransmitterBlockEntity;
import com.hbm.capabilities.network.transmitter.Transmitter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Objects;

@NothingNullByDefault
public class AcceptorCache<ACCEPTOR> extends AbstractAcceptorCache<ACCEPTOR, AcceptorCache.AcceptorInfo<ACCEPTOR>> {

    public AcceptorCache(Transmitter<ACCEPTOR, ?, ?> transmitter, TransmitterBlockEntity transmitterTile) {
        super(transmitter, transmitterTile);
    }

    @Override
    protected LazyOptional<ACCEPTOR> getConnectedAcceptor(Direction side) {
        return null;
    }
    public static class AcceptorInfo<ACCEPTOR> extends AbstractAcceptorInfo {

        private LazyOptional<?> sourceAcceptor;
        private LazyOptional<ACCEPTOR> acceptor;

        private AcceptorInfo(BlockEntity tile, LazyOptional<?> sourceAcceptor, LazyOptional<ACCEPTOR> acceptor) {
            super(tile);
            this.acceptor = acceptor;
            this.sourceAcceptor = sourceAcceptor;
        }

        private void updateAcceptor(LazyOptional<?> sourceAcceptor, LazyOptional<ACCEPTOR> acceptor) {
            this.sourceAcceptor = sourceAcceptor;
            this.acceptor = acceptor;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o instanceof AcceptorInfo<?> other && getTile().equals(other.getTile()) && sourceAcceptor.equals(other.sourceAcceptor) && acceptor.equals(other.acceptor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTile(), sourceAcceptor, acceptor);
        }
    }
}