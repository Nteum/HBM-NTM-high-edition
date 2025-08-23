package com.hbm.utils.creatures_data;

import com.hbm.HBM;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CEntitySyncPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = HBM.MODID)
public class AdditionalDataManager {
    public static final Capability<IEntityAdditionalData> ENTITY_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final float randomTickProbability = 0.2F;
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IEntityAdditionalData.class);
    }
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
        event.addCapability(HBM.rl("addational"), new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return LazyOptional.of(EntityAdditionalDataImpl::new).cast();
            }
        });
    }
    @SubscribeEvent
    public static void onAttachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event){

    }

    @SubscribeEvent
    public static void onRandomTick(TickEvent.LevelTickEvent event){
        // 为什么要用randomtick，就是为了不在每个tick处理数据的同时，避免和其他任务的使用的tick完全相同。
        if (event.level.random.nextFloat() > randomTickProbability) return;
        if (event.side == LogicalSide.SERVER){
            ServerLevel level = (ServerLevel) event.level;
            level.getAllEntities().forEach(entity -> entity.getCapability(ENTITY_DATA).ifPresent(entityData -> {
                // 里面更新处理逻辑

                // 同步客户端到服务端
                if (entityData.shouldSync()) ModMessages.sendToEntity(new S2CEntitySyncPacket(entity), entity);
            }));
        }
    }

    @SubscribeEvent
    public static void saveEntityData(LevelEvent.Save event){
        // 直到Level需要存储的时候才存储实体数据，避免频繁写入磁盘
        if (!event.getLevel().isClientSide()){
            ServerLevel level = (ServerLevel) event.getLevel();
            level.getAllEntities().forEach(entity -> entity.getCapability(ENTITY_DATA).ifPresent(entityData -> entity.getPersistentData().put(IEntityAdditionalData.nbtKey,entityData.serializeNBT())));
        }
    }

    @SubscribeEvent
    public static void loadEntityData(LevelEvent.Load event){
        // 直到Level需要读取的时候才读取实体数据，避免频繁写入磁盘
        if (!event.getLevel().isClientSide()){
            ServerLevel level = (ServerLevel) event.getLevel();
            level.getAllEntities().forEach(entity -> entity.getCapability(ENTITY_DATA).ifPresent(entityData -> entityData.deserializeNBT((CompoundTag) entity.getPersistentData().get(IEntityAdditionalData.nbtKey))));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.Clone event){
        event.getOriginal().getCapability(ENTITY_DATA).ifPresent(oldCap -> {
            event.getEntity().getCapability(ENTITY_DATA).ifPresent(newCap -> {
                newCap.copyAfterDeath(oldCap);
            });
        });
    }

    @SubscribeEvent
    public static void saveChunkData(ChunkDataEvent.Save event){
        // 存入chunk数据
    }

    @SubscribeEvent
    public static void loadChunkData(ChunkDataEvent.Load event){
        // 加载chunk数据
    }

    public static Optional<IEntityAdditionalData> getEntityData(Entity entity){
        return entity.getCapability(ENTITY_DATA).resolve();
    }
}
