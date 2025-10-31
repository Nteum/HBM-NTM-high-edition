package net.mcreator.nuclearcraft.network;

import java.util.function.Supplier;
import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/BigExplosivesModVariables.class */
public class BigExplosivesModVariables {
    public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() { // from class: net.mcreator.nuclearcraft.network.BigExplosivesModVariables.1
    });

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        BigExplosivesMod.addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::buffer, PlayerVariablesSyncMessage::new, PlayerVariablesSyncMessage::handler);
    }

    @SubscribeEvent
    public static void init(RegisterCapabilitiesEvent event) {
        event.register(PlayerVariables.class);
    }

    @Mod.EventBusSubscriber
    /* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/BigExplosivesModVariables$EventBusVariableHandlers.class */
    public static class EventBusVariableHandlers {
        @SubscribeEvent
        public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().m_9236_().m_5776_()) {
                ((PlayerVariables) event.getEntity().getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
            if (!event.getEntity().m_9236_().m_5776_()) {
                ((PlayerVariables) event.getEntity().getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!event.getEntity().m_9236_().m_5776_()) {
                ((PlayerVariables) event.getEntity().getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables())).syncPlayerVariables(event.getEntity());
            }
        }

        @SubscribeEvent
        public static void clonePlayer(PlayerEvent.Clone event) {
            event.getOriginal().revive();
            PlayerVariables original = (PlayerVariables) event.getOriginal().getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables());
            PlayerVariables clone = (PlayerVariables) event.getEntity().getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables());
            clone.fadevariable = original.fadevariable;
            clone.ScreenShake = original.ScreenShake;
            if (!event.isWasDeath()) {
            }
        }
    }

    @Mod.EventBusSubscriber
    /* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/BigExplosivesModVariables$PlayerVariablesProvider.class */
    private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
        private final PlayerVariables playerVariables = new PlayerVariables();
        private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> {
            return this.playerVariables;
        });

        private PlayerVariablesProvider() {
        }

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if ((event.getObject() instanceof Player) && !(event.getObject() instanceof FakePlayer)) {
                event.addCapability(new ResourceLocation(BigExplosivesMod.MODID, "player_variables"), new PlayerVariablesProvider());
            }
        }

        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY ? this.instance.cast() : LazyOptional.empty();
        }

        public Tag serializeNBT() {
            return this.playerVariables.writeNBT();
        }

        public void deserializeNBT(Tag nbt) {
            this.playerVariables.readNBT(nbt);
        }
    }

    /* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/BigExplosivesModVariables$PlayerVariables.class */
    public static class PlayerVariables {
        public double fadevariable = 0.0d;
        public double ScreenShake = 0.0d;

        public void syncPlayerVariables(Entity entity) {
            if (entity instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer) entity;
                BigExplosivesMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> {
                    return serverPlayer;
                }), new PlayerVariablesSyncMessage(this));
            }
        }

        public Tag writeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.m_128347_("fadevariable", this.fadevariable);
            nbt.m_128347_("ScreenShake", this.ScreenShake);
            return nbt;
        }

        public void readNBT(Tag tag) {
            CompoundTag nbt = (CompoundTag) tag;
            this.fadevariable = nbt.m_128459_("fadevariable");
            this.ScreenShake = nbt.m_128459_("ScreenShake");
        }
    }

    /* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/network/BigExplosivesModVariables$PlayerVariablesSyncMessage.class */
    public static class PlayerVariablesSyncMessage {
        private final PlayerVariables data;

        public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
            this.data = new PlayerVariables();
            this.data.readNBT(buffer.m_130260_());
        }

        public PlayerVariablesSyncMessage(PlayerVariables data) {
            this.data = data;
        }

        public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
            buffer.m_130079_(message.data.writeNBT());
        }

        public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getDirection().getReceptionSide().isServer()) {
                    PlayerVariables variables = (PlayerVariables) Minecraft.m_91087_().f_91074_.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new PlayerVariables());
                    variables.fadevariable = message.data.fadevariable;
                    variables.ScreenShake = message.data.ScreenShake;
                }
            });
            context.setPacketHandled(true);
        }
    }
}
