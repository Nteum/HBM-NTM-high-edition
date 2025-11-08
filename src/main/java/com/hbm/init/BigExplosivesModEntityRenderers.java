package com.hbm.init;

import com.hbm.render.AtomicBombExplosionRenderer;
import com.hbm.render.AtomicBombRenderer;
import com.hbm.render.FiveBombRenderer;
import com.hbm.render.FiveHundredKgExplosionRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class BigExplosivesModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.FIVE_BOMB.get(), FiveBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.FIVE_HUNDRED_KG_EXPLOSION.get(), FiveHundredKgExplosionRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.ATOMIC_BOMB.get(), AtomicBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.ATOMIC_BOMB_EXPLOSION.get(), AtomicBombExplosionRenderer::new);
    }
}
