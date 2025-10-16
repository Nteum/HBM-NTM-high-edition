package com.hbm.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientMsgHandler {

    public static void handleParticlePacket(CompoundTag tag){
        double x = tag.getDouble("posX");
        double y = tag.getDouble("posY");
        double z = tag.getDouble("posZ");
    }
}
