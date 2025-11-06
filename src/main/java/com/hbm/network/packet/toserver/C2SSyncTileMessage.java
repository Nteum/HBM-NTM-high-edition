package com.hbm.network.packet.toserver;

import com.hbm.HBM;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.blockentity.base2.UpdateableBlockEntity;
import com.hbm.network.IHBMMessage;
import com.hbm.network.packet.toclient.S2CSyncTileMessage;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SSyncTileMessage implements IHBMMessage {
    private final CompoundTag updateTag;
    private final BlockPos pos;
    public C2SSyncTileMessage(BlockPos blockPos, CompoundTag tag){
        this.updateTag = tag;
        this.pos = blockPos;
    }

    public C2SSyncTileMessage(UpdateableBlockEntity blockEntity) {
        this(blockEntity.getBlockPos(), blockEntity.getClientSyncTag());
    }
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeNbt(this.updateTag);
    }
    public static C2SSyncTileMessage decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new C2SSyncTileMessage(buf.readBlockPos(),buf.readNbt());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) { /* 此处有客户端任意伪造Packet漏洞：
        导致服务端无鉴权直接调用UpdateableBlockEntity.handleClientPacket
        后果：任意客户端可修改他人机器状态或注入恶意数据。*/
        UpdateableBlockEntity.handleClientPacket
        ctx.get().enqueueWork(() -> {
            ServerLevel level = ctx.get().getSender().serverLevel();
            if (WorldUtils.isBlockLoaded(level, pos)) {
                UpdateableBlockEntity tile = WorldUtils.getTileEntity(UpdateableBlockEntity.class, level, pos, true);
                if (tile == null) {
                    HBM.LOGGER.warn("Update server blockentity at position: {} in world: {}, but no valid tile was found.", pos, level.dimension().location());
                } else {
                    tile.handleClientPacket(updateTag);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
