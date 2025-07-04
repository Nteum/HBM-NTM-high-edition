package com.hbm.capabilities.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.HBM;
import com.hbm.api.Chunk3D;
import com.hbm.api.Coord4D;
import com.hbm.capabilities.network.network.DynamicNetwork;
import com.hbm.capabilities.network.transmitter.Transmitter;
import com.hbm.capabilities.network.validator.CompatibleTransmitterValidator;
import com.hbm.blockentity.base.TransmitterBlockEntity;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.WorldUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkTicketLevelUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
/**
 * from: mek
 * transmitter网络的注册器，每个tick自动调用运行每个network的逻辑
 * */
public class TransmitterNetworkRegistry {
    //单例模式
    private static final TransmitterNetworkRegistry INSTANCE = new TransmitterNetworkRegistry();
    private static boolean loaderRegistered = false;
    //所有transmitter
    private final Multimap<Chunk3D, Transmitter<?, ?, ?>> transmitters = HashMultimap.create();
    //新加入的单独不成网络的传输线，往往很快会加入transmitters
    private Map<Coord4D, Transmitter<?, ?, ?>> newOrphanTransmitters = new Object2ObjectOpenHashMap<>();
    //无效化的传输线，如果要移除一个transmitter，会将它加入这个变量中
    private Set<Transmitter<?, ?, ?>> invalidTransmitters = new ObjectOpenHashSet<>();
    //存在网络的区块加载和被加载的状态
    private Object2BooleanMap<Chunk3D> changedTicketChunks = new Object2BooleanOpenHashMap<>();
    //所有网络
    private final Set<DynamicNetwork<?, ?, ?>> networks = new ObjectOpenHashSet<>();
    private final Map<UUID, DynamicNetwork<?, ?, ?>> clientNetworks = new Object2ObjectOpenHashMap<>();
    //需要变更的网络
    private Set<DynamicNetwork<?, ?, ?>> networksToChange = new ObjectOpenHashSet<>();

    public void addClientNetwork(UUID networkID, DynamicNetwork<?, ?, ?> network) {
        if (!clientNetworks.containsKey(networkID)) {
            clientNetworks.put(networkID, network);
        }
    }

    @Nullable
    public DynamicNetwork<?, ?, ?> getClientNetwork(UUID networkID) {
        return clientNetworks.get(networkID);
    }

    public void removeClientNetwork(DynamicNetwork<?, ?, ?> network) {
        clientNetworks.remove(network.getUUID());
    }

    public void clearClientNetworks() {
        clientNetworks.clear();
    }
    public static TransmitterNetworkRegistry getInstance() {
        return INSTANCE;
    }
    //初始化
    public static void initiate() {
        if (!loaderRegistered) {
            loaderRegistered = true;
            MinecraftForge.EVENT_BUS.register(INSTANCE);
        }
    }

    public static void reset() {
        getInstance().networks.clear();
        getInstance().networksToChange.clear();
        getInstance().invalidTransmitters.clear();
        getInstance().newOrphanTransmitters.clear();
        getInstance().transmitters.clear();
        getInstance().changedTicketChunks.clear();
    }
    //添加一个transmitter，也相当于开始对它记录
    public static void trackTransmitter(Transmitter<?, ?, ?> transmitter) {
        getInstance().transmitters.put(transmitter.getTileChunk(), transmitter);
    }

