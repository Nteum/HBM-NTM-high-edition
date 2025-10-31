package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.client.particle.SmokeParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModParticles.class */
public class BigExplosivesModParticles {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet((ParticleType) BigExplosivesModParticleTypes.SMOKE.get(), SmokeParticle::provider);
    }
}
