package net.mcreator.nuclearcraft.init;

import net.mcreator.nuclearcraft.entity.AtomicBombEntity;
import net.mcreator.nuclearcraft.entity.AtomicBombExplosionEntity;
import net.mcreator.nuclearcraft.entity.BunkerBusterEntity;
import net.mcreator.nuclearcraft.entity.CakeBombEntity;
import net.mcreator.nuclearcraft.entity.FiveBombEntity;
import net.mcreator.nuclearcraft.entity.FiveHundredKgExplosionEntity;
import net.mcreator.nuclearcraft.entity.FourthOfJullyEntity;
import net.mcreator.nuclearcraft.entity.NapalmBarraageEntity;
import net.mcreator.nuclearcraft.entity.NapalmBombEntity;
import net.mcreator.nuclearcraft.entity.TenKgBombAirstrikesEntity;
import net.mcreator.nuclearcraft.entity.TwoFiddyEntity;
import net.mcreator.nuclearcraft.entity.TwoHundredFiftyKgExplosionEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/init/EntityAnimationFactory.class */
public class EntityAnimationFactory {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (event != null && event.getEntity() != null) {
            TwoFiddyEntity entity = event.getEntity();
            if (entity instanceof TwoFiddyEntity) {
                TwoFiddyEntity syncable = entity;
                String animation = syncable.getSyncedAnimation();
                if (!animation.equals("undefined")) {
                    syncable.setAnimation("undefined");
                    syncable.animationprocedure = animation;
                }
            }
            TwoHundredFiftyKgExplosionEntity entity2 = event.getEntity();
            if (entity2 instanceof TwoHundredFiftyKgExplosionEntity) {
                TwoHundredFiftyKgExplosionEntity syncable2 = entity2;
                String animation2 = syncable2.getSyncedAnimation();
                if (!animation2.equals("undefined")) {
                    syncable2.setAnimation("undefined");
                    syncable2.animationprocedure = animation2;
                }
            }
            TenKgBombAirstrikesEntity entity3 = event.getEntity();
            if (entity3 instanceof TenKgBombAirstrikesEntity) {
                TenKgBombAirstrikesEntity syncable3 = entity3;
                String animation3 = syncable3.getSyncedAnimation();
                if (!animation3.equals("undefined")) {
                    syncable3.setAnimation("undefined");
                    syncable3.animationprocedure = animation3;
                }
            }
            FiveBombEntity entity4 = event.getEntity();
            if (entity4 instanceof FiveBombEntity) {
                FiveBombEntity syncable4 = entity4;
                String animation4 = syncable4.getSyncedAnimation();
                if (!animation4.equals("undefined")) {
                    syncable4.setAnimation("undefined");
                    syncable4.animationprocedure = animation4;
                }
            }
            FiveHundredKgExplosionEntity entity5 = event.getEntity();
            if (entity5 instanceof FiveHundredKgExplosionEntity) {
                FiveHundredKgExplosionEntity syncable5 = entity5;
                String animation5 = syncable5.getSyncedAnimation();
                if (!animation5.equals("undefined")) {
                    syncable5.setAnimation("undefined");
                    syncable5.animationprocedure = animation5;
                }
            }
            NapalmBombEntity entity6 = event.getEntity();
            if (entity6 instanceof NapalmBombEntity) {
                NapalmBombEntity syncable6 = entity6;
                String animation6 = syncable6.getSyncedAnimation();
                if (!animation6.equals("undefined")) {
                    syncable6.setAnimation("undefined");
                    syncable6.animationprocedure = animation6;
                }
            }
            NapalmBarraageEntity entity7 = event.getEntity();
            if (entity7 instanceof NapalmBarraageEntity) {
                NapalmBarraageEntity syncable7 = entity7;
                String animation7 = syncable7.getSyncedAnimation();
                if (!animation7.equals("undefined")) {
                    syncable7.setAnimation("undefined");
                    syncable7.animationprocedure = animation7;
                }
            }
            BunkerBusterEntity entity8 = event.getEntity();
            if (entity8 instanceof BunkerBusterEntity) {
                BunkerBusterEntity syncable8 = entity8;
                String animation8 = syncable8.getSyncedAnimation();
                if (!animation8.equals("undefined")) {
                    syncable8.setAnimation("undefined");
                    syncable8.animationprocedure = animation8;
                }
            }
            AtomicBombEntity entity9 = event.getEntity();
            if (entity9 instanceof AtomicBombEntity) {
                AtomicBombEntity syncable9 = entity9;
                String animation9 = syncable9.getSyncedAnimation();
                if (!animation9.equals("undefined")) {
                    syncable9.setAnimation("undefined");
                    syncable9.animationprocedure = animation9;
                }
            }
            AtomicBombExplosionEntity entity10 = event.getEntity();
            if (entity10 instanceof AtomicBombExplosionEntity) {
                AtomicBombExplosionEntity syncable10 = entity10;
                String animation10 = syncable10.getSyncedAnimation();
                if (!animation10.equals("undefined")) {
                    syncable10.setAnimation("undefined");
                    syncable10.animationprocedure = animation10;
                }
            }
            CakeBombEntity entity11 = event.getEntity();
            if (entity11 instanceof CakeBombEntity) {
                CakeBombEntity syncable11 = entity11;
                String animation11 = syncable11.getSyncedAnimation();
                if (!animation11.equals("undefined")) {
                    syncable11.setAnimation("undefined");
                    syncable11.animationprocedure = animation11;
                }
            }
            FourthOfJullyEntity entity12 = event.getEntity();
            if (entity12 instanceof FourthOfJullyEntity) {
                FourthOfJullyEntity syncable12 = entity12;
                String animation12 = syncable12.getSyncedAnimation();
                if (!animation12.equals("undefined")) {
                    syncable12.setAnimation("undefined");
                    syncable12.animationprocedure = animation12;
                }
            }
        }
    }
}
