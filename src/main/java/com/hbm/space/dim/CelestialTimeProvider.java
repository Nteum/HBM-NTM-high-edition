package com.hbm.space.dim;

import com.hbm.handler.ImpactWorldHandler;
import com.hbm.space.util.AstronomyUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * Centralised time calculations for celestial dimensions.
 *
 * Extracted from 1.7.10 WorldProviderCelestial:
 * - Day length derived from rotational/orbital period
 * - Solar angle (celestial angle) custom curve
 * - Moon phase based on closest satellite
 * - Per-world time stored in CelestialBodyWorldSavedData
 *
 * All methods are static; no instance state required.
 */
public class CelestialTimeProvider {

    /**
     * Get the real local time for the dimension this level belongs to.
     * For the overworld (kerbin) this delegates to vanilla; for other bodies
     * it reads from CelestialBodyWorldSavedData.
     */
    public static long getWorldTime(Level level) {
        if (level.dimension().equals(Level.OVERWORLD)) {
            return level.getDayTime();
        }

        if (level instanceof ServerLevel serverLevel) {
            return CelestialBodyWorldSavedData.get(serverLevel).getLocalTime();
        }

        // Client: the value is synced via network — return what we last cached
        // (stored on the level's dayTime field set by the sync packet)
        return level.getDayTime();
    }

    /**
     * Set the local time for a dimension.
     */
    public static void setWorldTime(Level level, long time) {
        if (level.dimension().equals(Level.OVERWORLD)) {
            return; // vanilla handles overworld time
        }

        if (level instanceof ServerLevel serverLevel) {
            CelestialBodyWorldSavedData.get(serverLevel).setLocalTime(time);
        }
    }

    /**
     * Calculate the day length (in ticks) for the given body.
     *
     * From 1.7.10: dayLength = rotationalPeriod / (1 - 1/orbitalPeriod)
     * This accounts for the sidereal-vs-solar day difference.
     */
    public static double getDayLength(CelestialBody body) {
        return body.getRotationalPeriod() / (1.0 - (1.0 / body.getPlanet().getOrbitalPeriod()));
    }

    /**
     * Solar angle calculation — a custom curve that compresses the angle near
     * the horizon for longer dawn/dusk periods.
     *
     * This replaces WorldProvider.calculateCelestialAngle() for celestial bodies.
     */
    public static float calculateCelestialAngle(Level level, float partialTicks) {
        CelestialBody body = CelestialBody.getBody(level);

        long worldTime = getWorldTime(level);
        double dayLength = getDayLength(body);

        double frac = (worldTime % (long) dayLength) + partialTicks;
        double f1 = (frac / dayLength) - 0.25;

        if (f1 < 0.0) f1 += 1.0;
        if (f1 > 1.0) f1 -= 1.0;

        double f2 = f1;
        f1 = 0.5 - Math.cos(f1 * Math.PI) / 2.0;

        return (float) (f2 + (f1 - f2) / 3.0);
    }

    /**
     * Moon phase derived from the angular position of the closest moon.
     * If the body has no satellites, defaults to phase 2 (half moon difficulty).
     */
    public static int getMoonPhase(Level level, long worldTime) {
        CelestialBody body = CelestialBody.getBody(level);

        if (body.satellites.isEmpty()) return 2;

        double angle = SolarSystem.calculateSingleAngle(level, body, body.satellites.get(0));
        int phase = Math.round(8 - ((float) angle / 45 + 4));
        if (phase >= 8) return 0;
        return phase;
    }

    // === Sun power (cached per tick) ===

    private static long lastEclipseTick = -1;
    private static double cachedEclipse = -1;

    /**
     * Server-side: compute the eclipse factor for the current dimension.
     * Result is cached per tick.
     */
    public static double getEclipseAmount(Level level) {
        long tick = level.getGameTime();
        if (tick == lastEclipseTick) return cachedEclipse;

        CelestialBody body = CelestialBody.getBody(level);
        double sunSize = SolarSystem.calculateSunSize(body);
        float solarAngle = level.getSunAngle(0);

        var metrics = SolarSystem.calculateMetricsFromBody(level, 0, body, solarAngle);
        cachedEclipse = getEclipseFactor(metrics, sunSize, SolarSystem.MAX_APPARENT_SIZE_SURFACE);
        lastEclipseTick = tick;

        return cachedEclipse;
    }

