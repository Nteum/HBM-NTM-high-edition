package com.hbm.render.hud;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public record ClientHUDDataCache(ResourceLocation id, long startTime, int displayMillis, CompoundTag data) {
    public static ClientHUDDataCache of(ResourceLocation id, int displayMillis, CompoundTag data){
        return new ClientHUDDataCache(id, System.currentTimeMillis(), displayMillis, data);
    }
}
