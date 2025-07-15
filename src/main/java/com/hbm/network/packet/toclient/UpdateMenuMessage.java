package com.hbm.network.packet.toclient;

import com.hbm.network.IHBMMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateMenuMessage implements IHBMMessage {
    private final int containerId;
    private final CompoundTag updateTag;
    public UpdateMenuMessage(int containerId, CompoundTag updateTag){
        this.containerId = containerId;
        this.updateTag = updateTag;
    }
    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
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
    public static UpdateMenuMessage decode(FriendlyByteBuf buf){
        //记清：decode的顺序要和encode一致
        return new UpdateMenuMessage(buf.readInt(), buf.readNbt());
    }
}
