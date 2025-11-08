package com.hbm.procedures;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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

    private FiveHundredKgBombEntityDiesProcedure() {
    }

    public static void execute(LevelAccessor world, double x, double y, double z) {
        JsonObject config = readConfig();
        double spawnRadius = config.has("WidthOfThe500kgExplosionSpawnRadius")
                ? config.get("WidthOfThe500kgExplosionSpawnRadius").getAsDouble()
                : 0.0D;
        int explosionCount = config.has("AmountOfExplosions500kgSpawns")
                ? config.get("AmountOfExplosions500kgSpawns").getAsInt()
                : 0;

        if (world instanceof Level level) {
            playBlastSound(level, x, y, z);
            if (!level.isClientSide()) {
                spawnRandomExplosions(level, x, y, z, spawnRadius, explosionCount);
            }
        }
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
        for (int i = 0; i < count; i++) {
            double offsetX = radius <= 0 ? 0 : Mth.nextDouble(random, -radius, radius);
            double offsetZ = radius <= 0 ? 0 : Mth.nextDouble(random, -radius, radius);
            double offsetY = Mth.nextDouble(random, -7.0D, 5.0D);
            level.explode(null, x + offsetX, y + offsetY, z + offsetZ, 6.0F, Level.ExplosionInteraction.TNT);
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
        } catch (IOException ex) {
            return new JsonObject();
        }
    }
}
