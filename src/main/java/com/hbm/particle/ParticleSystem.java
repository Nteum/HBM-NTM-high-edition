package com.hbm.particle;

import com.hbm.HBMKey;
import com.hbm.addational_data.Pollution;
import com.hbm.particle.type.ParticleRocketFlame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 粒子系统，其实只是和粒子相关的东西的大杂烩。
 * bob习惯在客户端直接生成粒子，而不是在服务端发包，我其实很不喜欢这种方式，尊重他的选择。
 * */
public class ParticleSystem {
    public static final ConcurrentMap<String, BiConsumer<CompoundTag, Vec3>> PARTICLE_COMBOS = new ConcurrentHashMap<>();
    static {
        PARTICLE_COMBOS.put("waterSplash", ParticleSystem::waterSplash);
        PARTICLE_COMBOS.put("ABMContrail", ParticleSystem::contrailABM);
        PARTICLE_COMBOS.put("launchSmoke", ParticleSystem::launchSmoke);
        PARTICLE_COMBOS.put("exKerosene", ParticleSystem::exKerosene);
    }

    public static void handleParticleCombo(CompoundTag tag){
        if (Minecraft.getInstance().level == null) return;
        if (tag.contains(HBMKey.TYPE, Tag.TAG_STRING) && tag.contains(HBMKey.X, Tag.TAG_DOUBLE) && tag.contains(HBMKey.Y, Tag.TAG_DOUBLE) && tag.contains(HBMKey.Z, Tag.TAG_DOUBLE)){
            String type = tag.getString(HBMKey.TYPE);
            PARTICLE_COMBOS.getOrDefault(type, (tag1, pos) -> {}).accept(tag, new Vec3(tag.getDouble(HBMKey.X), tag.getDouble(HBMKey.Y), tag.getDouble(HBMKey.Z)));
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static void addRocketFlame(double pX, double pY, double pZ, double movX, double movY, double movZ, @Nullable Float scale, @Nullable Integer lifetime){
        if (Minecraft.getInstance().player.position().distanceTo(new Vec3(pX,pY,pZ)) > 350) return;
        ParticleRocketFlame particle = (ParticleRocketFlame)Minecraft.getInstance().particleEngine.makeParticle(ModParticleTypes.ROCKET_FLAME.get(), pX, pY, pZ, movX, movY, movZ);
        if (particle == null) return;
        if (scale != null) particle.scale(scale);
        if (lifetime != null) particle.setLifetime(lifetime);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    public static void waterSplash(CompoundTag tag, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        for (int i = 0; i < 10; i++) {
            level.addParticle(ParticleTypes.CLOUD, position.x + level.random.nextGaussian(), position.y + level.random.nextGaussian(), position.z + level.random.nextGaussian(), 0, 0, 0);
        }
    }

    public static void contrailABM(CompoundTag tag, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        level.addParticle(ModParticleTypes.ROCKET_FLAME.get(), position.x, position.y, position.z, 0, 0, 0);
    }

    public static void launchSmoke(CompoundTag data, Vec3 position){
        ClientLevel level = Minecraft.getInstance().level;
        double motionX = data.getDouble("moX");
        double motionY = data.getDouble("moY");
        double motionZ = data.getDouble("moZ");
        level.addParticle(ModParticleTypes.LAUNCH_SMOKE.get(), position.x, position.y, position.z, motionX, motionY, motionZ);
    }

    public static void exKerosene(CompoundTag data, Vec3 position){
        Minecraft.getInstance().level.addParticle(ModParticleTypes.CONTRAIL.get(), position.x, position.y, position.z, 0, 0, 0);
    }

}
