package com.hbm.world.feature;

import com.google.common.base.Suppliers;
import com.hbm.main.HBMxx;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/** configured feature
 * 内容主要是地物的生成细节
 * 参考内容：原版OreFeatures
 * */
public class ModConfiguredFeatures {
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
    public static final ResourceKey<ConfiguredFeature<?,?>> URANIUM_ORE_OVERWORLD = createKey("uranium_ore_overworld");
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context){
        //替换规则
        RuleTest stoneReplace = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
        RuleTest deepslateReplace = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplace = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endReplace = new BlockMatchTest(Blocks.END_STONE);
        //注册矿石生成
        FeatureUtils.register(context,URANIUM_ORE_OVERWORLD, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(stoneReplace, ModBlocks.URANIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplace, ModBlocks.DEEPSLATE_URANIUM_ORE.get().defaultBlockState())
        ),16));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String pName) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, HBMxx.hbm(pName));
    }
}