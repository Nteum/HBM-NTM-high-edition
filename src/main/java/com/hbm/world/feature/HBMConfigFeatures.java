package com.hbm.world.feature;

import com.hbm.HBM;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.RegistryHelper;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.ArrayList;
import java.util.List;

/** configured feature
 * 内容主要是地物的生成细节
 * 参考内容：原版OreFeatures
 *
 * 要实现一个地物（feature），需要的东西大概包括：
 * - Feature类，继承Feature<>，里面的place函数包含生成地物的逻辑。不需要新的可以找现成的。
 * - Configuration类，继承FeatureConfiguration，包含读取配置文件的CODEC。不需要新的可以找现成的。
 * - 注册Configuration
 * - 注册Placement
 * - 编写forge的biome modifier
 * */
public class HBMConfigFeatures {
    /*
    创建一个Supplier对象，提供一个包含了两个OreConfiguration.TargetBlockState对象的列表
    OreConfiguration.TargetBlockState 描述矿物生成时候的目标方块和替代方块。
    其中第一个参数：RuleTest 表示了替代的规则。 第二个参数BlockState表示了替代的方块和方块的状态。
    OreFeatures.STONE_ORE_REPLACEABLES 表示替代的规则是替代：石头、花岗岩、安山岩
    OreFeatures.DEEPSLATE_ORE_REPLACEABLES 替代深渊的石头。
     */
    /*
    注册描述zircon_ore矿物生成的方式，即将我们刚刚写的内容注册。
    ConfiguredFeature描述世界生成时候的结构和地形，例如矿物
    Feature.ORE 表示了生成时特定的类型。生成的逻辑。
    OreConfiguration 提供生成的额外数据，其中第一一个参数是一个list<TargetBlockState>类型，第二是个参数表示了每个矿脉的生成数量。
     */
//    public static final ResourceKey<ConfiguredFeature<?,?>> URANIUM_ORE_OVERWORLD = createKey("uranium_ore_overworld");
//    public static final ResourceKey<ConfiguredFeature<?,?>> ORE_SPHERE_OVERWORLD = createKey("ore_sphere_overworld");
//    public static final ResourceKey<ConfiguredFeature<?,?>> BEDROCK_ORE_OVERWORLD = createKey("bedrock_ore_overworld");
//    public static final ResourceKey<ConfiguredFeature<?,?>> GLYPHID_HIVE = createKey("glyphid_hive");
//    public static final ResourceKey<ConfiguredFeature<?,?>> METE_CREATOR_MOON = createKey("mete_creator_moon");
//    public static final ResourceKey<ConfiguredFeature<?,?>> METEORITE = createKey("meteorite");


    private static final List<FeatureSpec> ALL_SPECS = new ArrayList<>();

    private static final RuleTest stoneReplace = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    private static final RuleTest deepslateReplace = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    public static final FeatureSpec<MeteCreator.CreatorConfiguration> METE_CREATOR_MOON = add(new FeatureSpec<>(
            "mete_creator_moon", ModFeatures.METE_CREATOR.get(),
            new MeteCreator.CreatorConfiguration(ModBlocks.moon_rock.get().defaultBlockState(), Blocks.BASALT.defaultBlockState(), 8, 24),
            new PlacementModifier[]{RarityFilter.onAverageOnceEvery(50)}
    ));

    public static final FeatureSpec<OreConfiguration> URANIUM_ORE_OVERWORLD = add(new FeatureSpec<>(
            "uranium_ore_overworld", Feature.ORE,
            new OreConfiguration(List.of(
                    OreConfiguration.target(stoneReplace, ModBlocks.URANIUM_ORE.get().defaultBlockState()),
                    OreConfiguration.target(deepslateReplace, ModBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState())
            ),16),
            commonOrePlacement(7, HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-64),VerticalAnchor.aboveBottom(30))).toArray(PlacementModifier[]::new)
    ));

    public static final FeatureSpec<GeodeConfiguration> ORE_SPHERE_OVERWORLD = add(new FeatureSpec<>(
            "ore_sphere_overworld", Feature.GEODE,
            new GeodeConfiguration(
                    new GeodeBlockSettings(BlockStateProvider.simple(ModBlocks.RARE_EARTH_ORE.get()), BlockStateProvider.simple(ModBlocks.URANIUM_ORE.get()),
                            BlockStateProvider.simple(ModBlocks.LITHIUM_ORE.get()), BlockStateProvider.simple(ModBlocks.ASBESTOS_ORE.get()), BlockStateProvider.simple(ModBlocks.BASALT_ASBESTOS_ORE.get()),
                            List.of(ModBlocks.SA326_ORE.get().defaultBlockState()),
                            BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
                    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D),
                    new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true,
                    UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2),
                    -16, 16, 0.05D, 1),
            new PlacementModifier[]{RarityFilter.onAverageOnceEvery(24), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(30)), BiomeFilter.biome()})
    );

    public static final FeatureSpec<MeteCreator.CreatorConfiguration> BEDROCK_ORE_OVERWORLD = add(new FeatureSpec<>(
            "bedrock_ore_overworld", ModFeatures.BEDROCK_ORE.get(), FeatureConfiguration.NONE,
            new PlacementModifier[]{RarityFilter.onAverageOnceEvery(1)}
    ));

    public static final FeatureSpec<MeteCreator.CreatorConfiguration> GLYPHID_HIVE = add(new FeatureSpec<>(
            "glyphid_hive", ModFeatures.GLYPHID_HIVE.get(), FeatureConfiguration.NONE,
            new PlacementModifier[]{RarityFilter.onAverageOnceEvery(5), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()}
    ));

    public static final FeatureSpec<MeteCreator.CreatorConfiguration> METEORITE = add(new FeatureSpec<>(
            "meteorite", ModFeatures.METEORITE.get(), new Meteorite.Configuration(false, false, false),
            new PlacementModifier[]{RarityFilter.onAverageOnceEvery(50)}
    ));