    public static void untrackTransmitter(Transmitter<?, ?, ?> transmitter) {
        getInstance().transmitters.remove(transmitter.getTileChunk(), transmitter);
    }
    //无效化一个传输线（+invalidTransmitters）
    public static void invalidateTransmitter(Transmitter<?, ?, ?> transmitter) {
        TransmitterNetworkRegistry registry = getInstance();
        registry.invalidTransmitters.add(transmitter);
        Coord4D coord = transmitter.getTileCoord();
        Transmitter<?, ?, ?> removed = registry.newOrphanTransmitters.remove(coord);
        if (removed != null && removed != transmitter) {
            HBM.LOGGER.error("Different orphan transmitter was registered at location during removal! {}", coord);
            registry.newOrphanTransmitters.put(coord, transmitter);//put it back?
        }
    }
    //加入一个孤立传输线缆（+newOrphanTransmitters）
    public static void registerOrphanTransmitter(Transmitter<?, ?, ?> transmitter) {
        if (!getInstance().invalidTransmitters.remove(transmitter)) {
            //If we weren't an invalid transmitter, then we need to add it as a new orphan, otherwise removing it is good enough
            // as if it was an orphan before it still will be one, and if it wasn't then it still will be part of the network it
            // was in.
            Coord4D coord = transmitter.getTileCoord();
            Transmitter<?, ?, ?> previous = getInstance().newOrphanTransmitters.put(coord, transmitter);
            if (previous != null && previous != transmitter && previous.isValid()) {
                HBM.LOGGER.error("Different orphan transmitter was already registered at location! {}", coord);
            }
        }
    }
    public static void registerChangedNetwork(DynamicNetwork<?, ?, ?> network) {
        getInstance().networksToChange.add(network);
    }

    public void registerNetwork(DynamicNetwork<?, ?, ?> network) {
        networks.add(network);
    }

