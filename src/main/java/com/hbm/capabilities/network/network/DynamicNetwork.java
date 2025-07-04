package com.hbm.capabilities.network.network;

import com.hbm.capabilities.network.INetworkDataHandler;
import com.hbm.capabilities.network.TransmitterNetworkRegistry;
import com.hbm.capabilities.network.validator.CompatibleTransmitterValidator;
import com.hbm.api.text.IHasTextComponent;
import com.hbm.capabilities.network.cache.NetworkAcceptorCache;
import com.hbm.capabilities.network.transmitter.Transmitter;
import com.hbm.utils.EnumUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * from: mek
 * @apiNote 维护一个transmitter组成的网络结构，具体传输在子类中定义
 * */
public abstract class DynamicNetwork <ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
        TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> implements INetworkDataHandler, IHasTextComponent {
    protected final Set<TRANSMITTER> transmitters = new ObjectOpenHashSet<>();
    protected final Set<TRANSMITTER> transmittersToAdd = new ObjectOpenHashSet<>();
    protected final NetworkAcceptorCache<ACCEPTOR> acceptorCache = new NetworkAcceptorCache<>();
    @Nullable
    protected Level world;
    private UUID uuid;
    @Nullable
    private CompatibleTransmitterValidator<ACCEPTOR, NETWORK, TRANSMITTER> transmitterValidator;
    protected DynamicNetwork(UUID networkID) {
        this.uuid = networkID;
    }

    public UUID getUUID() {
        return uuid;
    }

    @SuppressWarnings("unchecked")
    protected NETWORK getNetwork() {
        return (NETWORK) this;
    }
    //网络实际运行的主函数，会被register调用
    public void commit(){
        //1. 更新网络结构
        if (!transmittersToAdd.isEmpty()) {
            boolean addedValidTransmitters = false;
            List<TRANSMITTER> transmittersToUpdate = new ArrayList<>();
            //1.1. 收集所有待更新的transmitter
            for (TRANSMITTER transmitter : transmittersToAdd) {
                //Note: Transmitter should not be able to be null here, but I ran into a null pointer
                // pointing to it being null that I could not reproduce, so just added this as a safety check
                if (transmitter != null && transmitter.isValid()) {
                    addedValidTransmitters = true;
                    if (world == null) {
                        world = transmitter.getTileWorld();
                    }
                    for (Direction side : EnumUtils.DIRECTIONS) {
                        acceptorCache.updateTransmitterOnSide(transmitter, side);
                    }
                    if (transmitter.setTransmitterNetwork(getNetwork(), false)) {
                        transmittersToUpdate.add(transmitter);
                    }
                    addTransmitterFromCommit(transmitter);
                }
            }
            transmittersToAdd.clear();
            //1.2. 更新所有需要变的transmitter
            if (addedValidTransmitters) {
                validTransmittersAdded();
                transmittersToUpdate.forEach(Transmitter::requestsUpdate);
            }
        }
        //2. 更新所有transmitter的方向
        acceptorCache.commit();
        transmitterValidator = null;
    }
    /**
     * @apiNote Only called on the server
     */
    public void onUpdate() {
    }
    //====transmitter relate
    public Set<TRANSMITTER> getTransmitters() {
        return transmitters;
    }
    public void addNewTransmitters(Collection<TRANSMITTER> newTransmitters, CompatibleTransmitterValidator<ACCEPTOR, NETWORK, TRANSMITTER> transmitterValidator) {
        transmittersToAdd.addAll(newTransmitters);
        //Cache the transmitter validator in the network, so that if we have a case of orphans being on either side of
        // an existing network, and the orphans are what have contents stored, that then we don't try merging them all
        // together when they may not actually be able to have both sets of orphans connect. After the network is
        // updated (committed), this cached validator will be unset
        this.transmitterValidator = transmitterValidator;
    }
    protected void addTransmitterFromCommit(TRANSMITTER transmitter) {
        transmitters.add(transmitter);
    }
    public void addTransmitter(TRANSMITTER transmitter) {
        transmitters.add(transmitter);
    }

    public void removeTransmitter(TRANSMITTER transmitter) {
        transmitters.remove(transmitter);
        if (transmitters.isEmpty()) {
            deregister();
        }
    }

    protected void validTransmittersAdded() {
    }

    public int transmittersSize() {
        return transmitters.size();
    }
    //====acceptor relate
    public boolean hasAcceptor(BlockPos acceptorPos) {
        return acceptorCache.hasAcceptor(acceptorPos);
    }

    public Set<Direction> getAcceptorDirections(BlockPos pos) {
        return acceptorCache.getAcceptorDirections(pos);
    }
    public void acceptorChanged(TRANSMITTER transmitter, Direction side) {
        acceptorCache.acceptorChanged(transmitter, side);
    }
    //====
    public boolean isRemote() {
        return world == null ? EffectiveSide.get().isClient() : world.isClientSide;
    }
    //
    public void invalidate(@Nullable TRANSMITTER triggerTransmitter) {
        //如果是最后一个transmitter，使网络消失
        if (transmitters.size() == 1 && triggerTransmitter != null && !triggerTransmitter.isValid()) {
            //We're destroying the last transmitter in the network
            //Note: We check it isn't valid to make sure we are destroying it and not just changing redstone sensitivity
            onLastTransmitterRemoved(triggerTransmitter);
        }
        removeInvalid(triggerTransmitter);
        //使其他的transmitter变成无网络单个的transmitter
        if (!isRemote()) {
            for (TRANSMITTER transmitter : transmitters) {
                if (transmitter.isValid()) {
                    transmitter.takeShare();
                    transmitter.setTransmitterNetwork(null);
                    TransmitterNetworkRegistry.registerOrphanTransmitter(transmitter);
                }
            }
        }
        deregister();
    }
    protected void onLastTransmitterRemoved(@NotNull TRANSMITTER triggerTransmitter) {
    }

    protected void removeInvalid(@Nullable TRANSMITTER triggerTransmitter) {
        //Remove invalid transmitters first for share calculations
        transmitters.removeIf(transmitter -> !transmitter.isValid());
    }
    public List<TRANSMITTER> adoptTransmittersAndAcceptorsFrom(NETWORK net) {
        List<TRANSMITTER> transmittersToUpdate = new ArrayList<>();
        for (TRANSMITTER transmitter : net.transmitters) {
            transmitters.add(transmitter);
            if (transmitter.setTransmitterNetwork(getNetwork(), false)) {
                transmittersToUpdate.add(transmitter);
            }
        }
        transmittersToAdd.addAll(net.transmittersToAdd);
        acceptorCache.adoptAcceptors(net.acceptorCache);
        return transmittersToUpdate;
    }

    protected void adoptAllAndRegister(Collection<NETWORK> networks) {
        List<TRANSMITTER> transmittersToUpdate = new ArrayList<>();
        for (NETWORK net : networks) {
            if (net != null) {
                transmittersToUpdate.addAll(adoptTransmittersAndAcceptorsFrom(net));
                net.deregister();
            }
        }
        register();
        transmittersToUpdate.forEach(Transmitter::requestsUpdate);
    }
    //注册到注册器中
    public void register() {
        if (isRemote()) {
            TransmitterNetworkRegistry.getInstance().addClientNetwork(getUUID(), this);
        } else {
            TransmitterNetworkRegistry.getInstance().registerNetwork(this);
        }
    }
    //从注册器中移除
    public void deregister() {
        transmitters.clear();
        transmittersToAdd.clear();
        acceptorCache.deregister();
        transmitterValidator = null;
        if (isRemote()) {
            TransmitterNetworkRegistry.getInstance().removeClientNetwork(this);
        } else {
            TransmitterNetworkRegistry.getInstance().removeNetwork(this);
        }
    }
    public boolean isEmpty() {
        return transmitters.isEmpty();
    }

    public int getAcceptorCount() {
        return acceptorCache.getAcceptorCount();
    }

    @Nullable
    public Level getWorld() {
        return world;
    }



    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DynamicNetwork<?, ?, ?> other) {
            return uuid.equals(other.uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
