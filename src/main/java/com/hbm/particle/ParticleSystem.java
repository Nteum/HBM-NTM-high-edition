package com.hbm.particle;

import com.hbm.particle.type.ParticleRocketFlame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 粒子系统，其实只是和粒子相关的东西的大杂烩。
 * bob习惯在客户端直接生成粒子，而不是在服务端发包，我其实很不喜欢这种方式，尊重他的选择。
 * */
public class ParticleSystem {
    @OnlyIn(Dist.CLIENT)
    public static void addRocketFlame(double pX, double pY, double pZ, double movX, double movY, double movZ, @Nullable Float scale, @Nullable Integer lifetime){
        if (Minecraft.getInstance().player.position().distanceTo(new Vec3(pX,pY,pZ)) > 350) return;
        ParticleRocketFlame particle = (ParticleRocketFlame)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.ROCKET_FLAME.get(), pX, pY, pZ, movX, movY, movZ);
        if (particle == null) return;
        if (scale != null) particle.scale(scale);
        if (lifetime != null) particle.setLifetime(lifetime);
        Minecraft.getInstance().particleEngine.add(particle);
    }
}
