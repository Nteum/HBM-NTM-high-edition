package com.hbm.space.dim.trait;

import java.util.Random;

import com.hbm.registries.RegistryHelper;
import com.hbm.space.dim.CelestialBody;
import com.hbm.space.dim.SolarSystemWorldSavedData;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Weather trait for celestial bodies.
 * Manages rain, thunder, and lightning mechanics based on
 * atmospheric pressure and water availability.
 *
 * Lightning is categorized into:
 * - Cloud lightning (pressure 0.5–3.0 atm)
 * - Haze lightning (pressure 3.0–5.0 atm)
 * - Opaque lightning (pressure >5.0 atm)
 *
 * Lightning cannot spawn if no biome on the body supports it.
 */
public class CBT_Weather extends CelestialBodyTrait {

    private static final RandomSource WEATHER_RANDOM = RandomSource.create();
    private static final int SAVE_INTERVAL = 200;
    private static final float LIGHTNING_CLOUD_PRESSURE = 0.5F;
    private static final float LIGHTNING_HAZE_PRESSURE = 3.0F;
    private static final float LIGHTNING_OPAQUE_PRESSURE = 5.0F;
    private static final float CLOUD_LIGHTNING_ACTIVITY = 0.55F;
    private static final float HAZE_LIGHTNING_ACTIVITY = 0.82F;
    private static final int[][] LIGHTNING_BIOME_SAMPLES = new int[][] {
        {0, 0}, {512, 0}, {-512, 0}, {0, 512}, {0, -512}
    };

    public boolean raining;
    public boolean thundering;
    public boolean canSpawnLightning = true;
    public int rainTime;
    public int thunderTime;
    public float prevRainStrength;
    public float rainStrength;
    public float prevThunderStrength;
    public float thunderStrength;

    private long lastUpdateTick = Long.MIN_VALUE;

    /** Check if a body can support weather at all */
    public static boolean supportsWeather(CelestialBody body) {
        if (body == null || body.gas != null) {
            return false;
        }
        CBT_Atmosphere atmosphere = body.getTrait(CBT_Atmosphere.class);
        CBT_Water water = body.getTrait(CBT_Water.class);
        return atmosphere != null && atmosphere.getPressure() > 0.5D && water != null && water.fluid != null;
    }

    /** Ensures the body has a CBT_Weather trait, creating one if conditions are met */
    public static CBT_Weather ensureTrait(CelestialBody body) {
        if (body == null) return null;

        CBT_Weather weather = body.getTrait(CBT_Weather.class);
        if (weather == null && supportsWeather(body)) {
            body.modifyTraits(new CBT_Weather());
            weather = body.getTrait(CBT_Weather.class);
        }
        return weather;
    }

    /** Global weather tick — called from server tick handler */
    public static void updateGlobalWeather() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        long tick = server.getTickCount();
        boolean dirty = false;

        for (CelestialBody body : CelestialBody.getAllBodies()) {
            CBT_Weather weather = ensureTrait(body);
            if (weather == null) continue;

            ServerLevel world = body.dimension != null
                ? server.getLevel(body.dimension)
                : null;
            RandomSource random = world != null ? world.random : WEATHER_RANDOM;
            if (weather.updateForTick(tick, random, body)) {
                dirty = true;
            }
        }

