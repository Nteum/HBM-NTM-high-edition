package com.hbm.world.level.explosion;

import com.hbm.network.ModMessages;
import com.hbm.network.packet.C2SExplosionEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ExplosionUtils {

    public static void explode(Level pLevel, double x, double y, double z, float radius, boolean cloud, boolean rubble, boolean shrapnel){
        pLevel.explode(null, x , y, z , radius, true, Level.ExplosionInteraction.TNT);
        if(cloud)
            spawnParticles(pLevel, x, y, z, cloudFunction((int)radius));
//        if(rubble)
//            spawnRubble(world, x, y, z, rubbleFunction((int)strength));
//        if(shrapnel)
//            spawnShrapnels(world, x, y, z, shrapnelFunction((int)strength));
    }

    public static void spawnParticles(Level pLevel, double x, double y, double z,int count){
        for (ServerPlayer serverPlayer : ((ServerLevel) pLevel).players()) {
            if (serverPlayer.distanceToSqr(x, y, z) < 4096.0D) {
                ModMessages.sendToPlayer(new C2SExplosionEffectPacket(x,y,z,1,1,count),serverPlayer);
            }
        }
    }

    public static int cloudFunction(int i) {
        return (int)(850 * (1 - Math.pow(Math.E, -i/15)) + 15);
    }

    public static int rubbleFunction(int i) {
        return i/10;
    }

    public static int shrapnelFunction(int i) {
        return i/3;
    }
}
