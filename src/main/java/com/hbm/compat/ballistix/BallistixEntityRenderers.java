package com.hbm.compat.ballistix;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BallistixEntityRenderers {

    private BallistixEntityRenderers() {
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BallistixEntities.PRIMED_EXPLOSIVE.get(), BallistixPrimedExplosiveRenderer::new);
    }
}
