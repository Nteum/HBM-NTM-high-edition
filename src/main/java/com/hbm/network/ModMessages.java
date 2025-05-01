package com.hbm.network;

import com.hbm.HBM;
import com.hbm.network.packet.toclient.AuxParticlePacket;
import com.hbm.network.packet.toclient.S2CExplosionEffectPacket;
import com.hbm.network.packet.toclient.UpdateMenuPacket;
import com.hbm.network.packet.toclient.UpdateTilePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    //接受和发送自定义数据包的类
    public static final String version = "1.0";
    public static final SimpleChannel netHandler =  NetworkRegistry.ChannelBuilder.named(HBM.rl("message")).networkProtocolVersion(()->version)
            .clientAcceptedVersions(version::equals).serverAcceptedVersions(version::equals).simpleChannel();
    private static int packetId = 0;
    private static int id(){return packetId++;}

    public static void register(){
        //C2SExplosionEffectPacket
        netHandler.messageBuilder(S2CExplosionEffectPacket.class, packetId++, NetworkDirection.PLAY_TO_CLIENT).decoder(S2CExplosionEffectPacket::new).encoder(S2CExplosionEffectPacket::toBytes)
                .consumerMainThread(S2CExplosionEffectPacket::handle).add();
        //AuxParticlePacket
        netHandler.messageBuilder(AuxParticlePacket.class, packetId++ , NetworkDirection.PLAY_TO_CLIENT).decoder(AuxParticlePacket::new).encoder(AuxParticlePacket::toBytes)
                .consumerMainThread(AuxParticlePacket::handle).add();
        //
        netHandler.messageBuilder(UpdateTilePacket.class, packetId++ , NetworkDirection.PLAY_TO_CLIENT).decoder(UpdateTilePacket::decode).encoder(UpdateTilePacket::encode)
                .consumerMainThread(UpdateTilePacket::handle).add();
//        netHandler.messageBuilder(UpdateMenuPacket.class, packetId++ , NetworkDirection.PLAY_TO_CLIENT).decoder(UpdateMenuPacket::decode).encoder(UpdateMenuPacket::encode)
//                .consumerMainThread(UpdateMenuPacket::handle).add();
    }


    public static <MSG> void sendToServer(MSG message){
        netHandler.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        netHandler.send(PacketDistributor.PLAYER.with(()-> player),message);
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
