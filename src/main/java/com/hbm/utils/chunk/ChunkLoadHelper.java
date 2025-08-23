package com.hbm.utils.chunk;

import com.hbm.HBM;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 协助加载mod中的chunk。
 * 目前ForgeChunkManager仍然存在，但似乎无法奏效？尽管代码我看不出什么问题，但在测试中确实无法完成加载任务
 * 本文参照的底层逻辑是：ServerLevel#getChunkSource().addRegionTicket()，经检验，它是可以符合条件的。
 * */
@Mod.EventBusSubscriber(modid = HBM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChunkLoadHelper {
    // 用于记录需要自动加载区块的实体，统一判断它们是否需要加载。
    public static Map<Entity, LongSet> entitiesWithChunkLoad = new HashMap<>();
    /**
     * 使用FORCE强制加载区块
     * 主要用于加载实体采用默认的方式。
     * */
    public static void forceChunk(ServerLevel level, ChunkPos chunkPos, boolean isLoad){
        if (isLoad){
            if (level.hasChunk(chunkPos.x, chunkPos.z)) return;
            LongSet chunks = level.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks").getChunks();
            if (chunks.contains(chunkPos.toLong()) || chunks.size() >= 256)return;
            level.setChunkForced(chunkPos.x, chunkPos.z, true);
//            level.getChunkSource().addRegionTicket(TicketType.FORCED, chunkPos, distance, chunkPos, true);
        }else
            level.setChunkForced(chunkPos.x, chunkPos.z, false);
//            level.getChunkSource().removeRegionTicket(TicketType.FORCED, chunkPos, distance, chunkPos, false);
    }

    public static void register(Entity entity){
        if (entityInServer(entity)){
            entitiesWithChunkLoad.computeIfAbsent(entity, e -> new LongOpenHashSet());
            // 如果实体登录的时候不处在加载区块，就先给它加载一下
            if (!entity.level().isLoaded(entity.blockPosition())){
                ChunkPos chunkPos = new ChunkPos(entity.blockPosition());
                ChunkLoadHelper.forceChunk((ServerLevel) entity.level(), chunkPos, true);
                entitiesWithChunkLoad.get(entity).add(chunkPos.toLong());
            }
        }
    }
    public static void unRegister(Entity entity){
        if (entityInServer(entity) && entitiesWithChunkLoad.containsKey(entity)){
            for (Long chunkPos : entitiesWithChunkLoad.get(entity)) {
                forceChunk((ServerLevel) entity.level(), new ChunkPos(chunkPos), false);
            }
        }
    }
    protected static boolean entityInServer(Entity entity){
        return !entity.level().isClientSide();
    }
    @SubscribeEvent
    public static void onEntityEnterSection(EntityEvent.EnteringSection event){
        Entity entity = event.getEntity();
        if (event.didChunkChange() && entitiesWithChunkLoad.containsKey(entity) && !entity.level().isClientSide){
            ChunkPos newChunk = event.getNewPos().chunk();
            ChunkPos oldChunk = event.getOldPos().chunk();
            LongSet chunks = entitiesWithChunkLoad.get(entity);
            if (chunks.contains(oldChunk.toLong())) {
                forceChunk((ServerLevel) entity.level(), oldChunk, false);
                chunks.remove(oldChunk.toLong());
            }
            if (!entity.level().isLoaded(entity.getOnPos())){
                forceChunk((ServerLevel) entity.level(), newChunk, true);
                chunks.add(newChunk.toLong());
            }
        }
    }
}
