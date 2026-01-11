package com.hbm.world.biome;


import com.hbm.registries.HBMBiomes;
import com.hbm.registries.ModBlocks;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public class HBMSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource MOON_TURF = makeStateRule(ModBlocks.moon_turf.get());
    private static final SurfaceRules.RuleSource MOON_ROCK = makeStateRule(ModBlocks.moon_rock.get());
    public static SurfaceRules.RuleSource overworldRules()
    {
        SurfaceRules.ConditionSource isAtOrAboveWaterLevel = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.RuleSource grassSurface = SurfaceRules.sequence(SurfaceRules.ifTrue(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);
        SurfaceRules.ConditionSource randomCond1 = SurfaceRules.noiseCondition(Noises.PATCH, 0.0D);

        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(HBMBiomes.NO_MANS_LAND), SurfaceRules.sequence(
                        SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(randomCond1, DIRT), GRAVEL)),
                        SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, GRAVEL)
                )),
                // Default to a grass and dirt surface
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, grassSurface)
        );
    }

    public static SurfaceRules.RuleSource moonRules(){
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, MOON_TURF),
                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, true, 0, CaveSurface.FLOOR), MOON_TURF),
                MOON_ROCK
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }
    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double pValue) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, pValue / 8.25D, Double.MAX_VALUE);
    }
}
