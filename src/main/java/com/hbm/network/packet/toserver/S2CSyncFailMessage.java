package com.hbm.network.packet.toserver;

import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.network.IHBMMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class S2CSyncFailMessage implements IHBMMessage {
    private final BlockPos pos;
    public S2CSyncFailMessage(BlockPos pos){
        this.pos = pos;
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static S2CSyncFailMessage decode(FriendlyByteBuf buf){
        return new S2CSyncFailMessage(buf.readBlockPos());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()->{
            Level level = Objects.requireNonNull(ctx.get().getSender()).level();
            UpdateableBlockEntity blockEntity = (UpdateableBlockEntity) level.getBlockEntity(pos);
            if (blockEntity != null){
                blockEntity.sendUpdatePacket();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
