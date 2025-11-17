package com.hbm.procedures;

import com.hbm.init.BigExplosivesModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Lightweight helper that spawns the legacy 500kg thermobaric visual along
 * with its signature blast sound. Machines that reuse the conventional bomb
 * payload can call this instead of the atomic flash pipeline.
 */
public final class ThermobaricExplosionHelper {

    private ThermobaricExplosionHelper() {
    }

    public static void triggerEffects(Level level, Vec3 center) {
        if (level == null) {
            return;
        }
        playBlastSound(level, center);
        if (!(level instanceof ServerLevel serverLevel) || level.isClientSide()) {
            return;
        }
        Entity explosion = BigExplosivesModEntities.FIVE_HUNDRED_KG_EXPLOSION.get().create(serverLevel);
        if (explosion == null) {
            return;
        }
        explosion.moveTo(center.x, center.y, center.z, 0.0F, 0.0F);
        explosion.setDeltaMovement(Vec3.ZERO);
        serverLevel.addFreshEntity(explosion);
    }

    private static void playBlastSound(Level level, Vec3 center) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:big_bomb"));
        if (sound == null) {
            return;
        }
        BlockPos pos = BlockPos.containing(center);
        if (!level.isClientSide()) {
            level.playSound(null, pos, sound, SoundSource.MASTER, 200.0F, 1.0F);
        } else {
            level.playLocalSound(center.x, center.y, center.z, sound, SoundSource.MASTER, 200.0F, 1.0F, false);
        }
    }
}
