package com.hbm.network.packet.toclient;

import com.hbm.HBM;
import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import com.hbm.blockentity.base2.UpdateableBlockEntity;
import com.hbm.network.IHBMMessage;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateTileMessage implements IHBMMessage {
    private final CompoundTag updateTag;
    private final BlockPos pos;
    public UpdateTileMessage(BaseMachineBlockEntity blockEntity){
        this(blockEntity.getBlockPos(), blockEntity.getReducedUpdateTag());
    }
    UpdateTileMessage(BlockPos blockPos, CompoundTag tag){
        this.updateTag = tag;
        this.pos = blockPos;
    }

    public UpdateTileMessage(UpdateableBlockEntity blockEntity) {
        this(blockEntity.getBlockPos(), blockEntity.getReducedUpdateTag());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ClientLevel world = Minecraft.getInstance().level;
        //Only handle the update packet if the block is currently loaded
        if (WorldUtils.isBlockLoaded(world, pos)) {
            BaseMachineBlockEntity tile = WorldUtils.getTileEntity(BaseMachineBlockEntity.class, world, pos, true);
            if (tile == null) {
                HBM.LOGGER.warn("Update tile packet received for position: {} in world: {}, but no valid tile was found.", pos,
                        world.dimension().location());
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

    public static UpdateTileMessage decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new UpdateTileMessage(buf.readBlockPos(),buf.readNbt());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UpdateTileMessage))
            return false;
        UpdateTileMessage msg = (UpdateTileMessage) obj;
        if (msg.pos != null && msg.pos.equals(this.pos) && msg.updateTag != null && msg.updateTag.equals(this.updateTag))
            return true;
        else
            return false;
    }

    public UpdateTileMessage copy(){
        return new UpdateTileMessage(new BlockPos(pos.getX(),pos.getY(),pos.getZ()), updateTag.copy());
    }
}
