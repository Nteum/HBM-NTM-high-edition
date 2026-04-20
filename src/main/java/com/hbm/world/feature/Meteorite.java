package com.hbm.world.feature;

import com.hbm.config.ConfigWorld;
import com.hbm.registries.ModBlocks;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.*;

/**
 * HBM 的陨石
 * */
public class Meteorite extends Feature<Meteorite.Configuration> {
    public static Set<Block> replacables;
    public static List<Block> meteorOres = List.of(ModBlocks.ORE_METEOR_IRON.get(), ModBlocks.ORE_METEOR_ALUMINIUM.get(), ModBlocks.ORE_METEOR_COPPER.get(), ModBlocks.ORE_METEOR_RAREEARTH.get(), ModBlocks.ORE_METEOR_COBALT.get());
    public Meteorite(Codec<Meteorite.Configuration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Meteorite.Configuration> context) {
        Configuration config = context.config();
        RandomSource rand = context.random();
        BlockPos blockPos = context.origin();
        WorldGenLevel level = context.level();
        if (replacables == null || replacables.isEmpty()) generateReplacables();
        if (config.damagingImpact) {
        }

        int height = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockPos.getX(), blockPos.getZ());
        blockPos = new BlockPos(blockPos.getX(), height, blockPos.getZ());

