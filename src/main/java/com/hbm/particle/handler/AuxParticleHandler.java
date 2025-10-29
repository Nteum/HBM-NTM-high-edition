package com.hbm.particle.handler;

import com.hbm.particle.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 主要用于处理AuxParticlePacket、AuxParticlePacketNT两种
 * 用于生成一些粒子
 * */
@OnlyIn(Dist.CLIENT)
public class AuxParticleHandler {
    public static void particleControl(double x, double y, double z, int type) {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;

        switch(type) {
            case 0:
                for(int i = 0; i < 10; i++) {
                    level.addParticle(ParticleTypes.CLOUD, false,x + level.random.nextGaussian(), y + level.random.nextGaussian(),z + level.random.nextGaussian(),0.0,0.0,0);
                }
                break;

            case 1:
                level.addParticle(ParticleTypes.CLOUD, false,x, y,z,0.0,0.1,0);
                break;

            case 2:
                level.addParticle(ModParticleTypes.ROCKET_FLAME.get(), false,x, y, z,0.0,0.0,0);
                break;

            case 3:
                level.addParticle(ModParticleTypes.RADIATION_FOG.get(), false,x, y, z,0.0,0.0,0);
                break;

            case 4:
                for (int i = 0; i < 6; i++) {
                    double offsetX = level.random.nextGaussian() * 0.08D;
                    double offsetZ = level.random.nextGaussian() * 0.08D;
                    double offsetY = level.random.nextDouble() * 0.15D;
                    double speedX = level.random.nextGaussian() * 0.01D;
                    double speedZ = level.random.nextGaussian() * 0.01D;
                    double speedY = 0.01D + level.random.nextDouble() * 0.01D;
                    level.addParticle(ModParticleTypes.CHERENKOV_GLOW.get(), false,
                            x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                }
                break;
        }
    }
}
