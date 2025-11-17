package com.hbm.network.packet.toclient;

import com.hbm.render.overlay.AtomicFlashOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Notifies the client to trigger the post-nuclear white flash overlay.
 */
public record S2CAtomicFlashPacket(float strength, int durationTicks) {

    public static S2CAtomicFlashPacket decode(FriendlyByteBuf buffer) {
        float alpha = buffer.readFloat();
        int duration = buffer.readVarInt();
        return new S2CAtomicFlashPacket(alpha, duration);
    }

    public static void encode(S2CAtomicFlashPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.strength());
        buffer.writeVarInt(packet.durationTicks());
    }

    public static void handle(S2CAtomicFlashPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> AtomicFlashOverlay.trigger(packet.strength(), packet.durationTicks()));
        ctx.get().setPacketHandled(true);
    }
}