    /**
     * Calculate how much of the sun is eclipsed by occluding bodies.
     * Factor 0 = no eclipse, 1 = total eclipse.
     */
    public static double getEclipseFactor(
            java.util.List<SolarSystem.AstroMetric> metrics,
            double sunSize, double maxSize) {
        double factor = 0;
        double sunArc = getArc(sunSize);

        for (SolarSystem.AstroMetric metric : metrics) {
            if (metric.apparentSize < 1) continue;

            double planetArc = getArc(Math.min(metric.apparentSize, maxSize));
            double minPhase = 1 - (planetArc + sunArc);
            double maxPhase = 1 - (planetArc - sunArc);
            if (metric.phaseObscure < minPhase) continue;

            factor = Math.max(factor,
                Math.min(1 - (metric.phaseObscure - maxPhase) / (minPhase - maxPhase), 1.0));
        }

        return factor;
    }

    /** Apparent angular radius from apparent size (non-linear due to quad rendering). */
    private static double getArc(double apparentSize) {
        return apparentSize * 0.0017 + Math.sqrt(apparentSize * 0.00003);
    }

    /** Whether any eclipse is occurring at all. */
    public static boolean isEclipse(Level level) {
        return getEclipseAmount(level) > 0.0;
    }

    // === Sun brightness factor ===

    /**
     * Compute sun brightness for the current dimension, accounting for
     * eclipse, dust, and atmospheric absorption.
     */
    public static float getSunBrightness(Level level, float partialTicks) {
        CelestialBody body = CelestialBody.getBody(level);

        // Destroyed star → no sunlight
        if (CelestialBody.getStar(level).hasTrait(
                com.hbm.space.dim.trait.CBT_Destroyed.class))
            return 0;

        // Sun brightness from solar angle (vanilla curve approximation):
        // sunAngle < PI = daytime, brightness = cos curve
        float sunAngle = level.getSunAngle(partialTicks);
        float sunBrightness;
        if (sunAngle < (float) Math.PI) {
            sunBrightness = Mth.cos(sunAngle);
        } else {
            sunBrightness = 0.3F; // night ambient
        }
        if (sunBrightness < 0) sunBrightness = 0;

        sunBrightness *= 1 - (float) getEclipseAmount(level) * 0.6f;

        // Impact dust reduces sunlight
        sunBrightness *= (1 - ImpactWorldHandler.getDustForClient(level));

        // Atmospheric absorption
        var atmosphere = body.getTrait(com.hbm.space.dim.trait.CBT_Atmosphere.class);
        if (atmosphere != null) {
            sunBrightness *= Math.max(0.25f,
                1.0f - ((float) atmosphere.getPressure() - 1.5f) * 0.2f);
        }

        return Math.max(sunBrightness, 0);
    }

    /**
     * Star brightness: hidden during the day, visible at night.
     * At large orbital distances stars become visible during the day too
     * (beyond Duna orbit ~20e6 km, fully visible beyond Jool ~80e6 km).
     */
    public static float getStarBrightness(Level level, float partialTicks) {
        float distanceStart = 20_000_000;
        float distanceEnd   = 80_000_000;

        float semiMajorAxisKm = CelestialBody.getPlanet(level).semiMajorAxisKm;
        float distanceFactor = Mth.clamp(
            (semiMajorAxisKm - distanceStart) / (distanceEnd - distanceStart), 0, 1);

        // Vanilla-style curve: 1 - sunBrightness (night = 1, noon = 0)
        float starBrightness = 1.0f - getSunBrightness(level, partialTicks);

        // Blocked by rain, thunder and impact dust
        starBrightness *= 1.0f - level.getRainLevel(partialTicks);
        starBrightness *= 1.0f - level.getThunderLevel(partialTicks);
        starBrightness *= 1 - ImpactWorldHandler.getDustForClient(level);
        starBrightness = Mth.clamp(starBrightness, 0.0f, 1.0f);

        return Math.max(starBrightness, distanceFactor);
    }

    // === Reset on sleep ===

    /**
     * Called when all players have slept — resets weather and sets time to
     * local morning.
     */
    public static void onAllPlayersSlept(Level level) {
        CelestialBody body = CelestialBody.getBody(level);
        var weather = com.hbm.space.dim.trait.CBT_Weather.ensureTrait(body);
        if (weather != null) {
            weather.forceClear(
                level.random,
                level.random.nextInt(168000) + 12000);
            SolarSystemWorldSavedData.get(level).setDirty();
        }

        if (level.dimension().equals(Level.OVERWORLD)) return;
        if (!level.getGameRules().getBoolean(
                net.minecraft.world.level.GameRules.RULE_DAYLIGHT)) return;

        long dayLength = (long) getDayLength(body);
        long time = getWorldTime(level);
        setWorldTime(level, time - (time % dayLength));
    }
}
