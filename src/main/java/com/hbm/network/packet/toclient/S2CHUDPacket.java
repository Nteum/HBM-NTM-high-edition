package com.hbm.network.packet.toclient;

import com.hbm.network.ClientMsgHandler;
import com.hbm.core.network.IHBMMessage;
import com.hbm.render.hud.ClientHUDDataCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CHUDPacket(ResourceLocation id, int displayMillis, CompoundTag data) implements IHBMMessage {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        buf.writeInt(displayMillis);
        buf.writeNbt(data);
    }

    public static S2CHUDPacket decode(FriendlyByteBuf buf){
        return new S2CHUDPacket(buf.readResourceLocation(), buf.readInt(), buf.readNbt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientMsgHandler.getOrCreate().updateUDHPacket(ClientHUDDataCache.of(id, displayMillis, data));
        });
        ctx.get().setPacketHandled(true);
    }
}
