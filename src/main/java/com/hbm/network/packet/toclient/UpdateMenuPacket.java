package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateMenuPacket implements IHBMPacket {
    private final int containerId;
    private final CompoundTag updateTag;
    public UpdateMenuPacket(int containerId, CompoundTag updateTag){
        this.containerId = containerId;
        this.updateTag = updateTag;
    }
    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        Minecraft minecraft = Minecraft.getInstance();

        Player player = minecraft.player;
        if (player.containerMenu != null && player.containerMenu.containerId == this.containerId) {

        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeNbt(updateTag);
    }
    public static UpdateMenuPacket decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new UpdateMenuPacket(buf.readInt(), buf.readNbt());
    }
}
