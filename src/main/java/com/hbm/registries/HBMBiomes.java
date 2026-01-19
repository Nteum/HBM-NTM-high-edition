package com.hbm.registries;

import com.hbm.HBM;
import com.hbm.dim.moon.HBMMun;
import com.hbm.world.biome.HBMSurfaceRules;
import com.hbm.world.biome.NoMansLand;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;

public class HBMBiomes {
    public static final ResourceKey<Biome> NO_MANS_LAND = register("no_mans_land");
    public static final ResourceKey<Biome> MUN = register("hbm_mun");
    public static final ResourceKey<Biome> MOON_HEIGHTLAND = register("hbm_moon_heighland");

    public static ResourceKey<Biome> register(String name)
    {
        return ResourceKey.create(Registries.BIOME, new ResourceLocation(HBM.MODID, name));
    }
    public static void setUp(){
        // Weights are kept intentionally low as we add minimal biomes
        Regions.register(new NoMansLand(NO_MANS_LAND.location(), 10));
        Regions.register(new HBMMun(MUN.location(), 10));
        Regions.register(new HBMMun(MOON_HEIGHTLAND.location(), 4));

        // 主世界表面规则
        SurfaceRuleManager.addSurfaceRules(RuleCategory.OVERWORLD, HBM.MODID, HBMSurfaceRules.overworldRules());
    }
}
