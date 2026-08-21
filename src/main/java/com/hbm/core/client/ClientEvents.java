package com.hbm.core.client;

import com.hbm.HBM;
import com.hbm.space.dim.CelestialDimensionEffects;
import com.hbm.space.dim.orbit.SpaceSpecialEffects;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static com.hbm.HBM.MODID;

public class ClientEvents {

    /** Set to false to hide the lode star (special skybox object). */
    public static boolean renderLodeStar = true;

    // ================================================================
    // Registered shader instances — accessed by CelestialSkyRenderer
    // ================================================================

    public static ShaderInstance crescentShader;
    public static ShaderInstance atmosphereShader;
    public static ShaderInstance atmosphereEmissiveShader;
    public static ShaderInstance lightningShader;
    public static ShaderInstance nukeShader;
    public static ShaderInstance nightLightsShader;
    public static ShaderInstance blackholeShader;
    public static ShaderInstance blackholedShader;
    public static ShaderInstance fleShader;
    public static ShaderInstance supernovaeShader;
    public static ShaderInstance swarmShader;

    // ================================================================
    // Client Forge bus events
    // ================================================================

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

    }

    // ================================================================
    // Client Mod bus events
    // ================================================================

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerDimensionsSpecialEffects(RegisterDimensionSpecialEffectsEvent event){
            event.register(HBM.rl("moon_effects"), new CelestialDimensionEffects());
        }
        @SubscribeEvent
        public static void handleRegisterShaders(RegisterShadersEvent event) throws IOException {
            ResourceProvider provider = event.getResourceProvider();

            // --- POSITION_TEX shaders ---
            event.registerShader(loadShader(provider, "crescent", DefaultVertexFormat.POSITION_TEX), s -> crescentShader = s);
            event.registerShader(loadShader(provider, "atmosphere", DefaultVertexFormat.POSITION_TEX), s -> atmosphereShader = s);
            event.registerShader(loadShader(provider, "atmosphere_emissive", DefaultVertexFormat.POSITION_TEX), s -> atmosphereEmissiveShader = s);
            event.registerShader(loadShader(provider, "lightning", DefaultVertexFormat.POSITION_TEX), s -> lightningShader = s);
            event.registerShader(loadShader(provider, "nuke", DefaultVertexFormat.POSITION_TEX), s -> nukeShader = s);
            event.registerShader(loadShader(provider, "nightlights", DefaultVertexFormat.POSITION_TEX), s -> nightLightsShader = s);
            event.registerShader(loadShader(provider, "blackhole", DefaultVertexFormat.POSITION_TEX), s -> blackholeShader = s);
            event.registerShader(loadShader(provider, "blackholed", DefaultVertexFormat.POSITION_TEX), s -> blackholedShader = s);
            event.registerShader(loadShader(provider, "fle", DefaultVertexFormat.POSITION_TEX), s -> fleShader = s);
            event.registerShader(loadShader(provider, "supernovae", DefaultVertexFormat.POSITION_TEX), s -> supernovaeShader = s);

            // --- Swarm shader (POSITION_COLOR, custom vertex shader) ---
            event.registerShader(loadShader(provider, "swarm", DefaultVertexFormat.POSITION_COLOR),
                s -> swarmShader = s);
        }

        private static ShaderInstance loadShader(ResourceProvider provider, String name, VertexFormat format) throws IOException {
            return new ShaderInstance(provider, ResourceLocation.fromNamespaceAndPath(MODID, name), format);
        }
    }
}
