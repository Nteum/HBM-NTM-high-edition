package net.mcreator.nuclearcraft.procedures;

import javax.annotation.Nullable;
import net.mcreator.nuclearcraft.init.BigExplosivesModMobEffects;
import net.mcreator.nuclearcraft.network.BigExplosivesModVariables;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
/* loaded from: explosives_beta.jar:net/mcreator/nuclearcraft/procedures/SetFadeProcedure.class */
public class SetFadeProcedure {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player);
        }
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null) {
            return;
        }
        if (entity instanceof LivingEntity) {
            LivingEntity _livEnt0 = (LivingEntity) entity;
            if (_livEnt0.m_21023_((MobEffect) BigExplosivesModMobEffects.WHITE_OUT_EFFECT.get())) {
                double _setval = Math.min(((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).fadevariable + 0.1d, 1.0d);
                entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).ifPresent(capability -> {
                    capability.fadevariable = _setval;
                    capability.syncPlayerVariables(entity);
                });
                return;
            }
        }
        double _setval2 = Math.max(((BigExplosivesModVariables.PlayerVariables) entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).orElse(new BigExplosivesModVariables.PlayerVariables())).fadevariable - 0.1d, 0.0d);
        entity.getCapability(BigExplosivesModVariables.PLAYER_VARIABLES_CAPABILITY, (Direction) null).ifPresent(capability2 -> {
            capability2.fadevariable = _setval2;
            capability2.syncPlayerVariables(entity);
        });
    }
}
