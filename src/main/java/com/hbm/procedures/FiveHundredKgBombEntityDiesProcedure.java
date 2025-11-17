package com.hbm.procedures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handles the sound and shrapnel burst when the 500kg bomb dies.
 */
public final class FiveHundredKgBombEntityDiesProcedure {

    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "bigexplosivesconfig.json";
    private static final double DEFAULT_CLUSTER_RADIUS = 6.0D;
    private static final int DEFAULT_CLUSTER_COUNT = 8;
    private static final float DEFAULT_BLAST_POWER = 12.0F;

    private FiveHundredKgBombEntityDiesProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z) {
        JsonObject config = readConfig();
        double spawnRadius = sanitized(config, "WidthOfThe500kgExplosionSpawnRadius", DEFAULT_CLUSTER_RADIUS);
        int explosionCount = sanitizedInt(config, "AmountOfExplosions500kgSpawns", DEFAULT_CLUSTER_COUNT);
        float blastPower = (float) sanitized(config, "FiveHundredKgExplosionPower", DEFAULT_BLAST_POWER);

        if (world instanceof Level level) {
            playBlastSound(level, x, y, z);
            if (!level.isClientSide()) {
                spawnCentralExplosion(level, x, y, z, blastPower);
                spawnRandomExplosions(level, x, y, z, spawnRadius, explosionCount);
            }
        }
    }

    private static double sanitized(JsonObject config, String key, double fallback) {
        if (config.has(key)) {
            try {
                double value = config.get(key).getAsDouble();
                return value > 0.0D ? value : fallback;
            } catch (ClassCastException | IllegalStateException ex) {
                return fallback;
            }
        }
        return fallback;
    }

    private static int sanitizedInt(JsonObject config, String key, int fallback) {
        if (config.has(key)) {
            try {
                int value = config.get(key).getAsInt();
                return value > 0 ? value : fallback;
            } catch (ClassCastException | IllegalStateException ex) {
                return fallback;
            }
        }
        return fallback;
    }

    private static void playBlastSound(Level level, double x, double y, double z) {
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("big_explosives:big_bomb"));
        if (sound == null) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        if (!level.isClientSide()) {
            level.playSound(null, pos, sound, SoundSource.MASTER, 200.0F, 1.0F);
        } else {
            level.playLocalSound(x, y, z, sound, SoundSource.MASTER, 200.0F, 1.0F, false);
        }
    }

    private static void spawnRandomExplosions(Level level, double x, double y, double z, double radius, int count) {
        RandomSource random = RandomSource.create();
        int cappedCount = Math.max(0, Math.min(32, count));
        for (int i = 0; i < cappedCount; i++) {
            double offsetX = radius <= 0 ? 0 : Mth.nextDouble(random, -radius, radius);
            double offsetZ = radius <= 0 ? 0 : Mth.nextDouble(random, -radius, radius);
            double offsetY = Mth.nextDouble(random, -7.0D, 5.0D);
            level.explode(null, x + offsetX, y + offsetY, z + offsetZ, 6.0F, Level.ExplosionInteraction.TNT);
        }
    }

    private static void spawnCentralExplosion(Level level, double x, double y, double z, float power) {
        float radius = Math.max(8.0F, Math.min(20.0F, power));
        level.explode(null, x, y, z, radius, Level.ExplosionInteraction.TNT);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 1.0D, z, 2, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y + 0.5D, z, 20, 0.6D, 0.4D, 0.6D, 0.02D);
        }
    }

    private static JsonObject readConfig() {
        Path configDir = FMLPaths.GAMEDIR.get().resolve("config");
        Path configPath = configDir.resolve(CONFIG_FILE);
        if (!Files.isRegularFile(configPath)) {
            return new JsonObject();
        }
        try {
            String raw = Files.readString(configPath);
            return GSON.fromJson(raw, JsonObject.class);
        } catch (IOException | JsonSyntaxException ex) {
            return new JsonObject();
        }
    }
}
