package com.hbm.space.dim;

import java.util.List;
import java.util.Random;

import com.hbm.HBM;
import com.hbm.main.ClientEvents;
import com.hbm.space.dim.SolarSystem.AstroMetric;
import com.hbm.space.dim.trait.CBT_Atmosphere;
import com.hbm.space.dim.trait.CBT_Destroyed;
import com.hbm.space.dim.trait.CBT_Dyson;
import com.hbm.space.dim.trait.CBT_Impact;
import com.hbm.space.dim.trait.CBT_War;
import com.hbm.space.dim.trait.CelestialBodyTrait;
import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.space.dim.orbit.OrbitalStation;
import com.hbm.item.ISatChip;
import com.hbm.space.render.AtmosphereRenderUtil;
import com.hbm.handler.CelestialNukeShockHandler;
import com.hbm.core.contents.addational_data.AdditionalDataManager;
import com.hbm.core.contents.addational_data.DataEntry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Renders the custom celestial sky for all non-overworld dimensions
 * (and optionally overworld too).
 *
 * Ported from 1.7.10 SkyProviderCelestial.render(). All GL11 fixed-function
 * calls replaced with RenderSystem + PoseStack + Tesselator.
 *
 * Shader rendering (crescent, atmosphere, lightning, nightlights, nuke, swarm)
 * is stubbed pending shader JSON definitions.
 *
 * ~1600 lines → ~600 lines (shader methods stubbed, rendering core intact)
 */
public class CelestialSkyRenderer {

    private static final ResourceLocation planetTexture  = hbm("textures/misc/space/planet.png");
    private static final ResourceLocation flareTexture   = hbm("textures/misc/space/sunspike.png");
    private static final ResourceLocation nightTexture   = hbm("textures/misc/space/night.png");
    private static final ResourceLocation digammaStar    = hbm("textures/misc/space/star_digamma.png");
    private static final ResourceLocation lodeStar       = hbm("textures/misc/star_lode.png");
    private static final ResourceLocation stationTexture = hbm("textures/misc/space/station.png");
    private static final ResourceLocation ringTexture    = hbm("textures/misc/space/rings.png");
    private static final ResourceLocation shockFlareTex  = hbm("textures/particle/flare.png");
    private static final ResourceLocation particleBase   = hbm("textures/particle/particle_base.png");
    private static final ResourceLocation defaultMask    = hbm("textures/misc/space/default_mask.png");
    private static final ResourceLocation shockwaveTex   = hbm("textures/particle/shockwave.png");
    private static final ResourceLocation destroyedBody  = hbm("textures/misc/space/destroyed.png");
    private static final ResourceLocation impactTexture  = hbm("textures/misc/space/impact.png");
    private static final ResourceLocation noiseTexture   = hbm("shaders/i_channel1.png");

    private static ResourceLocation hbm(String path) {
        return ResourceLocation.fromNamespaceAndPath(HBM.MODID, path);
    }

    // ========================================================================
    // Public entry point
    // ========================================================================

    /**
     * Called from CelestialDimensionEffects.renderSky() or directly
     * from a Forge RenderLevelStageEvent handler.
     */
    public static void renderSky(ClientLevel level, float partialTick,
            PoseStack poseStack, Camera camera,
            org.joml.Matrix4f projectionMatrix) {

        Minecraft mc = Minecraft.getInstance();
        CelestialBody body = CelestialBody.getBody(level);
        CBT_Atmosphere atmosphere = body.getTrait(CBT_Atmosphere.class);
        boolean hasAtmosphere = atmosphere != null;
        float pressure = hasAtmosphere ? (float) atmosphere.getPressure() : 0.0F;

        // 诊断日志 — 定位红绿天空来源后删除
//        if (level.getGameTime() % 200 == 0 && (body.name.equals("mun") || body.name.equals("ike"))) {
//            HBM.LOGGER.warn("[SkyDebug] dim={} body={} atmosphere={} pressure={}",
//                level.dimension().location(), body.name,
//                atmosphere != null ? atmosphere.getMainFluid() : "none", pressure);
//        }

        // --- Sky color ---
        float solarAngle = level.getSunAngle(partialTick);
        float starBrightness = CelestialTimeProvider.getStarBrightness(level, partialTick);
        float sunBrightness  = CelestialTimeProvider.getSunBrightness(level, partialTick);
        float siderealAngle  = (float) SolarSystem.calculateSiderealAngle(level, partialTick, body);

        float visibility = hasAtmosphere
            ? Mth.clamp(2.0F - pressure, 0.1F, 1.0F) : 1.0F;

        // --- Vanilla sky gradient (replaces glCallList) ---
        renderSkyGradient(level, partialTick, poseStack, camera, projectionMatrix);

        // --- Stars ---
        renderSunset(poseStack, mc, level, partialTick, solarAngle, pressure, body.surfaceTexture);
        renderStars(poseStack, mc, level, partialTick, starBrightness, solarAngle + siderealAngle, body.axialTilt);

        // --- Sun + planets (under solar-angle rotation) ---
        poseStack.pushPose();
        {
            poseStack.mulPose(Axis.XP.rotationDegrees(body.axialTilt));
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(solarAngle * 360.0F));

            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            double sunSize = SolarSystem.calculateSunSize(body) * SolarSystem.SUN_RENDER_SCALE;
            double coronaSize = sunSize * (3 - Mth.clamp(pressure, 0, 1));
            renderSun(poseStack, mc, level, partialTick, sunSize, coronaSize, visibility, pressure);

            // Planet tinted overlay — base sky colour (fog density hook pending)
            Vec3 planetTint = CelestialDimensionEffects.getSkyColorStatic(level, partialTick);
            float blendAmount = hasAtmosphere
                ? Mth.clamp(1 - sunBrightness, 0.25F, 1F) : 1F;

            List<AstroMetric> metrics = getMetrics(level, partialTick, body, solarAngle);
            renderCelestials(poseStack, mc, level, partialTick, metrics, solarAngle,
                null, planetTint, visibility, blendAmount, null,
                SolarSystem.MAX_APPARENT_SIZE_SURFACE);

            RenderSystem.enableBlend();

            // --- Satellites ---
            if (visibility > 0.2F) {
                for (var entry : SatelliteSavedData.getClientSats().entrySet()) {
                    entry.getValue().render(partialTick, level, mc,
                        poseStack, solarAngle, entry.getKey());
                }

                renderHeldSatellitePreview(poseStack, mc, level, partialTick, solarAngle);

                // Stations
                for (OrbitalStation station : OrbitalStation.orbitingStations) {
                    renderStation(poseStack, mc, station, solarAngle);
                }
            }
        }
        poseStack.popPose();

