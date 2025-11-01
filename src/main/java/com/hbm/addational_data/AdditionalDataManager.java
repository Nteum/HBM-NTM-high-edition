package com.hbm.addational_data;

import com.hbm.HBM;
import com.hbm.addational_data.chunk.ChunkAdditionalDataProvider;
import com.hbm.addational_data.chunk.IChunkAdditionalData;
import com.hbm.addational_data.chunk.RadiationManager;
import com.hbm.addational_data.entity.EntityAdditionalDataImpl;
import com.hbm.addational_data.entity.EntityAdditionalDataProvider;
import com.hbm.addational_data.entity.EntityEffectHandler;
import com.hbm.addational_data.entity.IEntityAdditionalData;
import com.hbm.network.ModMessages;
import com.hbm.network.packet.toclient.S2CEntitySyncPacket;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = HBM.MODID)
public class AdditionalDataManager {
    public static final Capability<IEntityAdditionalData> ENTITY_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IChunkAdditionalData> CHUNK_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final float randomTickProbability = 0.2F;
    public static final float chunkAccessProb = 0.05f;      // 平均每秒访问区块一次
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event){
        event.register(IEntityAdditionalData.class);
    }
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player){
            if (!event.getObject().getCapability(ENTITY_DATA).isPresent()){
                event.addCapability(HBM.rl("addational"), new EntityAdditionalDataProvider());
            }
        }
    }
    @SubscribeEvent
    public static void onAttachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event){
        LevelChunk chunk = event.getObject();
        if (!chunk.getCapability(CHUNK_DATA).isPresent()){
            event.addCapability(HBM.rl(IAdditionalData.nbtKey + "_chunk"), new ChunkAdditionalDataProvider());
        }
    }

//    @SubscribeEvent
//    public static void onServerUpdate(TickEvent.ServerTickEvent event){
//    }

    @SubscribeEvent
    public static void onLevelUpdate(TickEvent.LevelTickEvent event){
        if (event.side == LogicalSide.SERVER){
            ServerLevel level = (ServerLevel) event.level;
            // 更新已加载区块
            level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder -> {
                LevelChunk fullChunk = chunkHolder.getFullChunk();
                if (fullChunk != null){
                    getChunkData(fullChunk).ifPresent(chunkData -> {
                        // 更新chunk数据
                        RadiationManager.updateRadiation(level, fullChunk, chunkData);
//                        Pollution.update(level, fullChunk, chunkData);
                    });
                }
            });
            //
            Pollution.tick(level);
        }
    }

    @SubscribeEvent
    public static void onEntityUpdate(LivingEvent.LivingTickEvent event){
        if (!event.getEntity().level().isClientSide()){
            // 实体客户端更新
            EntityEffectHandler.onUpdate(event.getEntity());
            // 同步客户端到服务端
            syncEntityData(event.getEntity());
        }
    }

    /**
     * 存储实体数据用
     * */
    @SubscribeEvent
    public static void saveEntityData(LevelEvent.Save event){
        // 直到Level需要存储的时候才存储实体数据，避免频繁写入磁盘
        if (!event.getLevel().isClientSide()){
            ServerLevel level = (ServerLevel) event.getLevel();
            level.getAllEntities().forEach(entity -> entity.getCapability(ENTITY_DATA).ifPresent(entityData -> entity.getPersistentData().put(IEntityAdditionalData.nbtKey,entityData.serializeNBT())));
        }
    }

//    @SubscribeEvent
//    public static void loadEntityData(LevelEvent.Load event){
//        // 直到Level需要读取的时候才读取实体数据，避免频繁写入磁盘
//        if (!event.getLevel().isClientSide()){
//            ServerLevel level = (ServerLevel) event.getLevel();
//            level.getAllEntities().forEach(
//                    entity -> entity.getCapability(ENTITY_DATA).ifPresent(entityData ->
//                            entityData.deserializeNBT((CompoundTag) entity.getPersistentData().get(IEntityAdditionalData.nbtKey))
//                    )
//            );
//        }
//    }
    @SubscribeEvent
    public static void loadEntityData(EntityJoinLevelEvent event){
        if (!event.getLevel().isClientSide() && event.getLevel().isLoaded(event.getEntity().getOnPos())){
            event.getEntity().getCapability(ENTITY_DATA).ifPresent(entityData ->
                    entityData.deserializeNBT((CompoundTag) event.getEntity().getPersistentData().get(IEntityAdditionalData.nbtKey))
            );
        }
    }

    /**
     * 加载玩家数据
     * 玩家是比较特殊的，似乎不能用EntityJoinLevelEvent处理玩家加载的事情。
     * */
    @SubscribeEvent
    public static void loadPlayerData(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        if (!player.level().isClientSide()){
            player.getCapability(ENTITY_DATA).ifPresent(entityData ->
                    entityData.deserializeNBT((CompoundTag) player.getPersistentData().get(IEntityAdditionalData.nbtKey))
            );
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
        ChunkAccess chunk = event.getChunk();
        long chunkId = chunk.getPos().toLong();
        if (chunk instanceof LevelChunk levelChunk){
            levelChunk.getCapability(CHUNK_DATA).ifPresent(chunkData -> {
                event.getData().put(String.valueOf(chunkId), chunkData.serializeNBT());
            });
        }
    }

    @SubscribeEvent
    public static void loadChunkData(ChunkDataEvent.Load event){
        // 加载chunk数据
        ChunkAccess chunk = event.getChunk();
        long chunkId = chunk.getPos().toLong();
        if (chunk instanceof LevelChunk levelChunk){
            levelChunk.getCapability(CHUNK_DATA).ifPresent(chunkData -> {
                chunkData.deserializeNBT(event.getData().getCompound(String.valueOf(chunkId)));
            });
        }
    }
    /**  */
    public static Optional<IEntityAdditionalData> getAdditionalData(Entity entity){
        return entity.getCapability(ENTITY_DATA).resolve();
    }
    public static Optional<IChunkAdditionalData> getChunkData(LevelChunk chunk){
        return chunk.getCapability(CHUNK_DATA).resolve();
    }
    public static boolean checkEntityData(Entity entity, DataEntry entry){
        Optional<IEntityAdditionalData> optional = getAdditionalData(entity);
        if (optional.isPresent()){
            IEntityAdditionalData entityData = optional.get();
            return entityData.contains(entry);
        }
        return false;
    }
    public static Optional<?> getEntityData(Entity entity, DataEntry entry){
        Optional<IEntityAdditionalData> optional = getAdditionalData(entity);
        if (optional.isPresent()){
            IEntityAdditionalData entityData = optional.get();
            return entityData.getData(entry);
        }
        return Optional.empty();
    }
    public static Optional<?> getChunkData(LevelChunk chunk, DataEntry entry){
        Optional<IChunkAdditionalData> optional = getChunkData(chunk);
        if (optional.isPresent()){
            IChunkAdditionalData data = optional.get();
            return data.getData(entry);
        }
        return Optional.empty();
    }
    public static void setEntityData(Entity entity, DataEntry dataEntry, @Nullable Object value){
        getAdditionalData(entity).ifPresent(data -> {
            data.setData(dataEntry, value);
        });
    }
    public static void setChunkData(LevelChunk chunk, DataEntry dataEntry, @Nullable Object value){
        getChunkData(chunk).ifPresent(data -> {
            data.setData(dataEntry, value);
        });
    }
    public static void syncEntityData(Entity entity){
        getAdditionalData(entity).ifPresent(entityData -> {
            if (entityData.shouldSync()) ModMessages.sendToEntity(new S2CEntitySyncPacket(entity), entity);
        });
    }
}
