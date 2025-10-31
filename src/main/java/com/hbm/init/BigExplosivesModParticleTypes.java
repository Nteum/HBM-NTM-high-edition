package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.BigExplosivesMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModParticleTypes.class */
public class BigExplosivesModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BigExplosivesMod.MODID);
    public static final RegistryObject<SimpleParticleType> SMOKE = REGISTRY.register("smoke", () -> {
        return new SimpleParticleType(false);
    });
}
