package com.hbm.init;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BigExplosivesModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BigExplosivesMod.MODID);
    public static final RegistryObject<SimpleParticleType> SMOKE = REGISTRY.register("smoke", () -> {
        return new SimpleParticleType(false);
    });
}
