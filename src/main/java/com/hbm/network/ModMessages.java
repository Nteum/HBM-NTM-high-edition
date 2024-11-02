package com.hbm.network;

import com.hbm.main.HBMxx;
import com.hbm.network.packet.C2SExplosionEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    //接受和发送自定义数据包的类
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id(){return packetId++;}

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(HBMxx.MODID,"messages"))
                .networkProtocolVersion(()->"1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = net;

        net.messageBuilder(C2SExplosionEffectPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(C2SExplosionEffectPacket::new)
                .encoder(C2SExplosionEffectPacket::toBytes)
                .consumerMainThread(C2SExplosionEffectPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()-> player),message);
    }
}
