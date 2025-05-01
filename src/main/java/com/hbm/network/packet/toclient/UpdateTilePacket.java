package com.hbm.network.packet.toclient;

import com.hbm.HBM;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.network.IHBMPacket;
import com.hbm.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateTilePacket implements IHBMPacket {
    private final CompoundTag updateTag;
    private final BlockPos pos;
    public UpdateTilePacket(BaseMachineBlockEntity blockEntity){
        this(blockEntity.getBlockPos(), blockEntity.getReducedUpdateTag());
    }
    UpdateTilePacket(BlockPos blockPos, CompoundTag tag){
        this.updateTag = tag;
        this.pos = blockPos;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
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
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeNbt(this.updateTag);
    }

    public static UpdateTilePacket decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new UpdateTilePacket(buf.readBlockPos(),buf.readNbt());
    }
}
