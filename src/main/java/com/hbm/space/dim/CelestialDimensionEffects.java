package com.hbm.space.dim;

import com.hbm.core.contents.fluid.HbmFluidType;
import com.hbm.space.dim.trait.CBT_Atmosphere;
import com.hbm.space.dim.trait.CBT_Atmosphere.FluidEntry;
import com.hbm.space.dim.trait.CBT_War;
import com.hbm.space.dim.trait.CBT_Water;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

/**
 * Dimension rendering effects for celestial bodies.
 *
 * Replaces 1.7.10 WorldProviderCelestial for:
 * - Fog color (atmosphere-dependent, multi-fluid blended)
 * - Sunrise/sunset colours (atmosphere-specific swizzling)
 * - Cloud height (pressure-dependent)
 * - Horizon level
 *
 * Sky rendering is delegated to {@link CelestialSkyRenderer}.
 *
 * Ported from 1.7.10 WorldProviderCelestial to 1.20.1 DimensionSpecialEffects.
 */
public class CelestialDimensionEffects extends DimensionSpecialEffects {

    public CelestialDimensionEffects() {
        // fogDistance=NaN (we handle fog in getBrightnessDependentFogColor)
        // hasCustomSky=true, skyType=NORMAL — must NOT be NONE, otherwise
        // LevelRenderer skips the whole sky pass including renderSky()
        // forceBrightLightmap=false (no forced fullbright)
        // constantAmbientLight=false — TRUE would freeze ambient light at full
        // daylight like the nether/end, making the surface look like night vision
        super(Float.NaN, true, SkyType.NORMAL, false, false);
    }

