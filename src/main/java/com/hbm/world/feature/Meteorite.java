package com.hbm.world.feature;

import com.hbm.config.ConfigWorld;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * HBM 的陨石
 * */
public class Meteorite extends Feature<Meteorite.Configuration> {
    public static Set<Block> replacables;
    public Meteorite(Codec<Meteorite.Configuration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Meteorite.Configuration> context) {
        Configuration config = context.config();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();

        if (replacables.isEmpty()) generateReplacables();
        if (config.damagingImpact) {
        }

        int typeCode = rand.nextInt(300);
        boolean flagSpecial = ConfigWorld.enableSpecialMeteors.get() && config.allowSpecials && typeCode <= 12;
        // 0 - Molten; 1 - Cobble; 2 - Broken; 3 - Mix
        int hull = rand.nextInt(4);

        // 0 - Cobble; 1 - Broken; 2 - Mix
        int outerPadding = hull == 2 ? 1 + rand.nextInt(2) : (hull == 3) ? 2 : 0;

        // 0 - Broken; 1 - Stone; 2 - Netherrack
        int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);

        // 0 - Meteor; 1 - Treasure; 2 - Ore
        int core = innerPadding > 0 ? 2 : rand.nextInt(2);



        return true;
    }

    public void generateLarge(FeaturePlaceContext<Meteorite.Configuration> context){

    }
    public void generateMedium(FeaturePlaceContext<Meteorite.Configuration> context){

    }

    public void generateSmall(FeaturePlaceContext<Meteorite.Configuration> context){

    }

    public static void generateReplacables() {
        replacables = new HashSet<>();
//        replacables.add(ModBlocks.block_meteor);
//        replacables.add(ModBlocks.block_meteor_broken);
//        replacables.add(ModBlocks.block_meteor_cobble);
//        replacables.add(ModBlocks.block_meteor_molten);
//        replacables.add(ModBlocks.block_meteor_treasure);
//        replacables.add(ModBlocks.ore_meteor);
    }

    /**
     * 召唤陨石
     * */
    public static void spawnMeteor(ServerLevel level, BlockPos strikePos, boolean safe, boolean allowSpecials, boolean damagingImpact) {
        // 1. 创建即时配置对象 (不用经过注册表，直接 new)
        Configuration configuration = new Configuration(safe, allowSpecials, damagingImpact);

        // 2. 获取你的 Feature 实例
        // 假设你已经定义并注册了 HBMFeatures.METEOR_CRATER
        Meteorite meteorite = ModFeatures.METEORITE.get();

        // 3. 构建放置上下文 (PlaceContext)
        // 注意：手动生成不需要配置 PlacedFeature，直接调用 place 方法
        FeaturePlaceContext<Configuration> context = new FeaturePlaceContext<>(
                Optional.empty(), // 如果不涉及配置地物引用，传空
                level,
                level.getChunkSource().getGenerator(),
                level.getRandom(),
                strikePos,
                configuration
        );

        // 4. 执行生成
        meteorite.place(context);
    }

    public record Configuration(boolean safe, boolean allowSpecials, boolean damagingImpact) implements FeatureConfiguration{
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf("safe").forGetter(o -> o.safe),
                    Codec.BOOL.fieldOf("allowSpecials").forGetter(o -> o.allowSpecials),
                    Codec.BOOL.fieldOf("damagingImpact").forGetter(o -> o.damagingImpact)
                ).apply(instance, Configuration::new)
        );
    }
}
