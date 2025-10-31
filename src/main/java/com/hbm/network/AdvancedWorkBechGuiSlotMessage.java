package net.mcreator.nuclearcraft.network;

import java.util.HashMap;
import java.util.function.Supplier;
import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.mcreator.nuclearcraft.procedures.ItemTakenFromAdvancedOutputProcedure;
import net.mcreator.nuclearcraft.world.inventory.AdvancedWorkBechGuiMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/AdvancedWorkBechGuiSlotMessage.class */
public class AdvancedWorkBechGuiSlotMessage {
    private final int slotID;
    private final int x;
    private final int y;
    private final int z;
    private final int changeType;
    private final int meta;

    public AdvancedWorkBechGuiSlotMessage(int slotID, int x, int y, int z, int changeType, int meta) {
        this.slotID = slotID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.changeType = changeType;
        this.meta = meta;
    }

    public AdvancedWorkBechGuiSlotMessage(FriendlyByteBuf buffer) {
        this.slotID = buffer.readInt();
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.changeType = buffer.readInt();
        this.meta = buffer.readInt();
    }

    public static void buffer(AdvancedWorkBechGuiSlotMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.slotID);
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
        buffer.writeInt(message.changeType);
        buffer.writeInt(message.meta);
    }

    public static void handler(AdvancedWorkBechGuiSlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            int slotID = message.slotID;
            int changeType = message.changeType;
            int meta = message.meta;
            int x = message.x;
            int y = message.y;
            int z = message.z;
            handleSlotAction(sender, slotID, changeType, meta, x, y, z);
        });
        context.setPacketHandled(true);
    }

    public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
        Level world = entity.m_9236_();
        HashMap<String, Object> map = AdvancedWorkBechGuiMenu.guistate;
        if (world.m_46805_(new BlockPos(x, y, z)) && slot == 16 && changeType == 1) {
            ItemTakenFromAdvancedOutputProcedure.execute(entity);
        }
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        BigExplosivesMod.addNetworkMessage(AdvancedWorkBechGuiSlotMessage.class, AdvancedWorkBechGuiSlotMessage::buffer, AdvancedWorkBechGuiSlotMessage::new, AdvancedWorkBechGuiSlotMessage::handler);
    }
}
