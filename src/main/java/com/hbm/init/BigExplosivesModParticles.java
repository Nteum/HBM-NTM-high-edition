package com.hbm.init;

import com.hbm.particle.type.HBMSmokeParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})

public class BigExplosivesModParticles {
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(BigExplosivesModParticleTypes.SMOKE.get(),
                spriteSet -> new HBMSmokeParticle.Provider(spriteSet));
    }
}
