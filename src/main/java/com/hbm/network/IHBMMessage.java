package com.hbm.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface IHBMMessage {
    void encode(FriendlyByteBuf buf);
    void handle(Supplier<NetworkEvent.Context> ctx);
}