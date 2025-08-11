package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LoopedSoundPacket implements IHBMMessage {
    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {

    }
}
