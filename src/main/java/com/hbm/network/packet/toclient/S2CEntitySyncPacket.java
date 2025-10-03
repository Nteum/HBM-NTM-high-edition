package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMMessage;
import com.hbm.addational_data.AdditionalDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 用于同步服务端到客户端的实体数据
 * */
public class S2CEntitySyncPacket implements IHBMMessage {
    private final int entityId;
    private final CompoundTag tag;
    public S2CEntitySyncPacket(Entity entity){
        this(entity.getId(), AdditionalDataManager.getAdditionalData(entity).isPresent() ? AdditionalDataManager.getAdditionalData(entity).get().syncToClient() : new CompoundTag());
    }
    S2CEntitySyncPacket(int entityId, CompoundTag tag){
        this.entityId = entityId;
        this.tag = tag;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(tag);
    }

    public static S2CEntitySyncPacket decode(FriendlyByteBuf buf){
        return new S2CEntitySyncPacket(buf.readInt(), buf.readNbt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            assert Minecraft.getInstance().level != null;
            Entity entity = Minecraft.getInstance().level.getEntity(entityId);
            assert entity != null;
            AdditionalDataManager.getAdditionalData(entity).ifPresent(entityData -> entity.deserializeNBT(tag));
        });
        ctx.get().setPacketHandled(true);
    }
}
