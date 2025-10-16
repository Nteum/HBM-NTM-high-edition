package com.hbm.network.packet.toserver;

import com.hbm.network.IHBMMessage;
import com.hbm.network.ServerMsgHandler;
import com.hbm.registries.ModKeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class C2SKeyMessage implements IHBMMessage {
    int[] keys;
    public C2SKeyMessage(int[] keys){
        this.keys = keys;
    }
    public C2SKeyMessage(FriendlyByteBuf buf){
        this(buf.readVarIntArray());
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarIntArray(keys);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            ServerMsgHandler.pressedKey.put(ctx.get().getSender().getId(), new HashSet<>(Arrays.stream(keys).mapToObj(key -> ModKeyMapping.keys.get(key)).toList()));
        });
        ctx.get().setPacketHandled(true);
    }
}
