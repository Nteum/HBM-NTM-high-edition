package com.hbm.particle;

import com.hbm.HBM;
import com.hbm.datagen.HBMJsonProvider;
import com.hbm.particle.type.*;
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

import java.util.*;

//注册所有的粒子类型
public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HBM.MODID);
    public static final Map<RegistryObject<SimpleParticleType>, ParticleEngine.SpriteParticleRegistration<SimpleParticleType>> simpleParticles = new HashMap<>();
    public static Map<String, String> texMap = new HashMap<>();

    public static final RegistryObject<SimpleParticleType> HBM_SMOKE = addSimple("nuke_smoke",HBMSmokeParticle::new);
    public static final RegistryObject<SimpleParticleType> ROCKET_FLAME = addSimple("rocket_flame", "contrail", ParticleRocketFlame::new);
    public static final RegistryObject<SimpleParticleType> RADIATION_FOG = addSimple("radiation_fog", "fog", ParticleRadiationFog::new);
    public static final RegistryObject<SimpleParticleType> SHOCKWAVE = addSimple("shockwave", ShockWaveParticle::new);
    public static final RegistryObject<SimpleParticleType> DEAD_LEAF = addSimple("dead_leaf", DeadLeafParticle::new);
    public static final RegistryObject<SimpleParticleType> LAUNCH_SMOKE = addSimple("launch_smoke", "contrail", ParticleSmokePlume::new);
    public static final RegistryObject<SimpleParticleType> CONTRAIL = addSimple("contrail", "contrail", ParticleContrail::new);
    public static final RegistryObject<SimpleParticleType> EX_SMOKE = addSimple("ex_smoke", "particle_base", ParticleExSmoke::new);
    public static final RegistryObject<SimpleParticleType> DIGAMMA_SMOKE = addSimple("digamma_smoke", "particle_base", ParticleDigammaSmoke::new);
    public static final RegistryObject<SimpleParticleType> FOAM = addSimple("foam", "particle_base", ParticleFoam::new);
    public static final RegistryObject<SimpleParticleType> LETTER = addSimple("letter", "particle_base", ParticleLetter::new);
    public static final RegistryObject<SimpleParticleType> MUKEWAVE = addSimple("mukewave", "shockwave", ParticleMukeWave::new);

    public static RegistryObject<SimpleParticleType> addSimple(String name, SimpleParticleConstructor<? extends Particle> constructor){
        return addSimple(name, name, constructor);
    }
    public static RegistryObject<SimpleParticleType> addSimple(String name, String tex, SimpleParticleConstructor<? extends Particle> constructor){
        RegistryObject<SimpleParticleType> object = PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
        simpleParticles.put(object, spriteSet -> new ParticleProvider<>() {
            @Nullable
            @Override
            public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
                return constructor.create(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, spriteSet);
            }
        });
        texMap.put(name, tex);
        return object;
    }
    public static void register(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        simpleParticles.forEach((registry, provider) -> event.registerSpriteSet(registry.get(), provider));
    }

    public static void generateJson(HBMJsonProvider provider){
//        simpleParticles.forEach((k,v) -> provider.simpleParticle(k.getId().getPath()));
        // 由于hbm中多种粒子复用同一个贴图，因此这里直接把名称分为两部分
        simpleParticles.forEach((k,v) -> provider.simpleParticle2Name(k.getId().getPath(), texMap.get(k.getId().getPath())));
//        texMap = null;
    }

    @FunctionalInterface
    @OnlyIn(Dist.CLIENT)
    public interface SimpleParticleConstructor<T extends Particle> {
        T create(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites);
    }
}