    public void removeNetwork(DynamicNetwork<?, ?, ?> network) {
        networks.remove(network);
        networksToChange.remove(network);
    }
    //每一刻执行的内容
    //内容：变化的区块；移除无效的传输线；假如孤立的传输线；更新变化；每个网络的更新
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            handleChangedChunks();
            //移除无效的transmitter
            removeInvalidTransmitters();
            //加入孤立的transmitter
            assignOrphans();
            commitChanges();
            //所有网络内容的更新
            for (DynamicNetwork<?, ?, ?> net : networks) {
                net.onUpdate();
            }
        }
    }
    //区块归属变化时需要执行的内容
    @SubscribeEvent
    public void onTicketLevelChange(ChunkTicketLevelUpdatedEvent event) {
        int newTicketLevel = event.getNewTicketLevel();
        int oldTicketLevel = event.getOldTicketLevel();
        boolean loaded;
        if (oldTicketLevel > ChunkMap.MAX_VIEW_DISTANCE && newTicketLevel <= ChunkMap.MAX_VIEW_DISTANCE) {
            //Went from "unloaded" to loaded
            loaded = true;
        } else if (newTicketLevel > ChunkMap.MAX_VIEW_DISTANCE && oldTicketLevel <= ChunkMap.MAX_VIEW_DISTANCE) {
            //Went from loaded to "unloaded"
            loaded = false;
        } else {
            //Load type stayed the same, just exit
            return;
        }
        Chunk3D chunk = new Chunk3D(event.getLevel().dimension(), event.getChunkPos());
        if (transmitters.containsKey(chunk)) {
            //Only track it if we have any transmitters in that chunk
            if (changedTicketChunks.getOrDefault(chunk, loaded) != loaded) {
                //If we are watching the chunk and the loaded state isn't what we already had it as,
                // then remove it as it didn't actually change. In theory in all cases this is equivalent
                // to just checking if changeTicketChunks contains chunk, but is slightly more accurate
                // in case for some reason we get two load or unload notifications in a row
                changedTicketChunks.removeBoolean(chunk);
            } else {
                // Otherwise, make sure the map is aware of the change
                changedTicketChunks.put(chunk, loaded);
            }
        }
    }
    //区块的加载状态改变，联动改变相应区块上传输线的状态
    private void handleChangedChunks() {
        if (!changedTicketChunks.isEmpty()) {
            Object2BooleanMap<Chunk3D> changed = changedTicketChunks;
            changedTicketChunks = new Object2BooleanOpenHashMap<>();
            if (HBM.debug) {
                HBM.LOGGER.info("Dealing with {} changed chunks", changed.size());
            }
            for (Object2BooleanMap.Entry<Chunk3D> entry : changed.object2BooleanEntrySet()) {
                Chunk3D chunk = entry.getKey();
                boolean loaded = entry.getBooleanValue();
                Collection<Transmitter<?, ?, ?>> chunkTransmitters = transmitters.get(chunk);
                for (Transmitter<?, ?, ?> transmitter : chunkTransmitters) {
                    transmitter.getTransmitterTile().chunkAccessibilityChange(loaded);
                }
                if (HBM.debug) {
                    HBM.LOGGER.info("{} {} transmitters in chunk: {}, {}", loaded ? "Loaded" : "Unloaded", chunkTransmitters.size(), chunk.x, chunk.z);
                }
            }
        }
    }
    private void removeInvalidTransmitters() {
        if (!invalidTransmitters.isEmpty()) {
            //Ensure we copy the invalid transmitters, so that when we iterate and remove invalid ones
            // and add still valid ones as orphans, we actually add them as orphans rather than try
            // removing them as invalid and find out they are invalid
            Set<Transmitter<?, ?, ?>> toInvalidate = invalidTransmitters;
            invalidTransmitters = new ObjectOpenHashSet<>();
            if (HBM.debug) {
                HBM.LOGGER.info("Dealing with {} invalid Transmitters", toInvalidate.size());
            }
            for (Transmitter<?, ?, ?> invalid : toInvalidate) {
                removeInvalidTransmitter(invalid);
            }
        }
    }
    //移除无效的传输线，同时更新网络状态
    private <NETWORK extends DynamicNetwork<?, NETWORK, TRANSMITTER>, TRANSMITTER extends Transmitter<?, NETWORK, TRANSMITTER>>
    void removeInvalidTransmitter(Transmitter<?, NETWORK, TRANSMITTER> invalid) {
        if (!invalid.isOrphan() || !invalid.isValid()) {
            NETWORK n = invalid.getTransmitterNetwork();
            if (n != null) {
                n.invalidate((TRANSMITTER) invalid);
                if (!invalid.isValid()) {
                    //If the transmitter isn't valid, then we need to make sure we clear the network from it
                    // after invalidating the network, so that we can make sure that if this transmitter somehow
                    // gets revived, then it will be able to be properly handled as an orphan.
                    invalid.setTransmitterNetwork(null, false);
                }
            }
        }
    }
    //将孤立的传输线加入到网络中
    private void assignOrphans() {
        if (!newOrphanTransmitters.isEmpty()) {
            Map<Coord4D, Transmitter<?, ?, ?>> orphanTransmitters = newOrphanTransmitters;
            newOrphanTransmitters = new Object2ObjectOpenHashMap<>();
            if (HBM.debug) {
                HBM.LOGGER.info("Dealing with {} orphan Transmitters", orphanTransmitters.size());
            }

            for (Transmitter<?, ?, ?> orphanTransmitter : orphanTransmitters.values()) {
                if (orphanTransmitter.isValid() && orphanTransmitter.isOrphan()) {
                    OrphanPathFinder<?, ?, ?> finder = new OrphanPathFinder<>(orphanTransmitter);
                    networksToChange.add(finder.getNetworkFromOrphan(orphanTransmitters));
                }
            }
        }
    }
    //所有已经变化的网络都提交一次变化
    private void commitChanges() {
        if (!networksToChange.isEmpty()) {
            Set<DynamicNetwork<?, ?, ?>> networks = networksToChange;
            networksToChange = new ObjectOpenHashSet<>();
            for (DynamicNetwork<?, ?, ?> network : networks) {
                network.commit();
            }
        }
    }

    @Override
    public String toString() {
        return "Network Registry:\n" + networks;
    }

    public Component[] toComponents() {
        Component[] components = new Component[networks.size()];
        int i = 0;
        for (DynamicNetwork<?, ?, ?> network : networks) {
            components[i++] = network.getTextComponent();
        }
        return components;
    }
    //判断是否传输线是否是孤立的
    public static class OrphanPathFinder<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
            TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> {
        //验证器
        private final CompatibleTransmitterValidator<ACCEPTOR, NETWORK, TRANSMITTER> transmitterValidator;
        //从起始点开始所有相连的transmitter
        private final Set<TRANSMITTER> connectedTransmitters = new ObjectOpenHashSet<>();
        private final Long2ObjectMap<ChunkAccess> chunkMap = new Long2ObjectOpenHashMap<>();
        //从输入的transmitters中搜索到的网络，可以不只一个
        private final Set<NETWORK> networksFound = new ObjectOpenHashSet<>();
        //记录已经遍历的transmitter，避免重复，无其他作用
        private final Set<BlockPos> iterated = new ObjectOpenHashSet<>();
        //队列，用于暂存网络中的transmitter
        private final Deque<BlockPos> queue = new LinkedList<>();
        //验证时起始的transmitter
        private final TRANSMITTER startPoint;
        private final Level world;

        OrphanPathFinder(Transmitter<ACCEPTOR, NETWORK, TRANSMITTER> start) {
            startPoint = (TRANSMITTER) start;
            world = startPoint.getTileWorld();
            transmitterValidator = startPoint.getNewOrphanValidator();
        }

        NETWORK getNetworkFromOrphan(Map<Coord4D, Transmitter<?, ?, ?>> orphanTransmitters) {
            //Calculate the network
            if (queue.peek() != null) {
                HBM.LOGGER.error("OrphanPathFinder queue was not empty?!");
                queue.clear();
            }
            queue.push(startPoint.getTilePos());
            while (queue.peek() != null) {
                iterate(orphanTransmitters, queue.removeFirst());
            }
            //Create the network or grab the found ones
            NETWORK network;
            if (networksFound.size() == 1) {
                if (HBM.debug) {
                    HBM.LOGGER.info("Adding {} transmitters to single found network", connectedTransmitters.size());
                }
                network = networksFound.iterator().next();
            } else {
                if (HBM.debug) {
                    if (networksFound.isEmpty()) {
                        HBM.LOGGER.info("No networks found. Creating new network for {} transmitters", connectedTransmitters.size());
                    } else {
                        HBM.LOGGER.info("Merging {} networks with {} new transmitters", networksFound.size(), connectedTransmitters.size());
                    }
                }
                //TODO: Should we take one of the existing network's uuids if there is one?
                network = startPoint.createNetworkByMerging(networksFound);
            }
            network.addNewTransmitters(connectedTransmitters, transmitterValidator);
            return network;
        }
        //循环内容，循环查找所有transmitter
        private void iterate(Map<Coord4D, Transmitter<?, ?, ?>> orphanTransmitters, BlockPos from) {
            if (iterated.add(from)) {
                Coord4D fromCoord = new Coord4D(from, world);
                Transmitter<?, ?, ?> transmitter = orphanTransmitters.get(fromCoord);
                if (transmitter != null) {
                    //如果还有transmitter没有搜索到，则记录它，并搜索和它相连的transmitter
                    if (transmitter.isValid() && transmitter.isOrphan() && startPoint.supportsTransmissionType(transmitter) &&
                            transmitterValidator.isTransmitterCompatible(transmitter)) {
                        connectedTransmitters.add((TRANSMITTER) transmitter);
                        transmitter.setOrphan(false);
                        for (Direction direction : EnumUtils.DIRECTIONS) {
                            BlockPos directionPos = from.relative(direction);
                            if (!iterated.contains(directionPos)) {
                                TransmitterBlockEntity tile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, world, chunkMap, directionPos);
                                if (tile != null && transmitter.isValidTransmitterBasic(tile, direction)) {
                                    queue.addLast(directionPos);
                                }
                            }
                        }
                    }
                } else {
                    //如果已经没有了，说明一个网络已经被探索尽了，此时记录网络，或创建新网络。
                    TransmitterBlockEntity tile = WorldUtils.getTileEntity(TransmitterBlockEntity.class, world, chunkMap, from);
                    if (tile != null && startPoint.supportsTransmissionType(tile)) {
                        NETWORK net = (NETWORK) tile.getTransmitter().getTransmitterNetwork();
                        //Make sure that there is an external network
                        if (net != null && transmitterValidator.isNetworkCompatible(net)) {
                            networksFound.add(net);
                        }
                    }
                }
            }
        }
    }
}
