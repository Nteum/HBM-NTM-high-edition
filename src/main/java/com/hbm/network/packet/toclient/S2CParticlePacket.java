package com.hbm.network.packet.toclient;

import com.hbm.network.ClientMsgHandler;
import com.hbm.network.IHBMMessage;
import com.hbm.particle.ParticleSystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CParticlePacket implements IHBMMessage {

    CompoundTag nbt;
    public S2CParticlePacket(CompoundTag tag, double x, double y, double z){
        tag.putDouble("posX", x);
        tag.putDouble("posY", y);
        tag.putDouble("posZ", z);
        this.nbt = tag;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
    }

    public S2CParticlePacket decode(FriendlyByteBuf buf){
        CompoundTag tag = buf.readNbt();
        return new S2CParticlePacket(tag, tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ"));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ParticleSystem.handleParticleCombo(nbt);
        });
        ctx.get().setPacketHandled(true);
    }
}
