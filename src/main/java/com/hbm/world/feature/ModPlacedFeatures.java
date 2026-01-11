package com.hbm.world.feature;

import com.hbm.HBM;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.Tags;

import java.util.List;

/** Placed feature
 * 主要设置地物出现的位置
 * 参考内容：原版OrePlacements
 * */
public class ModPlacedFeatures {
    /**
     * PlacementModifier用于描述矿物的生成位置和条件。
     * PlacementFeature类第一个参数是关联的configuredfeatures，第二个就是相关的PlacementModifier的列表。
     *
     * 查原版的PlacementModifier可以直接查PlacementModifierType这个类
     * - InSquarePlacement.spread()表示矿物方块形状生成
     * - BiomeFilter.biome()是指在特定的生物群系生成。
     * - CountPlacement.of(p_195344_)指定了每个区块生成多少的矿物。
     * - RarityFilter.onAverageOnceEvery(p_195350_)是用于指定每隔多少个区块才生成一个矿物的。
     * */

    public static final ResourceKey<PlacedFeature> URANIUM_ORE_OVERWORLD = createKey("uranium_ore_overworld");
    public static final ResourceKey<PlacedFeature> ORE_SPHERE_OVERWORLD = createKey("ore_sphere_overworld");
    public static final ResourceKey<PlacedFeature> BEDROCK_ORE_OVERWORLD = createKey("bedrock_ore_overworld");
    public static final ResourceKey<PlacedFeature> GLYPHID_HIVE = createKey("glyphid_hive");
    public static final ResourceKey<PlacedFeature> METE_CREATOR_MOON = createKey("mete_creator_moon");

    public static void bootstrap(BootstapContext<PlacedFeature> context){
        HolderGetter<ConfiguredFeature<?, ?>> holdergetter = context.lookup(Registries.CONFIGURED_FEATURE);
        //矿物放置
        PlacementUtils.register(context, URANIUM_ORE_OVERWORLD, holdergetter.getOrThrow(ModConfiguredFeatures.URANIUM_ORE_OVERWORLD),
                commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64),VerticalAnchor.aboveBottom(30))));
        //洞穴放置
        PlacementUtils.register(context, ORE_SPHERE_OVERWORLD, holdergetter.getOrThrow(ModConfiguredFeatures.ORE_SPHERE_OVERWORLD),
                RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome());
        //基岩矿石生成
        PlacementUtils.register(context, BEDROCK_ORE_OVERWORLD, holdergetter.getOrThrow(ModConfiguredFeatures.BEDROCK_ORE_OVERWORLD),RarityFilter.onAverageOnceEvery(1));
        // 异虫巢
        PlacementUtils.register(context, GLYPHID_HIVE, holdergetter.getOrThrow(ModConfiguredFeatures.GLYPHID_HIVE) ,RarityFilter.onAverageOnceEvery(20), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
        // 陨石坑放置
        PlacementUtils.register(context, METE_CREATOR_MOON, holdergetter.getOrThrow(ModConfiguredFeatures.METE_CREATOR_MOON), RarityFilter.onAverageOnceEvery(50));
    }

    public static ResourceKey<PlacedFeature> createKey(String pKey) {
        return ResourceKey.create(Registries.PLACED_FEATURE, HBM.rl(pKey));
    }
    private static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }
    private static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }
    private static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }
}
