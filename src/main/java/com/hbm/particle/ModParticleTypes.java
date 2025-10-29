package com.hbm.particle;

import com.hbm.HBM;
import com.hbm.datagen.HBMJsonProvider;
import com.hbm.particle.type.DeadLeafParticle;
import com.hbm.particle.type.HBMSmokeParticle;
import com.hbm.particle.type.ParticleCherenkov;
import com.hbm.particle.type.ParticleRocketFlame;
import com.hbm.particle.type.ShockWaveParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//注册所有的粒子类型
public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HBM.MODID);
    public static final Map<RegistryObject<SimpleParticleType>, ParticleEngine.SpriteParticleRegistration<SimpleParticleType>> simpleParticles = new HashMap<>();

    public static final RegistryObject<SimpleParticleType> HBM_SMOKE = PARTICLE_TYPES.register("nuke_smoke",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ROCKET_FLAME = PARTICLE_TYPES.register("missile_contrail",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RADIATION_FOG = PARTICLE_TYPES.register("fog",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SHOCKWAVE = addSimple("shockwave", ShockWaveParticle::new);
    public static final RegistryObject<SimpleParticleType> DEAD_LEAF = addSimple("dead_leaf", DeadLeafParticle::new);
    public static final RegistryObject<SimpleParticleType> LAUNCH_SMOKE = addSimple("launch_smoke", DeadLeafParticle::new);
    public static final RegistryObject<SimpleParticleType> CHERENKOV_GLOW = addSimple("cherenkov_glow", ParticleCherenkov::new);

    public static RegistryObject<SimpleParticleType> addSimple(String name, SimpleParticleConstructor<? extends Particle> constructor){
        RegistryObject<SimpleParticleType> object = PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
        simpleParticles.put(object, spriteSet -> new ParticleProvider<>() {
            @Nullable
            @Override
            public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
                return constructor.create(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, spriteSet);
            }
        });
        return object;
    }
    public static void register(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        event.registerSpriteSet(ModParticleTypes.HBM_SMOKE.get(), HBMSmokeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.ROCKET_FLAME.get(), ParticleRocketFlame.Provider::new);
        simpleParticles.forEach((registry, provider) -> event.registerSpriteSet(registry.get(), provider));
    }

    public static void generateJson(HBMJsonProvider provider){
        simpleParticles.forEach((k,v) -> provider.simpleParticle(k.getId().getPath()));
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface SimpleParticleConstructor<T extends Particle> {
        T create(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites);
    }
}
