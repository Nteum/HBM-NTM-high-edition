package com.hbm.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

/**
 * Kills the entity if it ever touches liquids or other materials that would
 * normally neutralise the bomb. The original generated code referenced
 * obfuscated helpers; this uses the stable Mojang-named API.
 */
public final class AtomicBombOnEntityTickUpdateProcedure {

    private static final float DAMAGE = 100.0F;

    private AtomicBombOnEntityTickUpdateProcedure() {
    }

    public static void execute(LevelAccessor world, Entity entity) {
        if (world == null || entity == null) {
            return;
        }

        if (entity.isInWaterRainOrBubble()) {
            entity.hurt(entity.damageSources().drown(), DAMAGE);
        }
        if (entity.isInLava()) {
            entity.hurt(entity.damageSources().lava(), DAMAGE);
        }
        if (entity.isInWaterOrRain()) {
            entity.hurt(entity.damageSources().generic(), DAMAGE);
        }
    }
}
