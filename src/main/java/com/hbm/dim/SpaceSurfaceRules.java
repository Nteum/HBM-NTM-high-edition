package com.hbm.dim;

import com.hbm.registries.ModBlocks;
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
        // 水下/低洼处的玄武岩
        SurfaceRules.RuleSource basaltInPonds = SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.ifTrue(
                        SurfaceRules.waterBlockCheck(-1, 0),
                        SurfaceRules.state(Blocks.BASALT.defaultBlockState())
                )
        );

        // 高度 < 56 的砾石层
        SurfaceRules.RuleSource gravelBelow56 = SurfaceRules.ifTrue(
                SurfaceRules.UNDER_FLOOR,
                SurfaceRules.ifTrue(
                        SurfaceRules.yBlockCheck(VerticalAnchor.absolute(56), 0),
                        SurfaceRules.state(Blocks.GRAVEL.defaultBlockState())
                )
        );

        // 顶层月壤
        SurfaceRules.RuleSource topLayer = SurfaceRules.ifTrue(
                SurfaceRules.ON_FLOOR,
                SurfaceRules.state(ModBlocks.moon_turf.get().defaultBlockState())
        );

        // 次表层月岩（不包括高度<56被砾石替换的部分）
        SurfaceRules.RuleSource fillerLayer = SurfaceRules.ifTrue(
                SurfaceRules.UNDER_FLOOR,
                SurfaceRules.state(ModBlocks.moon_rock.get().defaultBlockState())
        );

        // 从下往上应用：先判断的条件先命中
        return SurfaceRules.sequence(
                basaltInPonds,   // 先检查池塘 → 玄武岩
                gravelBelow56,   // 再检查低处 → 砾石
                topLayer,        // 地表 → 月壤
                fillerLayer      // 次表层 → 月岩
        );
    }

}
