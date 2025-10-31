package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.client.renderer.AtomicBombExplosionRenderer;
import net.mcreator.nuclearcraft.client.renderer.AtomicBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.BunkerBusterRenderer;
import net.mcreator.nuclearcraft.client.renderer.CakeBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.FiveBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.FiveHundredKgExplosionRenderer;
import net.mcreator.nuclearcraft.client.renderer.FourthOfJullyRenderer;
import net.mcreator.nuclearcraft.client.renderer.NapalmBarraageRenderer;
import net.mcreator.nuclearcraft.client.renderer.NapalmBombRenderer;
import net.mcreator.nuclearcraft.client.renderer.TenKgBombAirstrikesRenderer;
import net.mcreator.nuclearcraft.client.renderer.TwoFiddyRenderer;
import net.mcreator.nuclearcraft.client.renderer.TwoHundredFiftyKgExplosionRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/BigExplosivesModEntityRenderers.class */
public class BigExplosivesModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.TWO_FIDDY.get(), TwoFiddyRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.TWO_HUNDRED_FIFTY_KG_EXPLOSION.get(), TwoHundredFiftyKgExplosionRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.TEN_KG_BOMB_AIRSTRIKES.get(), TenKgBombAirstrikesRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.FIVE_BOMB.get(), FiveBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.FIVE_HUNDRED_KG_EXPLOSION.get(), FiveHundredKgExplosionRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.NAPALM_BOMB.get(), NapalmBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.NAPALM_BARRAAGE.get(), NapalmBarraageRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.BUNKER_BUSTER.get(), BunkerBusterRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.ATOMIC_BOMB.get(), AtomicBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.ATOMIC_BOMB_EXPLOSION.get(), AtomicBombExplosionRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.CAKE_BOMB.get(), CakeBombRenderer::new);
        event.registerEntityRenderer((EntityType) BigExplosivesModEntities.FOURTH_OF_JULLY.get(), FourthOfJullyRenderer::new);
    }
}
