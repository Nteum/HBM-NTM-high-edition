package com.hbm.network.packet.toclient;

import com.hbm.HBM;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.network.IHBMMessage;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toserver.S2CSyncFailMessage;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncTileMessage implements IHBMMessage {
    private final CompoundTag updateTag;
    private final BlockPos pos;
    S2CSyncTileMessage(BlockPos blockPos, CompoundTag tag){
        this.updateTag = tag;
        this.pos = blockPos;
    }

    public S2CSyncTileMessage(UpdateableBlockEntity blockEntity) {
        this(blockEntity.getBlockPos(), blockEntity.getReducedUpdateTag());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ClientLevel world = Minecraft.getInstance().level;
        //Only handle the update packet if the block is currently loaded
        if (WorldUtils.isBlockLoaded(world, pos)) {
//            UpdateableBlockEntity tile = (UpdateableBlockEntity) world.getBlockEntity(pos);
            UpdateableBlockEntity tile = WorldUtils.getTileEntity(UpdateableBlockEntity.class, world, pos, true);
            if (tile == null) {
                HBM.LOGGER.warn("Update tile packet received for position: {} in world: {}, but no valid tile was found.", pos,
                        world.dimension().location());
                ModMessages.sendToServer(new S2CSyncFailMessage(pos));
            } else {
                tile.handleUpdatePacket(updateTag);
            }
        }
        ctx.get().setPacketHandled(true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeNbt(this.updateTag);
    }

    public static S2CSyncTileMessage decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new S2CSyncTileMessage(buf.readBlockPos(),buf.readNbt());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof S2CSyncTileMessage))
            return false;
        S2CSyncTileMessage msg = (S2CSyncTileMessage) obj;
        if (msg.pos != null && msg.pos.equals(this.pos) && msg.updateTag != null && msg.updateTag.equals(this.updateTag))
            return true;
        else
            return false;
    }

    public S2CSyncTileMessage copy(){
        return new S2CSyncTileMessage(new BlockPos(pos.getX(),pos.getY(),pos.getZ()), updateTag.copy());
    }
}
