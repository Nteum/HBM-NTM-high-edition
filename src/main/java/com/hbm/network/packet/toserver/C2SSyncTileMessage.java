package com.hbm.network.packet.toserver;

import com.hbm.HBM;
import com.hbm.blockentity.base.BaseMachineBlockEntity;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.network.IHBMMessage;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> handleServerSide(context));
        context.setPacketHandled(true);
    }

    /**
     * Validates the sender and target tile before allowing any client-provided
     * data to mutate server state. Older builds jumped straight into
     * {@link UpdateableBlockEntity#handleClientPacket(CompoundTag)}, which meant
     * any malicious client could spoof packets and tamper with machines it did
     * not own.
     */
    private void handleServerSide(NetworkEvent.Context context) {
        ServerPlayer sender = context.getSender();
        if (sender == null) {
            HBM.LOGGER.warn("Received C2SSyncTileMessage without a sender; dropping update at {}", pos);
            return;
        }

        ServerLevel level = sender.serverLevel();
        if (!WorldUtils.isBlockLoaded(level, pos)) {
            return;
        }

        UpdateableBlockEntity tile = WorldUtils.getTileEntity(UpdateableBlockEntity.class, level, pos, true);
        if (tile == null) {
            HBM.LOGGER.warn("Player {} tried to sync tile at {} but none was found", sender.getGameProfile().getName(), pos);
            return;
        }

        if (!isSenderAuthorized(sender, tile)) {
            HBM.LOGGER.warn("Blocked unauthorized sync attempt by {} for tile {}", sender.getGameProfile().getName(), pos);
            return;
        }

        tile.handleClientPacket(updateTag);
    }

    private boolean isSenderAuthorized(ServerPlayer sender, UpdateableBlockEntity tile) {
        // Require the player to be close to the tile to prevent remote tampering.
        final double maxDistanceSq = 16.0D * 16.0D; // 16-block interaction radius
        if (sender.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > maxDistanceSq) {
            return false;
        }

        if (tile instanceof BaseMachineBlockEntity machine) {
            // Reuse the machine's own access check so lockable machines stay protected.
            return machine.canOpen(sender);
        }

        return true;
    }
}
