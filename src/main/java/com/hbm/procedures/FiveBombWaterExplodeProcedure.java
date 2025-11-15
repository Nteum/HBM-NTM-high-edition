package com.hbm.procedures;

import com.hbm.compat.bigexplosives.BigExplosivesMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles underwater detonation logic for the Five Bomb.
 */
public final class FiveBombWaterExplodeProcedure {

    private static final int EXPLOSION_DELAY = 5;

    private FiveBombWaterExplodeProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null || (!entity.isInWaterRainOrBubble() && !entity.isInWater())) {
            return;
        }

        entity.hurt(entity.damageSources().drown(), 100.0F);
        playUnderwaterBlastSound(world, x, y, z);

        BigExplosivesMod.queueServerWork(EXPLOSION_DELAY, () -> {
            if (world instanceof Level level && !level.isClientSide()) {
                level.explode(null, x, y, z, 6.0F, Level.ExplosionInteraction.TNT);
            }
        });
    }

    private static void playUnderwaterBlastSound(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof Level level)) {
            return;
        }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:underwaterexplode"));
        if (sound == null) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        if (!level.isClientSide()) {
            level.playSound(null, pos, sound, SoundSource.NEUTRAL, 200.0F, 1.0F);
        } else {
            level.playLocalSound(x, y, z, sound, SoundSource.NEUTRAL, 200.0F, 1.0F, false);
        }
    }
}