        int radius = 1;
        int typeCode = rand.nextInt(312);
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        SimpleWeightedRandomList<BlockState>[] BLOCK_OPTIONS = new SimpleWeightedRandomList[4];
        boolean flagSpecial = ConfigWorld.enableSpecialMeteors.get() && config.allowSpecials && typeCode >= 300;
        if (flagSpecial) {
            switch (typeCode){
                case 300 -> {// Meteor-only tiny meteorite
                    radius = 1;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR.get().defaultBlockState(), 10).build();
                }
                case 301 -> {// Large ore-only meteorite
                    radius = 3;
                    var builder = SimpleWeightedRandomList.<BlockState>builder();
                    for (Block block : meteorOres) {
                        builder.add(block.defaultBlockState(), 1);
                    }
                    BLOCK_OPTIONS[0] = builder.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), meteorOres.size()).build();
                }
                case 302 -> {// Medium ore-only meteorite
                    radius = 2;
                    var builder = SimpleWeightedRandomList.<BlockState>builder();
                    for (Block block : meteorOres) {
                        builder.add(block.defaultBlockState(), 1);
                    }
                    BLOCK_OPTIONS[0] = builder.add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), meteorOres.size()).build();
                }
                case 303 -> {// Small pure ore meteorite
                    radius = 1;
                    var builder = SimpleWeightedRandomList.<BlockState>builder();
                    for (Block block : meteorOres) {
                        builder.add(block.defaultBlockState(), 1);
                    }
                    BLOCK_OPTIONS[0] = builder.build();
                }
                case 304 -> {// Bamboozle

                }
                case 305 -> {// Large treasure-only meteoritew
                    radius = 3;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1)
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1)
                            .build();
                }
                case 306 -> {// Medium treasure-only meteorite
                    radius = 2;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 2)
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1)
                            .build();
                }
                case 307 -> {// Small pure treasure meteorite
                    radius = 1;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                }
                case 308 -> {// Large nuclear meteorite
                    radius = 3;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                    // 这里原本应该放毒物方块，暂时用红石块代替
                    BLOCK_OPTIONS[3] = SimpleWeightedRandomList.<BlockState>builder().add(Blocks.REDSTONE_BLOCK.defaultBlockState(), 1).build();
                }
                case 309 -> {// Giant ore meteorite
                    radius = 4;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1).build();
                    var builder = SimpleWeightedRandomList.<BlockState>builder();
                    for (Block block : meteorOres) {
                        builder.add(block.defaultBlockState(), 1);
                    }
                    BLOCK_OPTIONS[3] = builder.build();
                }
                case 310 -> {// Tainted Meteorite
                    radius = 2;
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1).build();
                    blocks.put(blockPos, ModBlocks.TAINT.get().defaultBlockState());
                }
                case 311 -> {// Star Blaster
                }
            }
        }

        if (!flagSpecial){
            if (typeCode < 100) radius = 1;
            else if (typeCode < 200) radius = 2;
            else if (typeCode >= 200) radius = 3;
            // 0 - Molten; 1 - Cobble; 2 - Broken; 3 - Mix
            int hull = rand.nextInt(4);
            // 0 - Cobble; 1 - Broken; 2 - Mix; -1 - none
            int outerPadding = radius < 2 ? -1 : hull == 2 ? 1 + rand.nextInt(2) : (hull == 3) ? 2 : 0;
            // 0 - Broken; 1 - Stone; 2 - Netherrack; -1 - none
            int innerPadding = radius < 2 ? -1 : rand.nextInt(hull == 0 ? 3 : 2);
            // 0 - Meteor; 1 - Treasure; 2 - Ore
            int core = innerPadding > 0 ? 2 : rand.nextInt(2);

            switch (rand.nextInt(4)){
                case 0:
                    outerPadding = innerPadding = -1;
                    break;
                case 1:
                    innerPadding = -1;
                    break;
                case 2:
                    outerPadding = -1;
                    break;
                case 4:
                    innerPadding = 3;
            }

            switch (hull){
                case 0:
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_MOLTEN.get().defaultBlockState(), 1).build();
                    break;
                case 1:
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState(), 1).build();
                    break;
                case 2:
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 99)
                            .add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                    break;
                case 3:
                    BLOCK_OPTIONS[0] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_MOLTEN.get().defaultBlockState(), 99)
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1).build();
                    break;
            }

            switch (outerPadding){
                case -1:break;
                case 0:
                    BLOCK_OPTIONS[1] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState(), 1).build();
                    break;
                case 1:
                    BLOCK_OPTIONS[1] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 99)
                            .add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                    break;
                case 2:
                    BLOCK_OPTIONS[1] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState(), 1)
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1).build();
                    break;
            }

            switch (innerPadding){
                case -1:break;
                case 0:
                    BLOCK_OPTIONS[2] = SimpleWeightedRandomList.<BlockState>builder()
                            .add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 99)
                            .add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                    break;
                case 1:
                    BLOCK_OPTIONS[2] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_BROKEN.get().defaultBlockState(), 1).build();
                    break;
                case 2:
                    BLOCK_OPTIONS[2] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_COBBLE.get().defaultBlockState(), 1).build();
                    break;
                case 3:
                    var builder = SimpleWeightedRandomList.<BlockState>builder();
                    for (Block block : meteorOres) {
                        builder.add(block.defaultBlockState(), 1);
                    }
                    BLOCK_OPTIONS[0] = builder.build();
                    break;
            }

            switch (core){
                case 0:
                    BLOCK_OPTIONS[3] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR.get().defaultBlockState(), 1).build();
                    break;
                case 1:
                    BLOCK_OPTIONS[3] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1).build();
                    break;
                case 2:
                    BLOCK_OPTIONS[3] = SimpleWeightedRandomList.<BlockState>builder().add(ModBlocks.BLOCK_METEOR_TREASURE.get().defaultBlockState(), 1)
                            .add(ModBlocks.BLOCK_METEOR.get().defaultBlockState(), 1).build();
                    break;
            }
        }

        float dist;
        int i = 0;
        for (int ix = -radius; ix <= radius; ix++) {
            for (int iy = -radius; iy <= radius; iy++) {
                for (int iz = -radius; iz <= radius; iz++) {
                    dist = Mth.sqrt(ix * ix + iy * iy + iz * iz);
                    if (dist < 1){
                        for (int j = 3; j >= 0; j--) {
                            if (BLOCK_OPTIONS[j] != null) {
                                i = j;
                                break;
                            }
                        }
                        if (BLOCK_OPTIONS[i] != null)blocks.putIfAbsent(blockPos.offset(ix,iy,iz), BLOCK_OPTIONS[i].getRandomValue(rand).orElse(Blocks.AIR.defaultBlockState()));
                    }else if (dist < 2){
                        for (int j = 2; j >= 0; j--) {
                            if (BLOCK_OPTIONS[j] != null) {
                                i = j;
                                break;
                            }
                        }
                        if (BLOCK_OPTIONS[i] != null)blocks.putIfAbsent(blockPos.offset(ix,iy,iz), BLOCK_OPTIONS[i].getRandomValue(rand).orElse(Blocks.AIR.defaultBlockState()));
                    }else if (dist < 3){
                        for (int j = 1; j >= 0; j--) {
                            if (BLOCK_OPTIONS[j] != null) {
                                i = j;
                                break;
                            }
                        }
                        if (BLOCK_OPTIONS[i] != null)blocks.putIfAbsent(blockPos.offset(ix,iy,iz), BLOCK_OPTIONS[i].getRandomValue(rand).orElse(Blocks.AIR.defaultBlockState()));
                    }else if (dist < radius + 1){
                        if (BLOCK_OPTIONS[0] != null)blocks.putIfAbsent(blockPos.offset(ix,iy,iz), BLOCK_OPTIONS[i].getRandomValue(rand).orElse(Blocks.AIR.defaultBlockState()));
                    }
                }
            }
        }
        // 放置陨石块
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 2);
        }

        return true;
    }

    public static void generateReplacables() {
        replacables = new HashSet<>();
        replacables.add(ModBlocks.BLOCK_METEOR.get());
        replacables.add(ModBlocks.BLOCK_METEOR_BROKEN.get());
        replacables.add(ModBlocks.BLOCK_METEOR_COBBLE.get());
        replacables.add(ModBlocks.BLOCK_METEOR_MOLTEN.get());
        replacables.add(ModBlocks.BLOCK_METEOR_TREASURE.get());
        replacables.add(ModBlocks.ORE_METEOR_IRON.get());
        replacables.add(ModBlocks.ORE_METEOR_ALUMINIUM.get());
        replacables.add(ModBlocks.ORE_METEOR_COPPER.get());
        replacables.add(ModBlocks.ORE_METEOR_RAREEARTH.get());
        replacables.add(ModBlocks.ORE_METEOR_COBALT.get());
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
