package com.hbm.network;

import com.hbm.render.hud.ClientHUDDataCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientMsgHandler {
    private static ClientMsgHandler INSTANCE;
    private Map<ResourceLocation, ClientHUDDataCache> tempData = new HashMap<>();

    public static ClientMsgHandler get(){
        return INSTANCE;
    }
    public static ClientMsgHandler getOrCreate(){
        if (INSTANCE == null) INSTANCE = new ClientMsgHandler();
        return INSTANCE;
    }
    public static void handleParticlePacket(CompoundTag tag){
        double x = tag.getDouble("posX");
        double y = tag.getDouble("posY");
        double z = tag.getDouble("posZ");
    }

    public Map<ResourceLocation, ClientHUDDataCache> getTempData(){
        return tempData;
    }

    public void updateUDHPacket(ClientHUDDataCache data){
        if (data.displayMillis() > 0) tempData.put(data.id(), data);
    }
}
