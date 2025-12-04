package com.hbm.capabilities.network.transmitter;

import com.hbm.HBMKey;
import com.hbm.api.Chunk3D;
import com.hbm.api.Coord4D;
import com.hbm.capabilities.network.network.DynamicNetwork;
import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.capabilities.network.cache.AcceptorCache;
import com.hbm.capabilities.network.TransmissionType;
import com.hbm.blockentity.base.TransmitterBlockEntity;
import com.hbm.capabilities.network.cache.AbstractAcceptorCache;
import com.hbm.api.interferences.ITileWrapper;
import com.hbm.capabilities.network.ConnType;
import com.hbm.capabilities.network.validator.CompatibleTransmitterValidator;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.NBTHelper;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * from: mek
 * 传输线缆，所有电线、管道
 * */
public abstract class Transmitter<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
        TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> implements ITileWrapper {
    public static boolean connectionMapContainsSide(byte connections, Direction side) {
        return connectionMapContainsSide(connections, side.ordinal());
    }
    private static boolean connectionMapContainsSide(byte connections, int sideOrdinal) {
        byte tester = (byte) (1 << sideOrdinal);
        return (connections & tester) > 0;
    }
    private static byte setConnectionBit(byte connections, boolean toSet, Direction side) {
        return (byte) ((connections & ~(byte) (1 << side.ordinal())) | (byte) ((toSet ? 1 : 0) << side.ordinal()));
    }

    private static ConnType getConnType(Direction side, byte allConnections, byte transmitterConnections, ConnType[] types) {
        int sideOrdinal = side.ordinal();
        if (!connectionMapContainsSide(allConnections, sideOrdinal)) {
            return ConnType.FORBID;
        } else if (connectionMapContainsSide(transmitterConnections, sideOrdinal)) {
            return ConnType.NORMAL;
        }
        return types[sideOrdinal];
    }
    
    //所有连接点的连接状态。
    private ConnType[] connTypes = {ConnType.NORMAL,ConnType.NORMAL,ConnType.NORMAL,ConnType.NORMAL,ConnType.NORMAL,ConnType.NORMAL};
    //所有连接的接收者
    private AbstractAcceptorCache<ACCEPTOR, ?> acceptorCache;
    //当前所有方向的链接状态，
    public byte currentTransmitterConnections = 0x00;
    //传输线对应的方块实体
    private TransmitterBlockEntity transmitterTile;
    //支持的传输类型，比如流体、能量等，可以支持多种传输
    private Set<TransmissionType> supportedTransmissionTypes;
    //附属的网络
    private NETWORK network;
    //是否是单独的传输线
    private boolean orphaned = true;

    Transmitter(TransmitterBlockEntity tile, TransmissionType... transmissionTypes){
        this.transmitterTile = tile;
        this.acceptorCache = createAcceptorCache();
    }
    protected TRANSMITTER getTransmitter() {
        return (TRANSMITTER) this;
    }
    protected AbstractAcceptorCache<ACCEPTOR, ?> createAcceptorCache() {
        return new AcceptorCache<>(this, getTransmitterTile());
    }
    public AbstractAcceptorCache<ACCEPTOR, ?> getAcceptorCache() {return acceptorCache;}
    @NotNull
    public LazyOptional<ACCEPTOR> getAcceptor(Direction side) {
        return acceptorCache.getCachedAcceptor(side);
    }
    public TransmitterBlockEntity getTransmitterTile(){return this.transmitterTile;}
    public ConnType[] getConnTypesRaw(){return this.connTypes;}
    public void setConnTypesRaw(@NotNull ConnType[] connTypes) {
        if (this.connTypes.length != connTypes.length) {
            throw new IllegalArgumentException("Mismatched connection types length");
        }
        this.connTypes = connTypes;
    }

    public ConnType getConnTypeRaw(@NotNull Direction side) {
        return connTypes[side.ordinal()];
    }
    public void setConnectionTypeRaw(@NotNull Direction side, @NotNull ConnType type) {
        int index = side.ordinal();
        ConnType old = connTypes[index];
        if (old != type) {
            connTypes[index] = type;
            getTransmitterTile().sideChanged(side, old, type);
        }
    }
    @Override
    public BlockPos getTilePos() {
        return transmitterTile.getTilePos();
    }

    @Override
    public Level getTileWorld() {
        return transmitterTile.getTileWorld();
    }

    @Override
    public Coord4D getTileCoord() {
        return transmitterTile.getTileCoord();
    }

    @Override
    public Chunk3D getTileChunk() {
        return transmitterTile.getTileChunk();
    }
    public boolean isRemote() {
        return transmitterTile.isRemote();
    }
    public boolean isOrphan() {
        return orphaned;
    }
    public void setOrphan(boolean nowOrphaned) {
        orphaned = nowOrphaned;
    }
    public CompatibleTransmitterValidator<ACCEPTOR, NETWORK, TRANSMITTER> getNewOrphanValidator() {
        return new CompatibleTransmitterValidator<>();
    }
    public Set<TransmissionType> getSupportedTransmissionTypes() {
        return supportedTransmissionTypes;
    }

    public boolean supportsTransmissionType(Transmitter<?, ?, ?> transmitter) {
        return transmitter.getSupportedTransmissionTypes().stream().anyMatch(supportedTransmissionTypes::contains);
    }

    public boolean supportsTransmissionType(TransmitterBlockEntity transmitter) {
        return supportsTransmissionType(transmitter.getTransmitter());
    }
    /**
     * Only call on the server
     */
    public void requestsUpdate() {
        getTransmitterTile().sendUpdatePacket();
    }

    /**
     * Gets the network currently in use by this transmitter segment.
     *
     * @return network this transmitter is using
     */
    public NETWORK getTransmitterNetwork() {
        return this.network;
    }
    /**
     * Sets this transmitter segment's network to a new value.
     *
     * @param network - network to set to
     */
    public void setTransmitterNetwork(NETWORK network) {
        setTransmitterNetwork(network, true);
    }

    /**
     * Sets this transmitter segment's network to a new value.
     *
     * @param network    - network to set to
     * @param requestNow - Force a request now if not the return value will be if a request is needed
     */
    public boolean setTransmitterNetwork(NETWORK network, boolean requestNow) {
        if (this.network == network) {
            return false;
        }
        if (isRemote() && this.network != null) {
            this.network.removeTransmitter(getTransmitter());
        }
        this.network = network;
        orphaned = this.network == null;
        if (isRemote()) {
            if (this.network != null) {
                this.network.addTransmitter(getTransmitter());
            }
        } else if (requestNow) {
            //If we are requesting now request the update
            requestsUpdate();
        } else {
            //Otherwise, return that we need to update it
            return true;
        }
        return false;
    }
    public boolean hasTransmitterNetwork() {
        return !isOrphan() && getTransmitterNetwork() != null;
    }

    public abstract NETWORK createEmptyNetworkWithID(UUID networkID);

    public abstract NETWORK createNetworkByMerging(Collection<NETWORK> toMerge);
    //判断transmitter是否有效，只要对应的方块实体有效即可
    public boolean isValid() {
        return !getTransmitterTile().isRemoved() && getTransmitterTile().isLoaded();
    }
    public boolean isValidTransmitter(TransmitterBlockEntity transmitter, Direction side) {
        return isValidTransmitterBasic(transmitter, side);
    }

    public boolean isValidTransmitterBasic(TransmitterBlockEntity transmitter, Direction side) {
        return supportsTransmissionType(transmitter) && canConnectMutual(side, transmitter);
    }

    public void markDirtyAcceptor(Direction side) {
    }
    public abstract void takeShare();
    /**
     * @apiNote Only call this from the server side
     */
    public boolean isValidAcceptor(BlockEntity tile, Direction side) {
        //TODO: Rename this method better to make it more apparent that it caches and also listens to the acceptor
        //If it isn't a transmitter or the transmission type is different than the one the transmitter has
        return !(tile instanceof TransmitterBlockEntity transmitter) || !supportsTransmissionType(transmitter);
    }
    //判断是否可以和某个方向相互连接
    public boolean canConnectMutual(Direction side, @Nullable BlockEntity cachedTile) {
        if (!canConnect(side)) {
            return false;
        }
        if (cachedTile == null) {
            //If we don't already have the tile that is on the side calculated, do so
            cachedTile = WorldUtils.getTileEntity(getTileWorld(), getTilePos().relative(side));
        }
        return !(cachedTile instanceof TransmitterBlockEntity transmitter) || transmitter.getTransmitter().canConnect(side.getOpposite());
    }

    public boolean canConnectMutual(Direction side, @Nullable TRANSMITTER cachedTransmitter) {
        if (!canConnect(side)) {
            return false;
        }
        //Return true if the other transmitter is null (some other tile is there) or the transmitter can connect both directions
        return cachedTransmitter == null || cachedTransmitter.canConnect(side.getOpposite());
    }

    public boolean canConnect(Direction side) {
        if (getConnTypeRaw(side) == ConnType.FORBID) {
            return false;
        }
        //原本这里有判断红石信号的部分，但目前似乎没必要
        return true;
    }
    /**
     * @apiNote Only call this from the server side
     */
    public byte getPossibleTransmitterConnections() {
        byte connections = 0x00;
        for (Direction side : EnumUtils.DIRECTIONS) {
            TransmitterBlockEntity tile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, getTileWorld(), getTilePos().relative(side));
            if (tile != null && isValidTransmitter(tile, side)) {
                connections |= (byte) (1 << side.ordinal());
            }
        }
        return connections;
    }

    /**
     * @apiNote Only call this from the server side
     */
    private boolean getPossibleAcceptorConnection(Direction side) {
        BlockEntity tile = WorldUtils.getTileEntity(getTileWorld(), getTilePos().relative(side));
        if (canConnectMutual(side, tile) && isValidAcceptor(tile, side)) {
            return true;
        }
        acceptorCache.invalidateCachedAcceptor(side);
        return false;
    }

    //某个面和transmitter的连接
    private boolean getPossibleTransmitterConnection(Direction side) {
        TransmitterBlockEntity tile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, getTileWorld(), getTilePos().relative(side));
        return tile != null && isValidTransmitter(tile, side);
    }

    //所有面acceptor的连接
    public byte getPossibleAcceptorConnections() {
        byte connections = 0x00;
        for (Direction side : EnumUtils.DIRECTIONS) {
            BlockPos offset = getTilePos().relative(side);
            BlockEntity tile = WorldUtils.getTileEntity(getTileWorld(), offset);
            if (canConnectMutual(side, tile)) {
                if (!isRemote() && !WorldUtils.isBlockLoaded(getTileWorld(), offset)) {
                    getTransmitterTile().setForceUpdate();
                    continue;
                }
                if (isValidAcceptor(tile, side)) {
                    connections |= 1 << side.ordinal();
                    continue;
                }
            }
            acceptorCache.invalidateCachedAcceptor(side);
        }
        return connections;
    }

    //连接由transmitter的连接和acceptor的连接构成
    public byte getAllCurrentConnections() {
        return (byte) (currentTransmitterConnections | acceptorCache.currentAcceptorConnections);
    }
    public boolean canConnectToAcceptor(Direction side) {
        ConnType type = getConnTypeRaw(side);
        return type == ConnType.NORMAL || type == ConnType.OUT;
    }
    @NotNull
    public CompoundTag getReducedUpdateTag(CompoundTag updateTag) {
        updateTag.putByte(HBMKey.CURRENT_CONNECTIONS, currentTransmitterConnections);
        updateTag.putByte(HBMKey.CURRENT_ACCEPTORS, acceptorCache.currentAcceptorConnections);
        for (Direction direction : EnumUtils.DIRECTIONS) {
            NBTHelper.writeEnum(updateTag, HBMKey.SIDE + direction.ordinal(), getConnTypeRaw(direction));
        }
        //Transmitter
        if (hasTransmitterNetwork()) {
            updateTag.putUUID(HBMKey.NETWORK, getTransmitterNetwork().getUUID());
        }
        return updateTag;
    }

    public void handleUpdateTag(@NotNull CompoundTag tag) {
//        NBTHelper.setByteIfPresent(tag, HBMKey.CURRENT_CONNECTIONS, connections -> currentTransmitterConnections = connections);
//        NBTHelper.setByteIfPresent(tag, HBMKey.CURRENT_ACCEPTORS, acceptors -> acceptorCache.currentAcceptorConnections = acceptors);
//        for (Direction direction : EnumUtils.DIRECTIONS) {
//            NBTHelper.setEnumIfPresent(tag, HBMKey.SIDE + direction.ordinal(), ConnType::byIndexStatic, type -> setConnectionTypeRaw(direction, type));
//        }
//        //Transmitter
//        NBTHelper.setUUIDIfPresentElse(tag, HBMKey.NETWORK, networkID -> {
//            if (hasTransmitterNetwork() && getTransmitterNetwork().getUUID().equals(networkID)) {
//                //Nothing needs to be done
//                return;
//            }
//            DynamicNetwork<?, ?, ?> clientNetwork = TransmitterNetworkRegistry.getInstance().getClientNetwork(networkID);
//            if (clientNetwork == null) {
//                NETWORK network = createEmptyNetworkWithID(networkID);
//                network.register();
//                setTransmitterNetwork(network);
//                handleContentsUpdateTag(network, tag);
//            } else {
//                //TODO: Validate network type?
//                updateClientNetwork((NETWORK) clientNetwork);
//            }
//        }, () -> setTransmitterNetwork(null));
    }

    protected void updateClientNetwork(@NotNull NETWORK network) {
        network.register();
        setTransmitterNetwork(network);
    }

    protected void handleContentsUpdateTag(@NotNull NETWORK network, @NotNull CompoundTag tag) {
    }

    public void read(@NotNull CompoundTag nbtTags) {
        for (Direction direction : EnumUtils.DIRECTIONS) {
            NBTHelper.setEnumIfPresent(nbtTags, HBMKey.CONNECTION + direction.ordinal(), ConnType::byIndexStatic, type -> setConnectionTypeRaw(direction, type));
        }
    }

    @NotNull
    public CompoundTag write(@NotNull CompoundTag nbtTags) {
        for (Direction direction : EnumUtils.DIRECTIONS) {
            NBTHelper.writeEnum(nbtTags, HBMKey.CONNECTION + direction.ordinal(), getConnTypeRaw(direction));
        }
        return nbtTags;
    }
    //重检查连接
    public void refreshConnections() {
        if (!isRemote()) {
            byte possibleTransmitters = getPossibleTransmitterConnections();
            byte possibleAcceptors = getPossibleAcceptorConnections();
            byte newlyEnabledTransmitters = 0;
            boolean sendDesc = false;
            if ((possibleTransmitters | possibleAcceptors) != getAllCurrentConnections()) {
                sendDesc = true;
                if (possibleTransmitters != currentTransmitterConnections) {
                    //If they don't match get the difference
                    newlyEnabledTransmitters = (byte) (possibleTransmitters ^ currentTransmitterConnections);
                    //Now remove all bits that already where enabled, so we only have the
                    // ones that are newly enabled. There is no need to recheck for a
                    // network merge on two transmitters if one is no longer accessible
                    newlyEnabledTransmitters &= ~currentTransmitterConnections;
                }
            }

            currentTransmitterConnections = possibleTransmitters;
            acceptorCache.currentAcceptorConnections = possibleAcceptors;
            if (newlyEnabledTransmitters != 0) {
                //If any sides are now valid transmitters that were not before recheck the connection
                recheckConnections(newlyEnabledTransmitters);
            }
            if (sendDesc) {
                getTransmitterTile().sendUpdatePacket();
            }
        }
    }

    public void refreshConnections(Direction side) {
        if (!isRemote()) {
            boolean possibleTransmitter = getPossibleTransmitterConnection(side);
            boolean possibleAcceptor = getPossibleAcceptorConnection(side);
            boolean transmitterChanged = false;
            boolean sendDesc = false;
            if ((possibleTransmitter || possibleAcceptor) != connectionMapContainsSide(getAllCurrentConnections(), side)) {
                sendDesc = true;
                if (possibleTransmitter != connectionMapContainsSide(currentTransmitterConnections, side)) {
                    //If it doesn't match check if it is now enabled, as we don't care about it changing to disabled
                    transmitterChanged = possibleTransmitter;
                }
            }

            currentTransmitterConnections = setConnectionBit(currentTransmitterConnections, possibleTransmitter, side);
            acceptorCache.currentAcceptorConnections = setConnectionBit(acceptorCache.currentAcceptorConnections, possibleAcceptor, side);
            if (transmitterChanged) {
                //If this side is now a valid transmitter, and it wasn't before recheck the connection
                recheckConnection(side);
            }
            if (sendDesc) {
                getTransmitterTile().sendUpdatePacket();
            }
        }
    }

    /**
     * @param newlyEnabledTransmitters The transmitters that are now enabled and were not before.
     *
     * @apiNote Only call this from the server side
     */
    protected void recheckConnections(byte newlyEnabledTransmitters) {
        if (!hasTransmitterNetwork()) {
            //If we don't have a transmitter network then recheck connection status both ways if the other tile is also a transmitter
            //This fixes pipes not reconnecting cross chunk
            for (Direction side : EnumUtils.DIRECTIONS) {
                if (connectionMapContainsSide(newlyEnabledTransmitters, side)) {
                    TransmitterBlockEntity tile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, getTileWorld(), getTilePos().relative(side));
                    if (tile != null) {
                        tile.getTransmitter().refreshConnections(side.getOpposite());
                    }
                }
            }
        }
    }

    /**
     * @param side The side that a transmitter is now enabled on after having been disabled.
     *
     * @apiNote Only call this from the server side
     */
    protected void recheckConnection(Direction side) {
    }
    public void onModeChange(Direction side) {
        markDirtyAcceptor(side);
        if (getPossibleTransmitterConnections() != currentTransmitterConnections) {
            markDirtyTransmitters();
        }
        getTransmitterTile().setChanged();
    }

    public void onNeighborTileChange(Direction side) {
        refreshConnections(side);
    }

    public void onNeighborBlockChange(Direction side) {
        refreshConnections(side);
    }

    protected void markDirtyTransmitters() {
        notifyTileChange();
        if (hasTransmitterNetwork()) {
            //TODO - 1.18: Can this be done in a way that doesn't require reforming the network if it is still valid and the same
            TransmitterNetworkRegistry.invalidateTransmitter(getTransmitter());
        }
    }

    public void remove() {
        //Clear our cached listeners
        acceptorCache.clear();
    }

    public ConnType getConnType(Direction side) {
        return getConnType(side, getAllCurrentConnections(), currentTransmitterConnections, connTypes);
    }

    public Set<Direction> getConnections(ConnType type) {
        Set<Direction> sides = null;
        for (Direction side : EnumUtils.DIRECTIONS) {
            if (getConnType(side) == type) {
                if (sides == null) {
                    //Lazy init the set so that if there are none we can just use an empty set
                    // instead of having to initialize an enum set
                    sides = EnumSet.noneOf(Direction.class);
                }
                sides.add(side);
            }
        }
        return sides == null ? Collections.emptySet() : sides;
    }

    public InteractionResult onConfigure(Player player, Direction side) {
        return InteractionResult.PASS;
    }

//    public InteractionResult onRightClick(Player player, Direction side) {
//        if (handlesRedstone()) {
//            redstoneReactive = !redstoneReactive;
//            refreshConnections();
//            notifyTileChange();
//            player.displayClientMessage(MekanismLang.REDSTONE_SENSITIVITY.translateColored(EnumColor.GRAY, EnumColor.INDIGO, OnOff.of(redstoneReactive)), true);
//        }
//        return InteractionResult.SUCCESS;
//    }

    public void notifyTileChange() {
        WorldUtils.notifyLoadedNeighborsOfTileChange(getTileWorld(), getTilePos());
    }

    public void validateAndTakeShare() {
        takeShare();
    }
}