    // ========================================================================
    // Fog
    // ========================================================================

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 skyColor, float brightness) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return Vec3.ZERO;

        CelestialBody body = CelestialBody.getBody(level);
        CBT_Atmosphere atmosphere = body.getTrait(CBT_Atmosphere.class);

        // Vacuum — black fog
        if (atmosphere == null) return Vec3.ZERO;

        float sun = Mth.clamp(
            Mth.cos(level.getSunAngle(0) * (float) Math.PI * 2.0F) * 2.0F + 0.5F,
            0.0F, 1.0F);

        double r = 0, g = 0, b = 0;
        float totalPressure = (float) atmosphere.getPressure();

        // Blend fog colour by partial pressure of each fluid component
        for (FluidEntry entry : atmosphere.fluids) {
            Vec3 fluidColor = getFluidFogColor(entry, sun);
            float fraction = (float) entry.pressure / totalPressure;
            r += fluidColor.x * fraction;
            g += fluidColor.y * fraction;
            b += fluidColor.z * fraction;
        }

        // Night-time ambient glow
        float nightGlow = Mth.clamp(totalPressure, 0.0F, 1.0F);
        r += 0.06 * nightGlow;
        g += 0.06 * nightGlow;
        b += 0.09 * nightGlow;

        // Thin-atmosphere intensity scaling
        float pressureFactor = Mth.clamp(totalPressure * 10.0F, 0.0F, 1.0F);
        r *= pressureFactor;
        g *= pressureFactor;
        b *= pressureFactor;

        // Above 600m, fade to black
        var camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null && camera.getY() > 600) {
            double curvature = Mth.clamp((1000.0F - (float) camera.getY()) / 400.0F, 0.0F, 1.0F);
            r *= curvature;
            g *= curvature;
            b *= curvature;
        }

        // Eclipse darkening
        double eclipse = CelestialTimeProvider.getEclipseAmount(level);
        if (eclipse > 0) {
            r *= 1 - eclipse * 0.3;
            g *= 1 - eclipse * 0.3;
            b *= 1 - eclipse * 0.3;
        }

        // Impact dust/fire reddening
        float dust = com.hbm.handler.ImpactWorldHandler.getDustForClient(level);
        float fire = com.hbm.handler.ImpactWorldHandler.getFireForClient(level);
        r *= 1 - dust;
        g *= 1 - (dust * 0.5f);
        b *= 1 - dust;
        if (fire > 0) {
            r *= Math.max(1 - dust * 2, 0);
            g *= Math.max(1 - dust * 2, 0);
            b *= Math.max(1 - dust * 2, 0);
        }

        return new Vec3(r, g, b);
    }

    /** Map a fluid entry to its base fog colour. */
    private static Vec3 getFluidFogColor(FluidEntry entry, float sun) {
        float r, g, b;
        // Registry-path-based matching — avoids hardcoding HBMFluids constants
        String name = BuiltInRegistries.FLUID.getKey(entry.fluid).getPath();
        HbmFluidType type = (HbmFluidType) entry.fluid.getFluidType();

        if (name.contains("eve") || name.contains("eveair")) {
            r = 53F / 255F; g = 32F / 255F; b = 74F / 255F;
        } else if (name.contains("duna") || name.contains("carbondioxide")) {
            r = 212F / 255F; g = 112F / 255F; b = 78F / 255F;
        } else if (name.contains("earth") || name.contains("oxygen") || name.contains("nitrogen")) {
            r = 0.7529F; g = 0.8471F; b = 1.0F;
        } else {
            // Generic: extract RGB from fluid colour hex
            int hex = type.getHbmColor();
            r = ((hex >> 16) & 0xFF) / 255.0F;
            g = ((hex >> 8)  & 0xFF) / 255.0F;
            b = (hex & 0xFF) / 255.0F;
            r *= 1.4F; g *= 1.4F; b *= 1.4F;
        }

        r *= sun; g *= sun; b *= sun;
        return new Vec3(r, g, b);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        // For celestial dimensions, fog is handled per-pixel by the shader
        // At ground level with atmosphere → foggy; in vacuum → clear
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return false;
        return CelestialBody.getTrait(level, CBT_Atmosphere.class) != null;
    }

    // ========================================================================
    // Sunrise / Sunset
    // ========================================================================

    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return null;
        return getSunriseColorStatic(timeOfDay, partialTicks, level);
    }

    /** Static variant for use by renderers that already hold a Level reference. */
    public static float[] getSunriseColorStatic(float timeOfDay, float partialTicks,
            ClientLevel level) {
        CBT_Atmosphere atmosphere = CelestialBody.getTrait(level, CBT_Atmosphere.class);
        if (atmosphere == null || atmosphere.getPressure() < 0.05F) return null;

        // Vanilla overworld sunrise curve (from OverworldDimensionSpecialEffects)
        float[] colors = null;
        float f = Mth.cos(timeOfDay * (float) Math.PI * 2.0F) - 0.0F;
        if (f >= -0.4F && f <= 0.4F) {
            float g = f / 0.4F * 0.5F + 0.5F;
            float h = 1.0F - (1.0F - Mth.sin(g * (float) Math.PI)) * 0.99F;
            h *= h;
            colors = new float[] {
                g * 0.3F + 0.7F,      // red
                g * g * 0.7F + 0.2F,  // green
                g * g * 0.0F + 0.2F,  // blue
                h                      // alpha
            };
        }
        if (colors == null) return null;

        // Per-atmosphere colour swizzle
        String mainFluid = BuiltInRegistries.FLUID.getKey(
            atmosphere.getMainFluid()).getPath();
        if (mainFluid.contains("duna") || mainFluid.contains("carbondioxide")) {
            // Mars-like: inverted blue sunset
            float tmp = colors[0]; colors[0] = colors[2]; colors[2] = tmp;
        } else if (mainFluid.contains("tekto") || mainFluid.contains("jool")
                || mainFluid.contains("chlorine")) {
            // Green-to-blue swizzle
            float tmp = colors[1]; colors[1] = colors[2]; colors[2] = tmp;
        }

        float dustFactor = 1 - com.hbm.handler.ImpactWorldHandler.getDustForClient(level);
        colors[0] *= dustFactor; colors[1] *= dustFactor;
        colors[2] *= dustFactor; colors[3] *= dustFactor;

        return colors;
    }

    // ========================================================================
    // Clouds
    // ========================================================================

    @Override
    public float getCloudHeight() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return -99999;

        CBT_Atmosphere atmosphere = CelestialBody.getTrait(level, CBT_Atmosphere.class);
        if (atmosphere == null || atmosphere.getPressure() < 0.5F) return -99999;

        // Vanilla cloud height for atmospheres with pressure ≥ 0.5
        return 192;
    }

    // ========================================================================
    // Horizon
    // ========================================================================

    public double getHorizonLevel(ClientLevel level, double cameraX, double cameraY, double cameraZ) {
        // Overworld uses vanilla; celestial bodies use flat horizon at 63
        if (level.dimension().equals(Level.OVERWORLD)) {
            return level.getLevelData().getHorizonHeight(level);
        }
        return 63;
    }

    // ========================================================================
    // Sky rendering (delegated)
    // ========================================================================

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.Camera camera,
            org.joml.Matrix4f projectionMatrix,
            boolean isFoggy, Runnable setupFog) {

        // Full celestial sky: stars / sun / planets / satellites / rings /
        // meteors / shaders — delegated to the sky renderer.
        CelestialSkyRenderer.renderSky(level, partialTick, poseStack, camera, projectionMatrix);
        return true;
    }

    // ========================================================================
    // Static helpers (reusable from WorldProviderCelestial)
    // ========================================================================

    /**
     * Full sky colour calculation — multi-fluid atmosphere blending with
     * sun angle, eclipse darkening, war flashes and impact dust/fire tinting.
     * Ported from 1.7.10 WorldProviderCelestial.getSkyColor().
     */
    public static Vec3 getSkyColorStatic(ClientLevel level, float partialTick) {
        Vec3 color = Vec3.ZERO;

        CBT_Atmosphere atmosphere = CelestialBody.getTrait(level, CBT_Atmosphere.class);
        if (atmosphere == null) {
            // The cold hard vacuum of space
            return color;
        }

        float sun = CelestialTimeProvider.getSunBrightness(level, partialTick);
        float totalPressure = (float) atmosphere.getPressure();

        // Blend sky colour by partial pressure of each fluid component
        for (FluidEntry entry : atmosphere.fluids) {
            Vec3 fluidColor = getSkyFluidColor(entry, sun, level, partialTick);
            float fraction = (float) entry.pressure / totalPressure;
            color = color.add(fluidColor.scale(fraction));
        }

        // War projectile flashes brighten the sky
        CBT_War war = CelestialBody.getTrait(level, CBT_War.class);
        if (war != null) {
            for (CBT_War.Projectile proj : war.getProjectiles()) {
                float flash = proj.getFlashtime();
                if (proj.getAnimtime() > 0) {
                    float invFlash = 100 - flash;
                    color = color.add(invFlash * 0.5, invFlash * 0.5, invFlash * 0.5);
                }
            }
        }

        // Lower pressure sky renders thinner
        float pressureFactor = Mth.clamp(totalPressure, 0.0F, 1.0F);
        color = color.scale(pressureFactor);

        // Eclipse darkening
        double eclipse = CelestialTimeProvider.getEclipseAmount(level);
        if (eclipse > 0) {
            color = new Vec3(
                color.x * (1 - eclipse * 0.6),
                color.y * (1 - eclipse * 0.6),
                color.z * (1 - eclipse * 0.5));
        }

        // Impact dust/fire tinting
        float dust = com.hbm.handler.ImpactWorldHandler.getDustForClient(level);
        float fire = com.hbm.handler.ImpactWorldHandler.getFireForClient(level);
        if (dust > 0) {
            double r = color.x, g = color.y, b = color.z;
            if (fire > 0) {
                r *= 1.3;
                g *= Math.max(1 - dust * 1.4f, 0);
                b *= Math.max(1 - dust * 4, 0);
            } else {
                g *= 1 - dust * 0.5f;
                b *= Math.max(1 - dust * 4, 0);
            }
            float tint = fire + (1 - dust);
            color = new Vec3(r * tint, g * tint, b * tint);
        }

        return color;
    }

    /** Per-fluid sky colour (slightly different from fog colour mapping). */
    private static Vec3 getSkyFluidColor(FluidEntry entry, float sun,
            ClientLevel level, float partialTick) {
        String name = BuiltInRegistries.FLUID.getKey(entry.fluid).getPath();

        if (name.contains("eveair")) {
            return new Vec3(53/255f * sun, 32/255f * sun, 74/255f * sun);
        } else if (name.contains("dunaair") || name.contains("carbondioxide")) {
            return new Vec3(212/255f * sun, 112/255f * sun, 78/255f * sun);
        } else if (name.contains("earthair") || name.contains("oxygen")
                || name.contains("nitrogen")) {
            // Default to regular ol' overworld sky
            return new Vec3(0.7529f * sun, 0.8471f * sun, 1.0f * sun);
        }

        // Generic: extract RGB from fluid colour
        HbmFluidType type = (HbmFluidType) entry.fluid.getFluidType();
        Vec3 hex = getColorFromHex(type.getHbmColor());
        return new Vec3(hex.x * sun, hex.y * sun, hex.z * sun);
    }

    /** Extract colour from a hex integer into Vec3. */
    public static Vec3 getColorFromHex(int hexColor) {
        float r = ((hexColor >> 16) & 0xFF) / 255.0F;
        float g = ((hexColor >> 8)  & 0xFF) / 255.0F;
        float b = (hexColor & 0xFF) / 255.0F;
        return new Vec3(r, g, b);
    }

    /** How many cloud layers for a given atmospheric pressure. */
    public static int getCloudLayerCount(CBT_Atmosphere atmosphere) {
        if (atmosphere == null || atmosphere.getPressure() < 0.5F) return 0;
        if (atmosphere.getPressure() >= 5.0F) return 3;
        if (atmosphere.getPressure() >= 2.5F) return 2;
        return 1;
    }

    /** Whether weather is supported on this body. */
    public static boolean hasWeatherCycle(Level level) {
        return com.hbm.space.dim.trait.CBT_Weather.supportsWeather(
            CelestialBody.getBody(level));
    }

    /** Weather colour derived from the body's surface liquid. */
    public static Vec3 getWeatherColor(Level level) {
        CBT_Water water = CelestialBody.getTrait(level, CBT_Water.class);
        if (water == null || water.fluid == null) return new Vec3(1, 1, 1);

        Vec3 base = getColorFromHex(
            ((HbmFluidType) water.fluid.getFluidType()).getHbmColor());
        double luminance = base.x * 0.299 + base.y * 0.587 + base.z * 0.114;
        double saturation = 0.35;

        return new Vec3(
            Mth.clamp(luminance + (base.x - luminance) * saturation, 0, 1),
            Mth.clamp(luminance + (base.y - luminance) * saturation, 0, 1),
            Mth.clamp(luminance + (base.z - luminance) * saturation, 0, 1));
    }

    /** Snow colour (same as weather colour for exotic liquids). */
    public static Vec3 getSnowColor(Level level) {
        CBT_Water water = CelestialBody.getTrait(level, CBT_Water.class);
        if (water == null || water.fluid == null) return new Vec3(1, 1, 1);
        // vanilla water → white snow; exotic liquids → weather-tinted snow
        if ("water".equals(BuiltInRegistries.FLUID.getKey(water.fluid).getPath()))
            return new Vec3(1, 1, 1);
        return getWeatherColor(level);
    }
}
