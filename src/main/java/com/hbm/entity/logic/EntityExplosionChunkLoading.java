package com.hbm.entity.logic;

import com.hbm.HBM;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;

public abstract class EntityExplosionChunkLoading extends Entity {
    ServerLevel serverLevel;
    ChunkPos chunkPos;

    public EntityExplosionChunkLoading(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        if(pLevel instanceof ServerLevel){
            serverLevel = (ServerLevel) pLevel;
        }
    }

    public void init(ServerLevel level){
        serverLevel = level;
    }

    public void loadChunk(){
        this.loadChunk(this.chunkPosition());
    }
    public void loadChunk(ChunkPos chunkPos){
        this.loadChunk(chunkPos.x,chunkPos.z);
    }
    public void loadChunk(int x, int z){
        if (this.chunkPos == null){
            chunkPos = new ChunkPos(x,z);
            ForgeChunkManager.forceChunk(serverLevel, HBM.MODID,this,x,z,true,true);
        }
    }

    public void clearChunkLoader(){
        if (!level().isClientSide && serverLevel != null && chunkPos != null){
            ForgeChunkManager.forceChunk(serverLevel, HBM.MODID,this,chunkPos.x,chunkPos.z,false,false);
        }
    }
}