//    public static final FeatureSpec<OilBubbleFeature.Config> OIL_BUBBLE_MOON = add(
//            new FeatureSpec<>(
//                    "oil_bubble_test",                              // name
//                    NTVFeature.BUBBLE.get(),
//                    OilBubbleFeature.Config.simple(
//                            Blocks.COAL_ORE.defaultBlockState(),         // block: 先用煤矿测试
//                            Blocks.STONE.defaultBlockState(),            // replace: 替换石头
//                            8,                                          // minSize
//                            16,                                         // maxSize（≤16！）
//                            true                                        // fuzzy: 边缘模糊
//                    ),
//                    new PlacementModifier[]{
//                            RarityFilter.onAverageOnceEvery(40)          // 每~40个chunk生成一个
//                    }
//            )
//    );

    /**
     * ConfiguredFeature的启动
     */
    public static void bootstrapCF(BootstapContext<ConfiguredFeature<?, ?>> context){
        for (var spec : ALL_SPECS) spec.bootstrapConfigured(context);
//        //替换规则
//        RuleTest stoneReplace = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
//        RuleTest deepslateReplace = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
//        RuleTest netherReplace = new BlockMatchTest(Blocks.NETHERRACK);
//        RuleTest endReplace = new BlockMatchTest(Blocks.END_STONE);
//        //注册矿石生成
//        FeatureUtils.register(context,URANIUM_ORE_OVERWORLD, Feature.ORE, new OreConfiguration(List.of(
//                OreConfiguration.target(stoneReplace, ModBlocks.URANIUM_ORE.get().defaultBlockState()),
//                OreConfiguration.target(deepslateReplace, ModBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState())
//        ),16));
        //洞穴生成
//        FeatureUtils.register(context, ORE_SPHERE_OVERWORLD, Feature.GEODE, new GeodeConfiguration(
//                new GeodeBlockSettings(BlockStateProvider.simple(ModBlocks.RARE_EARTH_ORE.get()), BlockStateProvider.simple(ModBlocks.URANIUM_ORE.get()),
//                        BlockStateProvider.simple(ModBlocks.LITHIUM_ORE.get()), BlockStateProvider.simple(ModBlocks.ASBESTOS_ORE.get()), BlockStateProvider.simple(ModBlocks.BASALT_ASBESTOS_ORE.get()),
//                        List.of(ModBlocks.SA326_ORE.get().defaultBlockState()),
//                        BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
//                new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D),
//                new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D, true,
//                UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2),
//                -16, 16, 0.05D, 1));
        //基岩矿石生成
//        FeatureUtils.register(context, BEDROCK_ORE_OVERWORLD, ModFeatures.BEDROCK_ORE.get());
        // 异虫巢生成
//        FeatureUtils.register(context, GLYPHID_HIVE, ModFeatures.GLYPHID_HIVE.get());
        // 月球陨石坑生成
//        FeatureUtils.register(context, METE_CREATOR_MOON, ModFeatures.METE_CREATOR.get(),
//                new MeteCreator.CreatorConfiguration(ModBlocks.moon_rock.get().defaultBlockState(), Blocks.BASALT.defaultBlockState(), 8, 24));
        // 陨石自然生成
//        FeatureUtils.register(context, METEORITE, ModFeatures.METEORITE.get(),
//                new Meteorite.Configuration(false, false, false));
    }
    /**
     * PlacedFeature的启动
     */
    public static void bootstrapPF(BootstapContext<PlacedFeature> context){
        for (var spec : ALL_SPECS) spec.bootstrapPlaced(context);
    }

    private static FeatureSpec add(FeatureSpec featureSpec){
        ALL_SPECS.add(featureSpec);
        return featureSpec;
    }

    public record FeatureSpec<FC extends FeatureConfiguration>(
            String name,
            Feature<FC> feature,          // 代码层：DeferredRegister 注册这个
            FC config,                    // 数据层：ConfiguredFeature 用这个配置
            PlacementModifier[] placement // 数据层：PlacedFeature 用这个放置
    ) {
        // --- 数据层注册 ---
        public void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            ctx.register(configuredKey(), new ConfiguredFeature<>(feature, config));
        }

        public void bootstrapPlaced(BootstapContext<PlacedFeature> ctx) {
            ctx.register(placedKey(), new PlacedFeature(
                    ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configuredKey()),
                    List.of(placement)
            ));
        }

        public ResourceKey<ConfiguredFeature<?, ?>> configuredKey() {
            return ResourceKey.create(Registries.CONFIGURED_FEATURE, RegistryHelper.rl(name));
        }

        public ResourceKey<PlacedFeature> placedKey() {
            return ResourceKey.create(Registries.PLACED_FEATURE, RegistryHelper.rl(name));
        }
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