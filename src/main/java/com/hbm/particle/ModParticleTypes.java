package com.hbm.particle;

import com.hbm.HBM;
import com.hbm.particle.type.HBMSmokeParticle;
import com.hbm.particle.type.ParticleRocketFlame;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//注册所有的粒子类型
public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HBM.MODID);

    public static final RegistryObject<SimpleParticleType> HBM_SMOKE = PARTICLE_TYPES.register("nuke_smoke",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> ROCKET_FLAME = PARTICLE_TYPES.register("missile_contrail",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RADIATION_FOG = PARTICLE_TYPES.register("fog",() -> new SimpleParticleType(false));

    public static void register(RegisterParticleProvidersEvent event){
        //注册模组专属粒子效果
        event.registerSpriteSet(ModParticleTypes.HBM_SMOKE.get(), HBMSmokeParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.ROCKET_FLAME.get(), ParticleRocketFlame.Provider::new);
    }
}
