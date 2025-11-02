package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.client.renderer.AtomicBombExplosionRenderer;
import net.mcreator.nuclearcraft.client.renderer.AtomicBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.FiveBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.FiveHundredKgExplosionRenderer;
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
