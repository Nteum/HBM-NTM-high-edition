package com.hbm.particle;

import com.hbm.main.HBMxx;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//注册所有的粒子类型
public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HBMxx.MODID);

    public static final RegistryObject<SimpleParticleType> HBM_SMOKE = PARTICLE_TYPES.register("hbm_smoke",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> CONTRAIL = PARTICLE_TYPES.register("contrail",() -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RADIATION_FOG = PARTICLE_TYPES.register("radiation_fog",() -> new SimpleParticleType(false));
}
