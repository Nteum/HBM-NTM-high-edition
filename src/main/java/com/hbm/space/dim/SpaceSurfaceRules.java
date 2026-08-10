package com.hbm.space.dim;

import com.hbm.registries.ModBlocks;
import com.hbm.space.dim.moon.WorldGenMun;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class SpaceSurfaceRules {
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource MOON_TURF = makeStateRule(ModBlocks.moon_turf.get());
    private static final SurfaceRules.RuleSource MOON_ROCK = makeStateRule(ModBlocks.moon_rock.get());

    private static SurfaceRules.RuleSource makeStateRule(Block block)
    {
        return SurfaceRules.state(block.defaultBlockState());
    }

//    public static SurfaceRules.RuleSource moonSurfaceRules(){
//        return SurfaceRules.sequence(
//                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, MOON_TURF),
//                SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(4, true, 0, CaveSurface.FLOOR), MOON_TURF),
//                SpaceSurfaceRules.MOON_ROCK
//        );
//    }

    public static SurfaceRules.RuleSource moonSurfaceRules() {
        // 1. 定义用到的方块状态 (BlockState)
        SurfaceRules.RuleSource moonTurf = SurfaceRules.state(ModBlocks.moon_turf.get().defaultBlockState());
        SurfaceRules.RuleSource moonRock = SurfaceRules.state(ModBlocks.moon_rock.get().defaultBlockState());
        SurfaceRules.RuleSource gravel = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());

        // 2. 条件构建
        // 条件：当前是否为月球群系 (Moon Biome)
        SurfaceRules.ConditionSource isMoonBiome = SurfaceRules.isBiome(WorldGenMun.MOON_FLAT);

        // 条件：是否暴露在空气下方（地表 Surface）
        SurfaceRules.ConditionSource isAtSurface = SurfaceRules.ON_FLOOR;

        // 条件：是否在地下填充层（Under Floor）
        SurfaceRules.ConditionSource isUnderSurface = SurfaceRules.UNDER_FLOOR;

        // 条件：Y < 50 (对应原版代码中 56 - l 左右的低洼/深层区域)
        SurfaceRules.ConditionSource isDeepY = SurfaceRules.yStartCheck(VerticalAnchor.absolute(-5), 0);

        // 3. 组装地表规则树 (Rule Tree)
        return SurfaceRules.sequence(
                // 规则 A：地表最顶层 (Y >= 62 且在地面) -> Moon Turf
                SurfaceRules.ifTrue(
                        SurfaceRules.yStartCheck(VerticalAnchor.absolute(-2), 0),
                        SurfaceRules.ifTrue(isAtSurface, moonTurf)
                ),

                // 规则 B：地表下方泥土/岩石填充层 -> Moon Rock
                SurfaceRules.ifTrue(isUnderSurface, moonRock),

                // 规则 C：深层/低洼区域 (Y < 50 且在地表以下) -> Gravel
                SurfaceRules.ifTrue(
                        SurfaceRules.not(isDeepY),
                        SurfaceRules.ifTrue(isAtSurface, gravel)
                ),

                // 默认兜底：其他裸露地表全部使用 Moon Rock 替代
                SurfaceRules.ifTrue(isAtSurface, moonRock)
        );
    }

    public static SurfaceRules.RuleSource ikeSurfaceRules() {
//        return SurfaceRules.sequence();
        return SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.state(Blocks.STONE.defaultBlockState()));
    }
}