        // --- War projectiles ---
        CBT_War war = body.getTrait(CBT_War.class);
        if (war != null) {
            for (CBT_War.Projectile projectile : war.getProjectiles()) {
                if (projectile.getTravel() <= 0) {
                    float flash = projectile.getFlashtime() + partialTick;
                    float alphaGlow = 1.0F - Math.min(1.0F, flash / 100);
                    renderWarProjectile(poseStack, mc, projectile, flash, alphaGlow);
                }
            }
        }

        // --- Meteors ---
        Vec3 camPos = camera.getPosition();
        float rainStrength = level.getRainLevel(partialTick);
        for (CelestialMeteor.Meteor meteor : CelestialMeteor.meteors) {
            renderMeteor(poseStack, mc, meteor, camPos, level, rainStrength);
        }

        // --- Rings ---
        if (body.hasRings) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(body.axialTilt - body.ringTilt));
            poseStack.translate(0, -100, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            renderRings(poseStack, mc, body.ringColor, 200, visibility);
            poseStack.popPose();
        }

        // --- CBT_COMPROMISED flesh shader ---
        CelestialBodyTrait.CBT_COMPROMISED compromised =
            body.getTrait(CelestialBodyTrait.CBT_COMPROMISED.class);
        if (compromised != null) {
            renderFleshSky(poseStack, mc, level, partialTick);
        }

        // Reset state
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();

        // --- Bottom-of-world darkening ---
        renderBottomDark(level, partialTick, poseStack, camera);

        // --- Planet surface below horizon (seen from high altitude/orbit) ---
        renderPlanetSurface(poseStack, mc, level, partialTick, body, camera);

        // --- Final state restoration (blocks render after us — never leak GL state!) ---
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    // ========================================================================
    // Metrics cache (per-tick memoization, replaces updateSky())
    // ========================================================================

    private static long lastMetricsTick = -1;
    private static CelestialBody lastMetricsBody;
    private static float lastMetricsSolarAngle;
    private static List<AstroMetric> cachedMetrics;

    private static List<AstroMetric> getMetrics(ClientLevel level, float partialTick,
            CelestialBody body, float solarAngle) {
        long tick = level.getGameTime();
        if (tick == lastMetricsTick
                && body == lastMetricsBody
                && solarAngle == lastMetricsSolarAngle
                && cachedMetrics != null) {
            return cachedMetrics;
        }
        cachedMetrics = SolarSystem.calculateMetricsFromBody(
            level, partialTick, body, solarAngle);
        lastMetricsTick = tick;
        lastMetricsBody = body;
        lastMetricsSolarAngle = solarAngle;
        return cachedMetrics;
    }

    // ========================================================================
    // Sky gradient (replaces vanilla glSkyList)
    // ========================================================================

    private static void renderSkyGradient(ClientLevel level, float partialTick,
            PoseStack poseStack, Camera camera,
            org.joml.Matrix4f projectionMatrix) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.depthMask(false);

        // Full sky colour from atmosphere blending
        Vec3 skyColor = CelestialDimensionEffects.getSkyColorStatic(level, partialTick);

        float skyR = (float) skyColor.x;
        float skyG = (float) skyColor.y;
        float skyB = (float) skyColor.z;

        // Diminish sky colour when leaving the atmosphere
        if (camera.getPosition().y > 300) {
            double curve = Mth.clamp((800.0 - camera.getPosition().y) / 500.0, 0, 1);
            skyR *= curve; skyG *= curve; skyB *= curve;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(skyR, skyG, skyB, 1);
        RenderSystem.disableCull(); // skybox is viewed from inside — disable backface culling

        // 6-face skybox covering the entire view sphere (replaces glSkyList)
        float s = 200;
        var m = poseStack.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // +Y (up)
        b.vertex(m, -s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        // -Y (down)
        b.vertex(m, -s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();
        // +X
        b.vertex(m,  s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        // -X
        b.vertex(m, -s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();
        // +Z
        b.vertex(m,  s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s,  s,  s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s, -s,  s).color(skyR, skyG, skyB, 1).endVertex();
        // -Z
        b.vertex(m, -s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m, -s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s,  s, -s).color(skyR, skyG, skyB, 1).endVertex();
        b.vertex(m,  s, -s, -s).color(skyR, skyG, skyB, 1).endVertex();

        t.end();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    // ========================================================================
    // Sunset / sunrise glow
    // ========================================================================

    private static void renderSunset(PoseStack ps, Minecraft mc, ClientLevel level,
            float partialTick, float solarAngle, float pressure,
            ResourceLocation surfaceTex) {

        // Charged-dust sunset: only in near-vacuum (pressure < 0.05)
        float[] sunsetColor;
        if (pressure < 0.05F) {
            float cutoff = 0.4F;
            float angle = Mth.cos(solarAngle * (float) Math.PI * 2.0F) - 0.0F;
            if (angle < -cutoff || angle > cutoff) return;
            float colorIntensity = angle / cutoff * 0.5F + 0.5F;
            float alpha = 1.0F - (1.0F - Mth.sin(colorIntensity * (float) Math.PI)) * 0.99F;
            alpha *= alpha;
            sunsetColor = new float[] { 0.9F, 1.0F, 1.0F, alpha * 0.2F };
        } else {
            float[] colors = CelestialDimensionEffects.getSunriseColorStatic(
                solarAngle, partialTick, level);
            if (colors == null) return;
            sunsetColor = colors;
        }

        float sunsetDirection = Mth.sin(solarAngle * (float) Math.PI * 2.0F) < 0.0F ? 180.0F : 0.0F;

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        ps.pushPose();
        {
            ps.mulPose(Axis.XP.rotationDegrees(90.0F));
            ps.mulPose(Axis.ZP.rotationDegrees(sunsetDirection));
            ps.mulPose(Axis.ZP.rotationDegrees(90.0F));

            var m = ps.last().pose();
            b.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            b.vertex(m, 0.0F, 100.0F, 0.0F)
                .color(sunsetColor[0], sunsetColor[1], sunsetColor[2], sunsetColor[3])
                .endVertex();

            int segments = 16;
            for (int j = 0; j <= segments; j++) {
                float ang = (float) j * (float) Math.PI * 2.0F / (float) segments;
                float sinA = Mth.sin(ang);
                float cosA = Mth.cos(ang);
                b.vertex(m, sinA * 120.0F, cosA * 120.0F, -cosA * 40.0F * sunsetColor[3])
                    .color(sunsetColor[0], sunsetColor[1], sunsetColor[2], 0.0F)
                    .endVertex();
            }
            t.end();
        }
        ps.popPose();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Stars
    // ========================================================================

    private static void renderStars(PoseStack ps, Minecraft mc, ClientLevel level,
            float partialTick, float starBrightness, float siderealAngle, float axialTilt) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.disableCull(); // starfield skybox is viewed from inside

        // Lode star — render if enabled
        if (ClientEvents.renderLodeStar) {

            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            ps.pushPose();
            {
                ps.mulPose(Axis.XP.rotationDegrees(axialTilt));
                ps.mulPose(Axis.YP.rotationDegrees(-90.0F));
                ps.mulPose(Axis.XP.rotationDegrees(siderealAngle * 360.0F));
                ps.mulPose(Axis.XP.rotationDegrees(-90.0F));

                if (starBrightness > 0.0F) {
                    ps.pushPose();
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, nightTexture);
                    RenderSystem.setShaderColor(1, 1, 1, starBrightness * 0.6f);

                    ps.mulPose(Axis.XP.rotationDegrees(90));
                    ps.mulPose(Axis.ZP.rotationDegrees(-90));
                    renderSkyboxSide(b, t, ps, 4);

                    ps.pushPose();
                    ps.mulPose(Axis.XP.rotationDegrees(90));
                    renderSkyboxSide(b, t, ps, 1);
                    ps.popPose();

                    ps.pushPose();
                    ps.mulPose(Axis.XP.rotationDegrees(-90));
                    renderSkyboxSide(b, t, ps, 0);
                    ps.popPose();

                    ps.mulPose(Axis.ZP.rotationDegrees(90));
                    renderSkyboxSide(b, t, ps, 5);
                    ps.mulPose(Axis.ZP.rotationDegrees(90));
                    renderSkyboxSide(b, t, ps, 2);
                    ps.mulPose(Axis.ZP.rotationDegrees(90));
                    renderSkyboxSide(b, t, ps, 3);

                    ps.popPose();
                }

                // --- Digamma star ---
                ps.pushPose();
                {
                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
                            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    float brightness = Math.max(0.5F, starBrightness * 1.6F);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
                    RenderSystem.setShaderTexture(0, digammaStar);

                    // Digamma from player data
                    float digamma = 0f;
                    var player = Minecraft.getInstance().player;
                    if (player != null) {
                        digamma = AdditionalDataManager.getEntityData(player, DataEntry.DIGAMMA).map(o -> (float) o).orElse(digamma);
                    }
                    float digmaSize = 1 + digamma * 0.25f;
                    float digmaDist = 100f - digamma * 2.5f;

                    ps.mulPose(Axis.XP.rotationDegrees(140));
                    ps.mulPose(Axis.ZP.rotationDegrees(-40));

                    var m = ps.last().pose();
                    b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    b.vertex(m, -digmaSize, digmaDist, -digmaSize).uv(0, 0).endVertex();
                    b.vertex(m, digmaSize, digmaDist, -digmaSize).uv(0, 1).endVertex();
                    b.vertex(m, digmaSize, digmaDist, digmaSize).uv(1, 1).endVertex();
                    b.vertex(m, -digmaSize, digmaDist, digmaSize).uv(1, 0).endVertex();
                    t.end();

                    RenderSystem.blendFuncSeparate(
                            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                }
                ps.popPose();
            }
            ps.popPose();
        }

        RenderSystem.enableCull();
    }

    private static void renderSkyboxSide(BufferBuilder b, Tesselator t, PoseStack ps, int side) {
        double u = side % 3 / 3.0;
        double v = side / 3 / 2.0;
        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -100, -100, -100).uv((float) u, (float) v).endVertex();
        b.vertex(m, -100, -100,  100).uv((float) u, (float) (v + 0.5)).endVertex();
        b.vertex(m,  100, -100,  100).uv((float) (u + 0.3333), (float) (v + 0.5)).endVertex();
        b.vertex(m,  100, -100, -100).uv((float) (u + 0.3333), (float) v).endVertex();
        t.end();
    }

    // ========================================================================
    // Sun
    // ========================================================================

    private static void renderSun(PoseStack ps, Minecraft mc, ClientLevel level,
            float partialTick, double sunSize, double coronaSize,
            float visibility, float pressure) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        CelestialBody sun = CelestialBody.getStar(level);
        CBT_Dyson dyson = sun.getTrait(CBT_Dyson.class);
        int swarmCount = dyson != null ? dyson.size() : 0;

        // BLACK HOLE SUN — destroyed star rendered via blackhole shader
        if (sun.hasTrait(CBT_Destroyed.class) && ClientEvents.blackholeShader != null) {
            renderBlackHoleSun(ps, level, partialTick, sun, sunSize);
            return;
        }

        // Blanking to conceal stars behind sun
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0, 0, 0, 1);

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        b.vertex(m, -(float) sunSize, 99.9f, -(float) sunSize).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  (float) sunSize, 99.9f, -(float) sunSize).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  (float) sunSize, 99.9f,  (float) sunSize).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -(float) sunSize, 99.9f,  (float) sunSize).color(0, 0, 0, 1).endVertex();
        t.end();

        // Depth buffer for swarm occlusion
        RenderSystem.depthMask(true);
        RenderSystem.setShaderColor(0, 0, 0, 0);

        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        b.vertex(m, -(float) sunSize * 0.25f, 100.1f, -(float) sunSize * 0.25f).color(0, 0, 0, 0).endVertex();
        b.vertex(m,  (float) sunSize * 0.25f, 100.1f, -(float) sunSize * 0.25f).color(0, 0, 0, 0).endVertex();
        b.vertex(m,  (float) sunSize * 0.25f, 100.1f,  (float) sunSize * 0.25f).color(0, 0, 0, 0).endVertex();
        b.vertex(m, -(float) sunSize * 0.25f, 100.1f,  (float) sunSize * 0.25f).color(0, 0, 0, 0).endVertex();
        t.end();

        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Sun disc
        RenderSystem.setShaderTexture(0, SolarSystem.kerbol.texture);
        RenderSystem.setShaderColor(1, 1, 1, visibility);

        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -(float) sunSize, 100, -(float) sunSize).uv(0, 0).endVertex();
        b.vertex(m,  (float) sunSize, 100, -(float) sunSize).uv(1, 0).endVertex();
        b.vertex(m,  (float) sunSize, 100,  (float) sunSize).uv(1, 1).endVertex();
        b.vertex(m, -(float) sunSize, 100,  (float) sunSize).uv(0, 1).endVertex();
        t.end();

        // Corona flare
        float flareAlpha = 1 - Mth.clamp(pressure, 0, 1) * 0.75f;
        RenderSystem.setShaderTexture(0, flareTexture);
        RenderSystem.setShaderColor(1, 1, 1, flareAlpha);
        RenderSystem.enableBlend();

        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -(float) coronaSize, 99.9f, -(float) coronaSize).uv(0, 0).endVertex();
        b.vertex(m,  (float) coronaSize, 99.9f, -(float) coronaSize).uv(1, 0).endVertex();
        b.vertex(m,  (float) coronaSize, 99.9f,  (float) coronaSize).uv(1, 1).endVertex();
        b.vertex(m, -(float) coronaSize, 99.9f,  (float) coronaSize).uv(0, 1).endVertex();
        t.end();

        // Dyson swarm — points displaced by gl_VertexID in swarm.vsh
        if (swarmCount > 0 && ClientEvents.swarmShader != null) {
            renderSwarm(ps, level, partialTick, sunSize * 0.5, swarmCount);
        }

        // Clean up depth buffer — restore depth mask afterwards!
        RenderSystem.depthMask(true);
        RenderSystem.clear(org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT, false);
        RenderSystem.depthMask(true);
    }

    // ========================================================================
    // Celestial bodies (planets/moons)
    // ========================================================================

    private static void renderCelestials(PoseStack ps, Minecraft mc, ClientLevel level,
            float partialTick, List<AstroMetric> metrics, float solarAngle,
            CelestialBody tidalLockedBody, Vec3 planetTint, float visibility,
            float blendAmount, CelestialBody orbiting, float maxSize) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        float blendDarken = 0.1F;

        double transitionMinSize = 0.01;
        double transitionMaxSize = 0.5;

        for (AstroMetric metric : metrics) {
            if (metric.distance == 0) continue; // skip self

            boolean orbitingThis = metric.body == orbiting;
            double uvOffset = orbitingThis
                ? 1 - (((level.getGameTime() + partialTick) / 1024) % 1) : 0;

            ps.pushPose();
            {
                double size = Mth.clamp(metric.apparentSize, 0, maxSize);
                boolean renderPoint = size < transitionMaxSize;
                boolean renderBody  = size > transitionMinSize;

                ps.mulPose(Axis.XP.rotationDegrees((float) metric.angle));
                ps.mulPose(Axis.ZP.rotationDegrees((float) metric.inclination));
                ps.mulPose(Axis.YP.rotationDegrees(metric.body.axialTilt + 90));

                if (renderBody) {
                    // Destroyed planet fragment rendering
                    CBT_Destroyed d = metric.body.getTrait(CBT_Destroyed.class);
                    if (d != null) {
                        renderDestroyedBody(ps, mc, b, t, metric, d, size, uvOffset);
                    } else {
                        // Atmosphere glow bands around the planet
                        renderAtmosphereGlow(ps, b, t, metric.body, size, visibility);

                        RenderSystem.disableBlend();
                        RenderSystem.setShaderTexture(0, metric.body.texture);
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.setShaderColor(1, 1, 1, visibility);

                        var m = ps.last().pose();
                        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                        b.vertex(m, -(float) size, 100, -(float) size).uv(0.0f + (float) uvOffset, 0).endVertex();
                        b.vertex(m,  (float) size, 100, -(float) size).uv(1.0f + (float) uvOffset, 0).endVertex();
                        b.vertex(m,  (float) size, 100,  (float) size).uv(1.0f + (float) uvOffset, 1).endVertex();
                        b.vertex(m, -(float) size, 100,  (float) size).uv(0.0f + (float) uvOffset, 1).endVertex();
                        t.end();

                        // Crescent shadow (day/night terminator) via crescent shader
                        renderCrescentShadow(ps, b, t,
                            (float) -metric.phase, uvOffset, size);

                        // Impact overlay (lava texture + shockwave + flare)
                        CBT_Impact impact = metric.body.getTrait(CBT_Impact.class);
                        if (impact != null) {
                            double impactTime = (level.getGameTime() - impact.time) + partialTick;
                            renderImpactOverlay(ps, b, t, metric, impactTime, size, uvOffset);
                        }

                        // Nuke shock overlays via shaders (ClientEvents.nukeShader)
                        List<CelestialNukeShockHandler.ShockStatus> shocks =
                            CelestialNukeShockHandler.getClientShocks(metric.body);
                        if (!shocks.isEmpty() && ClientEvents.nukeShader != null) {
                            RenderSystem.setShader(() -> ClientEvents.nukeShader);
                            RenderSystem.setShaderColor(1, 1, 1, 1);
                            AtmosphereRenderUtil.applyNukeShockUniforms(
                                ClientEvents.nukeShader, shocks,
                                level.getGameTime() + partialTick);
                            drawTexturedQuad(ps, b, t, size, 0.0 + uvOffset, 0.0,
                                1.0 + uvOffset, 1.0);
                            RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        }
                    }
                }

                // Point rendering (tiny distant bodies)
                if (renderPoint) {
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, planetTexture);
                    RenderSystem.enableBlend();
                    float alpha = Mth.clamp((float) size * 100, 0, 1);
                    float alpha2 = 1 - Mth.clamp(
                        ((float) size - (float) transitionMinSize)
                            / ((float) transitionMaxSize - (float) transitionMinSize),
                        0, 1);
                    RenderSystem.setShaderColor(
                        metric.body.color[0], metric.body.color[1],
                        metric.body.color[2], alpha * alpha2 * visibility);

                    var m = ps.last().pose();
                    b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    b.vertex(m, -1, 100, -1).uv(0, 0).endVertex();
                    b.vertex(m,  1, 100, -1).uv(1, 0).endVertex();
                    b.vertex(m,  1, 100,  1).uv(1, 1).endVertex();
                    b.vertex(m, -1, 100,  1).uv(0, 1).endVertex();
                    t.end();
                }
            }
            ps.popPose();
        }
    }

    // ========================================================================
    // Destroyed body fragments
    // ========================================================================

    private static void renderDestroyedBody(PoseStack ps, Minecraft mc,
            BufferBuilder b, Tesselator t, AstroMetric metric, CBT_Destroyed d,
            double size, double uvOffset) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        double progress = d.destProgress + size * 0.5;
        float alpha = (float) (1.0F - Math.min(1.0F, progress / 100));
        Random random = new Random(12);

        // 30 random planet-texture fragments flying apart
        int numQuads = 30;
        for (int i = 0; i < numQuads; i++) {
            double radius = (random.nextDouble() * size) * d.destProgress;
            double theta = random.nextDouble() * Math.PI * 2;
            double phi = random.nextDouble() * Math.PI;
            double rx = radius * Math.sin(phi) * Math.cos(theta) * 0.7;
            double ry = radius * Math.sin(phi) * Math.sin(theta);
            double rz = radius * Math.cos(phi) * 0.7;
            float rot = random.nextFloat() * 360.0F;

            double uMin = random.nextDouble();
            double vMin = random.nextDouble();
            double uMax = Math.min(uMin + random.nextDouble() * 0.2, 1.0);
            double vMax = Math.min(vMin + random.nextDouble() * 0.2, 1.0);

            // Fragment A — planet texture
            ps.pushPose();
            ps.translate(rx * -0.05, ry * 0.00, rz * -0.05);
            ps.mulPose(Axis.YP.rotationDegrees(rot * d.destProgress * 0.05F));
            RenderSystem.setShaderTexture(0, metric.body.texture);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            drawTexturedQuad(ps, b, t, size * random.nextDouble() * 0.1,
                uMin, vMin, uMax, vMax);
            ps.popPose();

            // Fragment B — destroyed body texture
            ps.pushPose();
            ps.translate(rx * 0.04, ry * 0.00, rz * 0.04);
            ps.mulPose(Axis.YP.rotationDegrees(rot * d.destProgress * 0.05F));
            RenderSystem.setShaderTexture(0, destroyedBody);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            drawTexturedQuad(ps, b, t, size * random.nextDouble() * 0.07,
                uMin, vMin, uMax, vMax);
            ps.popPose();
        }

        // Expanding shockwave
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        RenderSystem.setShaderTexture(0, shockwaveTex);
        double shockSize = (d.destProgress * 0.5) * size * 0.1;
        drawTexturedQuad(ps, b, t, shockSize, 0.0 + uvOffset, 0.0,
            1.0 + uvOffset, 1.0);

        // Bright flash flare
        RenderSystem.setShaderColor(1, 1, 1, alpha * 2);
        RenderSystem.setShaderTexture(0, shockFlareTex);
        drawTexturedQuad(ps, b, t, size * 3, 0.0 + uvOffset, 0.0,
            1.0 + uvOffset, 1.0);
    }

    private static void drawTexturedQuad(PoseStack ps, BufferBuilder b, Tesselator t,
            double size, double uMin, double vMin, double uMax, double vMax) {
        // NOTE: caller MUST set the shader — POSITION_TEX data with a
        // mismatched shader renders UV coordinates as colours (RGB gradient)
        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, (float) -size, 100, (float) -size).uv((float) uMin, (float) vMin).endVertex();
        b.vertex(m, (float)  size, 100, (float) -size).uv((float) uMax, (float) vMin).endVertex();
        b.vertex(m, (float)  size, 100, (float)  size).uv((float) uMax, (float) vMax).endVertex();
        b.vertex(m, (float) -size, 100, (float)  size).uv((float) uMin, (float) vMax).endVertex();
        t.end();
    }

    /** Impact visuals: lava overlay, expanding shockwave, static flare. */
    private static void renderImpactOverlay(PoseStack ps, BufferBuilder b, Tesselator t,
            AstroMetric metric, double impactTime, double size, double uvOffset) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        double lavaAlpha = Math.min(impactTime * 0.1, 1.0);
        double impactSize = (impactTime * 0.1) * size * 0.035;
        double impactAlpha = 1.0 - Math.min(1.0, impactTime * 0.0015);
        double flareSize = size * 1.5;
        double flareAlpha = 1.0 - Math.min(1.0, impactTime * 0.002);

        // Lava glow covering the body
        if (lavaAlpha > 0) {
            RenderSystem.setShaderColor(1, 1, 1, (float) lavaAlpha);
            RenderSystem.setShaderTexture(0, impactTexture);
            drawTexturedQuad(ps, b, t, size, 0.0 + uvOffset, 0.0,
                1.0 + uvOffset, 1.0);
        }

        // Impact shockwave — grows and fades
        if (impactAlpha > 0) {
            ps.pushPose();
            ps.translate(-size * 0.5, 0, size * 0.4);
            RenderSystem.setShaderColor(1, 1, 1, (float) impactAlpha);
            RenderSystem.setShaderTexture(0, shockwaveTex);
            drawTexturedQuad(ps, b, t, impactSize, 0, 0, 1, 1);
            ps.popPose();
        }

        // Impact flare — static size, fades
        if (flareAlpha > 0) {
            ps.pushPose();
            ps.translate(-size * 0.5, 0, size * 0.4);
            RenderSystem.setShaderColor(1, 1, 1, (float) flareAlpha);
            RenderSystem.setShaderTexture(0, shockFlareTex);
            drawTexturedQuad(ps, b, t, flareSize, 0, 0, 1, 1);
            ps.popPose();
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // War projectile flash
    // ========================================================================

    private static void renderWarProjectile(PoseStack ps, Minecraft mc,
            CBT_War.Projectile proj, float flashScale, float alpha) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();

        ps.pushPose();
        ps.translate(proj.getTranslateX() + 70, proj.getTranslateY(),
            proj.getTranslateZ() + 50);
        ps.scale(flashScale, flashScale, flashScale);
        ps.mulPose(Axis.YP.rotationDegrees(90));
        ps.mulPose(Axis.XP.rotationDegrees(-10));
        ps.mulPose(Axis.ZP.rotationDegrees(20));
        ps.mulPose(Axis.YP.rotationDegrees(-10));

        // Shockwave ring
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        RenderSystem.setShaderTexture(0, shockwaveTex);
        drawTexturedQuad(ps, b, t, 1, 0, 0, 1, 1);

        // Inner flare
        ps.scale(0.4f, 0.4f, 0.4f);
        RenderSystem.setShaderTexture(0, shockFlareTex);
        drawTexturedQuad(ps, b, t, 1, 0, 0, 1, 1);

        ps.popPose();
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Meteor
    // ========================================================================

    private static void renderMeteor(PoseStack ps, Minecraft mc,
            CelestialMeteor.Meteor meteor, Vec3 camPos, ClientLevel level,
            float rainStrength) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        ps.pushPose();

        Vec3 offset = new Vec3(
            meteor.posX - camPos.x,
            meteor.posY - camPos.y,
            meteor.posZ - camPos.z);
        double dist = Math.min(mc.options.renderDistance().get() * 16, offset.length());
        Vec3 dir = offset.normalize();
        Vec3 renderPos = dir.scale(dist);

        ps.translate(renderPos.x, renderPos.y, renderPos.z);

        double descent = 2017 - meteor.posY;
        double quadratic = (-(descent * descent) + (1517 * descent)) / 41;
        float scalar = (float) (quadratic / dist);
        ps.scale(scalar, scalar, scalar);

        if (meteor.type == CelestialMeteor.MeteorType.SMOKE) {
            RenderSystem.setShaderColor(1, 0, 0, 1);
            RenderSystem.setShaderTexture(0, particleBase);
            renderSmoke(ps, b, t, meteor.age);
        } else {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, shockFlareTex);
            renderGlow(ps, b, t, 1, 1, 1, rainStrength, mc);
        }

        ps.popPose();
    }

    // ========================================================================
    // Rings
    // ========================================================================

    private static void renderRings(PoseStack ps, Minecraft mc, float[] ringColor,
            float ringSize, float visibility) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(ringColor[0], ringColor[1], ringColor[2], visibility);
        RenderSystem.setShaderTexture(0, ringTexture);
        RenderSystem.enableBlend();

        float offset = -20;
        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, offset, -ringSize, -ringSize).uv(0, 0).endVertex();
        b.vertex(m, offset,  ringSize, -ringSize).uv(1, 0).endVertex();
        b.vertex(m, offset,  ringSize,  ringSize).uv(1, 1).endVertex();
        b.vertex(m, offset, -ringSize,  ringSize).uv(0, 1).endVertex();
        t.end();

        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Bottom-of-world darkening
    // ========================================================================

    private static void renderBottomDark(ClientLevel level, float partialTick,
            PoseStack ps, Camera camera) {

        Vec3 pos = camera.getPosition();
        double heightAboveHorizon = pos.y - level.getMinBuildHeight();
        if (heightAboveHorizon >= 0) return;

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float base = 1.0F;
        float bottom = (float) -(heightAboveHorizon + 65);
        float top = -base;

        ps.pushPose();
        ps.translate(0, 12, 0);

        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var m = ps.last().pose();
        b.vertex(m, -base, bottom,  base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, bottom,  base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,    -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,    -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, bottom, -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, bottom, -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,    -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, bottom,  base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, bottom, -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, bottom, -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, bottom,  base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,    -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,    -base).color(0, 0, 0, 1).endVertex();
        b.vertex(m, -base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,     base).color(0, 0, 0, 1).endVertex();
        b.vertex(m,  base, top,    -base).color(0, 0, 0, 1).endVertex();
        t.end();

        ps.popPose();
    }

    // ========================================================================
    // Anaglyph (3D glasses) — kept for completeness
    // ========================================================================

    private static float[] applyAnaglyph(float r, float g, float b) {
        return new float[] {
            (r * 30 + g * 59 + b * 11) / 100,
            (r * 30 + g * 70) / 100,
            (r * 30 + b * 70) / 100
        };
    }

    // ========================================================================
    // Glow / smoke billboard quads
    // ========================================================================

    private static void renderGlow(PoseStack ps, BufferBuilder b, Tesselator t,
            double x, double y, double z, float rainStrength, Minecraft mc) {
        ps.pushPose();
        RenderSystem.enableBlend();

        // Billboard rotation: face the camera
        Camera cam = mc.gameRenderer.getMainCamera();
        ps.mulPose(Axis.YP.rotationDegrees(180.0F - cam.getYRot()));
        ps.mulPose(Axis.XP.rotationDegrees(-cam.getXRot()));

        float w = 1.0F, hw = 0.5F, qw = 0.25F;
        double near = 0.51 * (Math.min(40000, Math.max(0, y - 35000)) / 40000);
        double entry = near * (1 - rainStrength)
            + (1 - (Math.min(200, Math.max(0, x - 2017)) / 200));

        float alpha = (float) Math.max(0, Math.min(1, entry));
        RenderSystem.setShaderColor(alpha, alpha, alpha, alpha);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m,  0 - hw,  0 - qw, 0).uv(1, 0).endVertex();
        b.vertex(m, w - hw,  0 - qw, 0).uv(0, 0).endVertex();
        b.vertex(m, w - hw, w - qw, 0).uv(0, 1).endVertex();
        b.vertex(m,  0 - hw, w - qw, 0).uv(1, 1).endVertex();
        t.end();

        RenderSystem.disableBlend();
        ps.popPose();
    }

    /** Smoke puff billboard — fades out as the meteor ages. */
    private static void renderSmoke(PoseStack ps, BufferBuilder b, Tesselator t,
            long age) {
        ps.pushPose();
        RenderSystem.enableBlend();

        // Billboard rotation: face the camera
        Camera cam = Minecraft.getInstance().gameRenderer.getMainCamera();
        ps.mulPose(Axis.YP.rotationDegrees(180.0F - cam.getYRot()));
        ps.mulPose(Axis.XP.rotationDegrees(-cam.getXRot()));

        float w = 1.0F, hw = 0.5F, qw = 0.25F;
        float dark = 1f - Math.min((float) age / (100f * 0.35f), 1f);
        RenderSystem.setShaderColor(0.6F * dark, 0.6F * dark, dark, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m,  0 - hw,  0 - qw, 0).uv(1, 0).endVertex();
        b.vertex(m, w - hw,  0 - qw, 0).uv(0, 0).endVertex();
        b.vertex(m, w - hw, w - qw, 0).uv(0, 1).endVertex();
        b.vertex(m,  0 - hw, w - qw, 0).uv(1, 1).endVertex();
        t.end();

        RenderSystem.disableBlend();
        ps.popPose();
    }

    // ========================================================================
    // Planet surface below horizon (seen from high altitude / orbit)
    // ========================================================================

    /**
     * Renders the surface texture of the body the player is standing on,
     * filling the sky below the horizon. This is what you see when looking
     * down from orbit or high altitude.
     */
    private static void renderPlanetSurface(PoseStack ps, Minecraft mc,
            ClientLevel level, float partialTick, CelestialBody body, Camera camera) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();

        Vec3 pos = camera.getPosition();
        double sc = 1000.0 / Math.max(pos.y, 1.0); // scale shrinks as you go higher
        double uvOffset = (pos.x / 1024) % 1;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float sunBrightness = CelestialTimeProvider.getSunBrightness(level, partialTick);
        float alpha = Mth.clamp(((float) pos.y - 200.0F) / 300.0F, 0.0F, 1.0F);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, body.texture);
        RenderSystem.setShaderColor(sunBrightness, sunBrightness, sunBrightness, alpha);

        ps.pushPose();
        ps.mulPose(Axis.XP.rotationDegrees(180)); // face downward
        float s = (float) (115 * sc);

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -s, 100.0f, -s).uv(0.0f + (float) uvOffset, 0).endVertex();
        b.vertex(m,  s, 100.0f, -s).uv(1.0f + (float) uvOffset, 0).endVertex();
        b.vertex(m,  s, 100.0f,  s).uv(1.0f + (float) uvOffset, 1).endVertex();
        b.vertex(m, -s, 100.0f,  s).uv(0.0f + (float) uvOffset, 1).endVertex();
        t.end();
        ps.popPose();

        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Flesh sky (CBT_COMPROMISED)
    // ========================================================================

    /** Renders the corrupted flesh dome over the sky via the fle shader. */
    private static void renderFleshSky(PoseStack ps, Minecraft mc,
            ClientLevel level, float partialTick) {

        if (ClientEvents.fleShader == null) return;

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();

        float time = (level.getGameTime() + partialTick) * 0.2F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(() -> ClientEvents.fleShader);
        RenderSystem.setShaderTexture(0, noiseTexture);

        var iTime = ClientEvents.fleShader.getUniform("iTime");
        if (iTime != null) iTime.set(time * 0.05F);
        var iChannel = ClientEvents.fleShader.getUniform("iChannel0");
        if (iChannel != null) iChannel.set(0);

        ps.pushPose();
        ps.mulPose(Axis.ZP.rotationDegrees(90));
        ps.scale(194.5f, 70.5f, 94.5f);
        // Fix orbital plane
        ps.mulPose(Axis.YP.rotationDegrees(-90.0F));

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -1, 100, -1).uv(0, 0).endVertex();
        b.vertex(m,  1, 100, -1).uv(1, 0).endVertex();
        b.vertex(m,  1, 100,  1).uv(1, 1).endVertex();
        b.vertex(m, -1, 100,  1).uv(0, 1).endVertex();
        t.end();
        ps.popPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Held satellite preview
    // ========================================================================

    /**
     * Preview the satellite currently held in the player's hand,
     * showing its orbit line and position in the sky.
     */
    private static void renderHeldSatellitePreview(PoseStack ps, Minecraft mc,
            ClientLevel level, float partialTick, float solarAngle) {

        var player = mc.player;
        if (player == null) return;

        net.minecraft.world.item.ItemStack held = player.getMainHandItem();
        if (held.isEmpty() || !Satellite.isSatelliteItem(held.getItem())) return;

        CelestialBody body = CelestialBody.getBody(level);
        // Only show preview when the satellite type targets this body
        if (!Satellite.getTargetDimensionId(held, body.dimension).equals(body.dimension)) return;

        float r = Satellite.getColorR(held);
        float g = Satellite.getColorG(held);
        float b2 = Satellite.getColorB(held);
        float inclination = Satellite.getInclination(held);
        float altitude = Satellite.getAltitude(held);
        float phaseOffset = Satellite.getPhaseOffset(held);
        boolean isBlinking = Satellite.isBlinking(held);
        float blinkPeriod = Satellite.getBlinkPeriod(held);

        Satellite.renderOrbitLine(solarAngle, r, g, b2, inclination, altitude,
            isBlinking, blinkPeriod, ps);
        Satellite.renderDefault(partialTick, level, mc, ps, solarAngle,
            ISatChip.getFreqS(held), r, g, b2, inclination, altitude, phaseOffset,
            isBlinking, blinkPeriod);
    }

    // ========================================================================
    // Orbital stations
    // ========================================================================

    /** Renders a station in orbit, slowly rotating with seed-based orientation. */
    private static void renderStation(PoseStack ps, Minecraft mc,
            OrbitalStation station, float solarAngle) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();

        long seed = station.dX * 1024L + station.dZ;
        double ticks = (double) (System.currentTimeMillis() % (1600 * 50)) / 50;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, stationTexture);
        RenderSystem.setShaderColor(0.8F, 1, 1, 1);

        ps.pushPose();
        ps.mulPose(Axis.XP.rotationDegrees(solarAngle * -360.0F));
        ps.mulPose(Axis.XP.rotationDegrees(-40.0F + (float) (seed % 800) * 0.1F - 5.0F));
        ps.mulPose(Axis.YP.rotationDegrees((float) (seed % 50) * 0.1F - 20.0F));
        ps.mulPose(Axis.ZP.rotationDegrees((float) (seed % 80) * 0.1F - 2.5F));
        ps.mulPose(Axis.XP.rotationDegrees((float) ((ticks / 1600.0) * -360.0)));

        float size = 0.8F;
        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, -size, 100.0f, -size).uv(0, 0).endVertex();
        b.vertex(m,  size, 100.0f, -size).uv(0, 1).endVertex();
        b.vertex(m,  size, 100.0f,  size).uv(1, 1).endVertex();
        b.vertex(m, -size, 100.0f,  size).uv(1, 0).endVertex();
        t.end();
        ps.popPose();

        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Atmosphere glow around planets
    // ========================================================================

    /**
     * Renders the soft atmosphere glow bands around a planet using
     * non-linear gradient stepping (four trapezoid bands with colour falloff).
     */
    private static void renderAtmosphereGlow(PoseStack ps, BufferBuilder b, Tesselator t,
            CelestialBody body, double size, float visibility) {

        float glowAlpha = AtmosphereRenderUtil.getAtmosphereGlowAlpha(body) * visibility;
        if (glowAlpha <= 0.001F) return;

        float leadingGlow = glowAlpha;
        float trailingGlow = glowAlpha;

        Vec3 atmo = AtmosphereRenderUtil.getBodyAtmosphereColor(body);
        float r = Mth.clamp((float) atmo.x * 1.15F, 0.0F, 1.0F);
        float g = Mth.clamp((float) atmo.y * 1.15F, 0.0F, 1.0F);
        float bl = Mth.clamp((float) atmo.z * 1.15F, 0.0F, 1.0F);

        // Non-linear gradient stepping
        double innerSize = size * 0.98;
        double middleSize = size * 1.075;
        double outerSize = size * 1.15 * (1.0 + glowAlpha * 0.25);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Top band
        b.vertex(m, (float) -outerSize, 100, (float) -outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  outerSize, 100, (float) -outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float) -middleSize).color(r, g, bl, leadingGlow / 2).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float) -middleSize).color(r, g, bl, trailingGlow / 2).endVertex();

        b.vertex(m, (float) -middleSize, 100, (float) -middleSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float) -middleSize).color(r, g, bl, leadingGlow / 2).endVertex();
        b.vertex(m, (float)  innerSize, 100, (float) -innerSize).color(r, g, bl, leadingGlow).endVertex();
        b.vertex(m, (float) -innerSize, 100, (float) -innerSize).color(r, g, bl, trailingGlow).endVertex();

        // Left band
        b.vertex(m, (float)  outerSize, 100, (float) -outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  outerSize, 100, (float)  outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float)  middleSize).color(r, g, bl, leadingGlow / 2).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float) -middleSize).color(r, g, bl, 0).endVertex();

        b.vertex(m, (float)  middleSize, 100, (float) -middleSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float)  middleSize).color(r, g, bl, leadingGlow / 2).endVertex();
        b.vertex(m, (float)  innerSize, 100, (float)  innerSize).color(r, g, bl, leadingGlow).endVertex();
        b.vertex(m, (float)  innerSize, 100, (float) -innerSize).color(r, g, bl, 0).endVertex();

        // Bottom band
        b.vertex(m, (float)  outerSize, 100, (float)  outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float) -outerSize, 100, (float)  outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float)  middleSize).color(r, g, bl, trailingGlow / 2).endVertex();
        b.vertex(m, (float)  middleSize, 100, (float)  middleSize).color(r, g, bl, leadingGlow / 2).endVertex();

        b.vertex(m, (float)  middleSize, 100, (float)  middleSize).color(r, g, bl, leadingGlow / 2).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float)  middleSize).color(r, g, bl, trailingGlow / 2).endVertex();
        b.vertex(m, (float) -innerSize, 100, (float)  innerSize).color(r, g, bl, trailingGlow).endVertex();
        b.vertex(m, (float)  innerSize, 100, (float)  innerSize).color(r, g, bl, leadingGlow).endVertex();

        // Right band
        b.vertex(m, (float) -outerSize, 100, (float)  outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float) -outerSize, 100, (float) -outerSize).color(r, g, bl, 0).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float) -middleSize).color(r, g, bl, trailingGlow / 2).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float)  middleSize).color(r, g, bl, trailingGlow / 2).endVertex();

        b.vertex(m, (float) -middleSize, 100, (float)  middleSize).color(r, g, bl, trailingGlow / 2).endVertex();
        b.vertex(m, (float) -middleSize, 100, (float) -middleSize).color(r, g, bl, trailingGlow / 2).endVertex();
        b.vertex(m, (float) -innerSize, 100, (float) -innerSize).color(r, g, bl, trailingGlow).endVertex();
        b.vertex(m, (float) -innerSize, 100, (float)  innerSize).color(r, g, bl, 0).endVertex();

        t.end();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Black hole sun (CBT_Destroyed)
    // ========================================================================

    /** Renders the destroyed star as a black hole using the blackhole shader. */
    private static void renderBlackHoleSun(PoseStack ps, ClientLevel level,
            float partialTick, CelestialBody sun, double sunSize) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();
        double shaderSize = sunSize * sun.shaderScale;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> ClientEvents.blackholeShader);
        RenderSystem.setShaderTexture(0, noiseTexture);

        float time = (level.getGameTime() + partialTick) / 20.0F;
        var iTime = ClientEvents.blackholeShader.getUniform("iTime");
        if (iTime != null) iTime.set(time);
        var iChannel = ClientEvents.blackholeShader.getUniform("iChannel1");
        if (iChannel != null) iChannel.set(0);

        ps.pushPose();
        // Fix orbital plane
        ps.mulPose(Axis.YP.rotationDegrees(-90.0F));

        var m = ps.last().pose();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        b.vertex(m, (float) -shaderSize, 100, (float) -shaderSize).uv(0, 0).endVertex();
        b.vertex(m, (float)  shaderSize, 100, (float) -shaderSize).uv(1, 0).endVertex();
        b.vertex(m, (float)  shaderSize, 100, (float)  shaderSize).uv(1, 1).endVertex();
        b.vertex(m, (float) -shaderSize, 100, (float)  shaderSize).uv(0, 1).endVertex();
        t.end();
        ps.popPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableBlend();
    }

    // ========================================================================
    // Dyson swarm
    // ========================================================================

    /**
     * Renders swarm members as GL points displaced by the vertex shader
     * (swarm.vsh uses gl_VertexID to scatter each point).
     */
    private static void renderSwarm(PoseStack ps, ClientLevel level,
            float partialTick, double swarmRadius, int swarmCount) {

        Tesselator t = Tesselator.getInstance();
        BufferBuilder b = t.getBuilder();

        float time = (level.getGameTime() + partialTick) / 800.0F;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> ClientEvents.swarmShader);
        RenderSystem.setShaderColor(0, 0, 0, 1);

        var iTime = ClientEvents.swarmShader.getUniform("iTime");
        if (iTime != null) iTime.set(time);

        ps.pushPose();
        ps.translate(0, 100, 0);
        ps.scale((float) swarmRadius, (float) swarmRadius, (float) swarmRadius);

        // Three tilted rings, interleaved point batches (matching original i%3 split)
        drawSwarmRing(ps, b, t, 0, 0, 80.0F, swarmCount, time);
        drawSwarmRing(ps, b, t, 1, 60.0F, 80.0F, swarmCount, time);
        drawSwarmRing(ps, b, t, 2, -60.0F, 80.0F, swarmCount, time);

        ps.popPose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableBlend();
    }

    private static void drawSwarmRing(PoseStack ps, BufferBuilder b, Tesselator t,
            int offset, float yaw, float pitch, int swarmCount, float time) {

        ps.pushPose();
        ps.mulPose(Axis.YP.rotationDegrees(yaw));
        ps.mulPose(Axis.XP.rotationDegrees(pitch));

        var m = ps.last().pose();

        // 这一段似乎没法用原有的POINTS类型，因此先用GL实现试一试是否可行
        // 在渲染代码中
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 开始绘制点
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3f(1.0f, 1.0f, 1.0f); // 设置颜色
        for (int i = offset; i < swarmCount; i += 3) {
            float tt = i + time;
            double x = Math.cos(tt);
            double z = Math.sin(tt);
            GL11.glVertex3d(x, 0, z);
        }
        GL11.glEnd();
        // 恢复渲染状态
        RenderSystem.disableBlend();


//        b.begin(VertexFormat.Mode.POINTS, DefaultVertexFormat.POSITION_COLOR);
//        for (int i = offset; i < swarmCount; i += 3) {
//            float tt = i + time;
//            double x = Math.cos(tt);
//            double z = Math.sin(tt);
//            b.vertex(m, (float) x, 0, (float) z).color(0, 0, 0, 1).endVertex();
//        }
        t.end();
        ps.popPose();
    }

    // ========================================================================
    // Crescent shadow (day/night terminator)
    // ========================================================================

    /** Renders the shadowed half of a planet using the crescent shader. */
    private static void renderCrescentShadow(PoseStack ps, BufferBuilder b, Tesselator t,
            float phase, double uvOffset, double size) {

        if (ClientEvents.crescentShader == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> ClientEvents.crescentShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        var phaseU = ClientEvents.crescentShader.getUniform("phase");
        if (phaseU != null) phaseU.set(phase);
        var offsetU = ClientEvents.crescentShader.getUniform("offset");
        if (offsetU != null) offsetU.set((float) uvOffset);
        var bodyTexU = ClientEvents.crescentShader.getUniform("bodyTex");
        if (bodyTexU != null) bodyTexU.set(0);
        var maskU = ClientEvents.crescentShader.getUniform("useBodyAlphaMask");
        if (maskU != null) maskU.set(0);

        drawTexturedQuad(ps, b, t, size, 0.0 + uvOffset, 0.0, 1.0 + uvOffset, 1.0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableBlend();
    }
}