        if (dirty) {
            SolarSystemWorldSavedData.get().setDirty();
        }
    }

    private static float getAtmospherePressure(CelestialBody body) {
        if (body == null || body.gas != null) return 0.0F;
        CBT_Atmosphere atmosphere = body.getTrait(CBT_Atmosphere.class);
        return atmosphere != null ? Math.max(0.0F, (float) atmosphere.getPressure()) : 0.0F;
    }

    /** Returns lightning severity (0.0 to 1.0) based on atmospheric pressure */
    public static float getLightningSeverity(CelestialBody body) {
        if (!supportsWeather(body)) return 0.0F;
        float pressure = getAtmospherePressure(body);
        if (pressure > LIGHTNING_HAZE_PRESSURE) {
            float hazeMix = Mth.clamp(
                (pressure - LIGHTNING_HAZE_PRESSURE)
                    / (LIGHTNING_OPAQUE_PRESSURE - LIGHTNING_HAZE_PRESSURE),
                0.0F, 1.0F
            );
            return Mth.clamp(0.65F + hazeMix * 0.35F, 0.65F, 1.0F);
        }
        return Mth.clamp(
            (pressure - LIGHTNING_CLOUD_PRESSURE)
                / (LIGHTNING_HAZE_PRESSURE - LIGHTNING_CLOUD_PRESSURE) * 0.65F,
            0.0F, 0.65F
        );
    }

    /** Lightning activity factor for strike frequency */
    public static float getLightningActivityFactor(CelestialBody body) {
        if (!supportsWeather(body)) return 0.0F;
        float pressure = getAtmospherePressure(body);
        if (pressure > LIGHTNING_HAZE_PRESSURE) {
            float hazeMix = Mth.clamp(
                (pressure - LIGHTNING_HAZE_PRESSURE)
                    / (LIGHTNING_OPAQUE_PRESSURE - LIGHTNING_HAZE_PRESSURE),
                0.0F, 1.0F
            );
            return Mth.clamp(
                HAZE_LIGHTNING_ACTIVITY + hazeMix * (1.0F - HAZE_LIGHTNING_ACTIVITY),
                HAZE_LIGHTNING_ACTIVITY, 1.0F
            );
        }
        float cloudMix = Mth.clamp(
            (pressure - LIGHTNING_CLOUD_PRESSURE)
                / (LIGHTNING_HAZE_PRESSURE - LIGHTNING_CLOUD_PRESSURE),
            0.0F, 1.0F
        );
        return Mth.clamp(
            CLOUD_LIGHTNING_ACTIVITY + cloudMix * (HAZE_LIGHTNING_ACTIVITY - CLOUD_LIGHTNING_ACTIVITY),
            CLOUD_LIGHTNING_ACTIVITY, HAZE_LIGHTNING_ACTIVITY
        );
    }

    private static int getStormDuration(RandomSource rand, float lightningSeverity) {
        int baseDuration = rand.nextInt(12000) + 3600;
        return Math.max(1200, Mth.floor(baseDuration * (0.95F + lightningSeverity * 0.45F)));
    }

    private static int getRainDuration(RandomSource rand) {
        return rand.nextInt(12000) + 12000;
    }

    private static int getClearDuration(RandomSource rand) {
        return rand.nextInt(168000) + 12000;
    }

    private static int getThunderClearDuration(RandomSource rand, float lightningSeverity) {
        int baseDuration = getClearDuration(rand);
        return Math.max(2400, Mth.floor(baseDuration * (1.05F - lightningSeverity * 0.45F)));
    }

    public void forceClear(RandomSource rand, int duration) {
        raining = false;
        thundering = false;
        rainTime = Math.max(1, duration);
        thunderTime = getClearDuration(rand);
        prevRainStrength = 0.0F;
        rainStrength = 0.0F;
        prevThunderStrength = 0.0F;
        thunderStrength = 0.0F;
    }

    public void forceRain(RandomSource rand, int duration) {
        raining = true;
        thundering = false;
        rainTime = Math.max(1, duration);
        thunderTime = getClearDuration(rand);
    }

    public void forceThunder(int duration) {
        raining = true;
        thundering = true;
        rainTime = Math.max(1, duration);
        thunderTime = Math.max(1, duration);
    }

    private static boolean sampleCanSpawnLightning(ServerLevel world) {
        if (world == null) return true;
        for (int[] sample : LIGHTNING_BIOME_SAMPLES) {
            int height = world.getHeight(Heightmap.Types.WORLD_SURFACE, sample[0], sample[1]);
            if (RegistryHelper.worldCanSpawnLightingBolt(world, new BlockPos(sample[0], height, sample[1]))) {
                return true;
            }
        }
        return false;
    }

    public boolean updateForTick(long tick, RandomSource rand, CelestialBody body) {
        if (lastUpdateTick == tick) return false;
        lastUpdateTick = tick;

        if (!supportsWeather(body)) {
            boolean hadWeather = rainTime != 0
                || thunderTime != 0
                || canSpawnLightning
                || raining
                || thundering
                || prevRainStrength > 0.0F
                || rainStrength > 0.0F
                || prevThunderStrength > 0.0F
                || thunderStrength > 0.0F;

            rainTime = 0;
            thunderTime = 0;
            canSpawnLightning = false;
            raining = false;
            thundering = false;
            prevRainStrength = 0.0F;
            rainStrength = 0.0F;
            prevThunderStrength = 0.0F;
            thunderStrength = 0.0F;
            return hadWeather;
        }

        boolean stateChanged = false;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel world = server != null ? server.getLevel(body.dimension) : null;

        float lightningSeverity = getLightningSeverity(body);
        boolean lightningAllowed = sampleCanSpawnLightning(world);
        if (canSpawnLightning != lightningAllowed) {
            canSpawnLightning = lightningAllowed;
            stateChanged = true;
        }

        if (!canSpawnLightning && thundering) {
            thundering = false;
            thunderTime = getThunderClearDuration(rand, lightningSeverity);
            stateChanged = true;
        }

        if (thunderTime <= 0) {
            thunderTime = thundering
                ? getStormDuration(rand, lightningSeverity)
                : getThunderClearDuration(rand, lightningSeverity);
            stateChanged = true;
        } else {
            thunderTime--;
            if (thunderTime <= 0) {
                thundering = canSpawnLightning && !thundering;
                thunderTime = thundering
                    ? getStormDuration(rand, lightningSeverity)
                    : getThunderClearDuration(rand, lightningSeverity);
                stateChanged = true;
            }
        }

        prevThunderStrength = thunderStrength;
        thunderStrength = Mth.clamp(
            thunderStrength + (thundering ? 0.01F : -0.01F),
            0.0F, 1.0F
        );

        if (rainTime <= 0) {
            rainTime = raining ? getRainDuration(rand) : getClearDuration(rand);
            stateChanged = true;
        } else {
            rainTime--;
            if (rainTime <= 0) {
                raining = !raining;
                if (!raining) {
                    thundering = false;
                    thunderTime = getThunderClearDuration(rand, lightningSeverity);
                }
                rainTime = raining ? getRainDuration(rand) : getClearDuration(rand);
                stateChanged = true;
            }
        }

        prevRainStrength = rainStrength;
        rainStrength = Mth.clamp(
            rainStrength + (raining ? 0.01F : -0.01F),
            0.0F, 1.0F
        );

        return stateChanged || Math.floorMod(body.dimension != null
            ? body.dimension.location().hashCode()
            : 0 + (int) tick, SAVE_INTERVAL) == 0;
    }

    public float getRainStrength(float partialTicks) {
        return Mth.clamp(prevRainStrength + (rainStrength - prevRainStrength) * partialTicks, 0.0F, 1.0F);
    }

    public float getThunderStrength(float partialTicks) {
        return Mth.clamp(prevThunderStrength + (thunderStrength - prevThunderStrength) * partialTicks, 0.0F, 1.0F);
    }

    public float getWeightedThunderStrength(float partialTicks) {
        return getRainStrength(partialTicks) * getThunderStrength(partialTicks);
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putBoolean("raining", raining);
        nbt.putBoolean("thundering", thundering);
        nbt.putBoolean("canSpawnLightning", canSpawnLightning);
        nbt.putInt("rainTime", rainTime);
        nbt.putInt("thunderTime", thunderTime);
        nbt.putFloat("prevRainStrength", prevRainStrength);
        nbt.putFloat("rainStrength", rainStrength);
        nbt.putFloat("prevThunderStrength", prevThunderStrength);
        nbt.putFloat("thunderStrength", thunderStrength);
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        raining = nbt.getBoolean("raining");
        thundering = nbt.getBoolean("thundering");
        canSpawnLightning = !nbt.contains("canSpawnLightning") || nbt.getBoolean("canSpawnLightning");
        rainTime = nbt.getInt("rainTime");
        thunderTime = nbt.getInt("thunderTime");
        prevRainStrength = nbt.getFloat("prevRainStrength");
        rainStrength = nbt.getFloat("rainStrength");
        prevThunderStrength = nbt.getFloat("prevThunderStrength");
        thunderStrength = nbt.getFloat("thunderStrength");
    }

    @Override
    public void writeToBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(raining);
        buf.writeBoolean(thundering);
        buf.writeBoolean(canSpawnLightning);
        buf.writeInt(rainTime);
        buf.writeInt(thunderTime);
        buf.writeFloat(prevRainStrength);
        buf.writeFloat(rainStrength);
        buf.writeFloat(prevThunderStrength);
        buf.writeFloat(thunderStrength);
    }

    @Override
    public void readFromBytes(FriendlyByteBuf buf) {
        raining = buf.readBoolean();
        thundering = buf.readBoolean();
        canSpawnLightning = buf.readBoolean();
        rainTime = buf.readInt();
        thunderTime = buf.readInt();
        prevRainStrength = buf.readFloat();
        rainStrength = buf.readFloat();
        prevThunderStrength = buf.readFloat();
        thunderStrength = buf.readFloat();
    }
}
