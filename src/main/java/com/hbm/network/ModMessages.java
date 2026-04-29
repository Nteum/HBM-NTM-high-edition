package com.hbm.network;

import com.hbm.HBM;
import com.hbm.network.packet.toclient.*;
import com.hbm.network.packet.toserver.C2SKeyMessage;
import com.hbm.network.packet.toserver.C2SSyncTileMessage;
import com.hbm.network.packet.toserver.S2CSyncFailMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModMessages {
    //接受和发送自定义数据包的类
    public static final String version = "1.0";
    public static final SimpleChannel netHandler =  NetworkRegistry.ChannelBuilder.named(HBM.rl("message")).networkProtocolVersion(()->version)
            .clientAcceptedVersions(version::equals).serverAcceptedVersions(version::equals).simpleChannel();
    private static int packetId = 0;

    public static void register(){
        registerServerToClient(AuxParticlePacket.class, AuxParticlePacket::decode, AuxParticlePacket::encode, AuxParticlePacket::handle);
        registerServerToClient(S2CBatchedRenderUpdatePacket.class, S2CBatchedRenderUpdatePacket::decode, S2CBatchedRenderUpdatePacket::encode, S2CBatchedRenderUpdatePacket::handle);
        registerServerToClient(S2CSyncTileMessage.class, S2CSyncTileMessage::decode, S2CSyncTileMessage::encode, S2CSyncTileMessage::handle);
        registerServerToClient(S2CEntitySyncPacket.class, S2CEntitySyncPacket::decode, S2CEntitySyncPacket::encode, S2CEntitySyncPacket::handle);
        registerServerToClient(S2CAtomicFlashPacket.class, S2CAtomicFlashPacket::decode, S2CAtomicFlashPacket::encode, S2CAtomicFlashPacket::handle);
        registerServerToClient(S2CExplosionPacket.class, S2CExplosionPacket::new, S2CExplosionPacket::encode, S2CExplosionPacket::handle);

        registerClientToServer(C2SSyncTileMessage.class, C2SSyncTileMessage::decode, C2SSyncTileMessage::encode, C2SSyncTileMessage::handle);
        registerClientToServer(S2CSyncFailMessage.class, S2CSyncFailMessage::decode, S2CSyncFailMessage::encode, S2CSyncFailMessage::handle);
        registerClientToServer(C2SKeyMessage.class, C2SKeyMessage::new, C2SKeyMessage::encode, C2SKeyMessage::handle);
    }

    public static <MSG>void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder , BiConsumer<MSG, FriendlyByteBuf> encoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer){
        registerMessage(type, decoder, encoder, consumer, NetworkDirection.PLAY_TO_SERVER);
    }
    public static <MSG>void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder , BiConsumer<MSG, FriendlyByteBuf> encoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer){
        registerMessage(type, decoder, encoder, consumer, NetworkDirection.PLAY_TO_CLIENT);
    }
    //这里参考的是mek中的注册，它之间用的simplechannel自带的注册方式，但似乎和channelBuilder的方式有所区别。
    public static <MSG>void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder , BiConsumer<MSG, FriendlyByteBuf> encoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer, NetworkDirection direction){
        netHandler.registerMessage(packetId++, type, encoder, decoder, consumer, Optional.of(direction));
    }

    public static <MSG> void sendToServer(MSG message){
        netHandler.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        netHandler.send(PacketDistributor.PLAYER.with(()-> player),message);
    }
    public static <MSG> void sendToEntity(MSG message, Entity entity){
        netHandler.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity),message);
    }
    public static <MSG> void sendToDimension(MSG message, ResourceKey<Level> dimensionId){
        netHandler.send(PacketDistributor.DIMENSION.with(()->dimensionId),message);
    }
    public static <MSG> void sendToAllAround(MSG message, Entity entity, double radius) {
        sendToAllAround(message, new PacketDistributor.TargetPoint(entity.getX(), entity.getY(), entity.getZ(), radius, entity.level().dimension()));
    }
    public static <MSG> void sendToAllAround(MSG message, PacketDistributor.TargetPoint point) {
        netHandler.send(PacketDistributor.NEAR.with(()->point),message);
    }
    public static <MSG> void sendToAll(MSG message){
        netHandler.send(PacketDistributor.ALL.noArg(), message);
    }
    public static <MSG> void sendToAllTracking(MSG message, BlockEntity tile) {
        sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
    }

    public static <MSG> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
        if (world instanceof ServerLevel level) {
            //If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
            // This provides a decent performance boost over using the packet distributor
            level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendToPlayer(message, p));
        } else {
            //Otherwise, fallback to entities tracking the chunk if some mod did something odd and our world is not a ServerWorld
            netHandler.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))), message);
        }
    }
}
