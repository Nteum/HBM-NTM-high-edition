package com.hbm.world.feature;

import com.hbm.block.BlockEnums;
import com.hbm.registries.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * 油气泡 Feature —— 从 1.7.10 MapGenBubble 迁移到 1.20.1
 * <p>
 * 生成扁球体（水平半径约是垂直半径的 3 倍）形状的气泡状矿脉。
 * 可选开启 fuzzy 模式使气泡边缘更自然，可选生成地面油渍痕迹。
 * <p>
 * 使用时确保 maxSize ≤ 16，否则会触发 far-chunk 错误。
 */
public class OilBubbleFeature extends Feature<OilBubbleFeature.Config> {
    public OilBubbleFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource rand = ctx.random();
        Config config = ctx.config();

        // === 1. 计算气泡参数 ===
        int radius = rand.nextInt(config.maxSize - config.minSize) + config.minSize;
        double radiusSqr = (radius * radius) / 2.0;

        int yMin = Math.max(level.getMinBuildHeight() + 1, origin.getY() - radius);
        int yMax = Math.min(level.getMaxBuildHeight() - 1, origin.getY() + radius);

        int originCX = origin.getX() >> 4;
        int originCZ = origin.getZ() >> 4;

        // === 2. 生成地下气泡 ===
        for (int bx = -16; bx <= 32; bx++) {
            for (int bz = -16; bz <= 32; bz++) {
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

                for (int by = yMin; by <= yMax; by++) {
                    pos.set(origin.getX() + bx, by, origin.getZ() + bz);

                    // 越界保护
                    int cx = pos.getX() >> 4;
                    int cz = pos.getZ() >> 4;
                    if (Math.abs(cx - originCX) > 1 || Math.abs(cz - originCZ) > 1)
                        continue;

                    BlockState existing = level.getBlockState(pos);
                    if (existing != config.replace)
                        continue;

                    double dx = bx;
                    double dy = (origin.getY() - by); // Y 方向距中心
                    double dz = bz;

                    double rSqr = dx * dx + dz * dz + dy * dy * 3; // 扁球体：Y 轴压缩 3 倍
                    if (config.fuzzy)
                        rSqr -= rand.nextDouble() * radiusSqr / 3.0;

                    if (rSqr < radiusSqr) {
                        level.setBlock(pos, config.block, 2);
                    }
                }
            }
        }

        // === 3. 可选：地面油渍痕迹 ===
        if (rand.nextBoolean()) {
            addSurfaceSpot(level, origin, rand, config, originCX, originCZ);
        }

        return true;
    }

    /**
     * 在地表生成油渍痕迹：油污泥土、死草、裂缝石头、小油坑。
     */
    private static void addSurfaceSpot(WorldGenLevel level, BlockPos origin, RandomSource rand,
                                        Config config, int originCX, int originCZ) {
        int spotCount = 150;
        int spotWidth = 7;

        for (int i = 0; i < spotCount; i++) {
            int offX = (int) (rand.nextGaussian() * spotWidth);
            int offZ = (int) (rand.nextGaussian() * spotWidth);

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            pos.set(origin.getX() + offX, 0, origin.getZ() + offZ);

            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            if (Math.abs(cx - originCX) > 1 || Math.abs(cz - originCZ) > 1)
                continue;

            // 从顶部往下找地面
            for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight() + 4; y--) {
                pos.setY(y);
                BlockState state = level.getBlockState(pos);

                if (state.isAir()) continue;
                if (!state.isSolid()) continue;

                // 找到了地面
                int distSq = offX * offX + offZ * offZ;
                boolean inner = distSq < (spotWidth / 2) * (spotWidth / 2);

                for (int oy = 1; oy > -3; oy--) {
                    pos.setY(y + oy);
                    BlockState groundState = level.getBlockState(pos);
                    BlockPos immutablePos = pos.immutable();

                    if (inner) {
                        // 中心区：油污地面
                        if (groundState == Blocks.GRASS.defaultBlockState()) {
                            level.setBlock(immutablePos, ModBlocks.PLANT_DEAD.get(BlockEnums.EnumDeadPlantType.GRASS).get().defaultBlockState(), 2);
                            break;
                        } else if (groundState == Blocks.DIRT.defaultBlockState()){
                            level.setBlock(immutablePos, ModBlocks.DIRT_OILY.get().defaultBlockState(), 2);
                            break;
                        } else if (groundState == Blocks.SAND.defaultBlockState()) {
                            level.setBlock(immutablePos, ModBlocks.SAND_DIRTY.get().defaultBlockState(), 2);
                            break;
                        } else if (groundState == Blocks.STONE.defaultBlockState()) {
                            level.setBlock(immutablePos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                            break;
                        }
                    } else {
                        // 外圈：枯死地面 + 随机死草
                        if (groundState == ModBlocks.SAND_DIRTY.get().defaultBlockState()) {
                            if (oy == 0 && rand.nextInt(20) == 0) {
                                pos.setY(y + 1);
                                if (Math.abs(pos.getX() >> 4 - originCX) <= 1
                                        && Math.abs(pos.getZ() >> 4 - originCZ) <= 1) {
                                    level.setBlock(pos.immutable(), ModBlocks.PLANT_DEAD.get(BlockEnums.EnumDeadPlantType.GRASS).get().defaultBlockState(), 2);
                                }
                            }
                            break;
                        } else if (groundState == Blocks.SAND.defaultBlockState()) {
                            level.setBlock(immutablePos, ModBlocks.SAND_DIRTY.get().defaultBlockState(), 2);
                            break;
                        } else if (groundState == Blocks.STONE.defaultBlockState()) {
                            level.setBlock(immutablePos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                            break;
                        }
                    }
                }
                break;
            }
        }

        // === 小油坑（中心洞口） ===
        for (int dir = 1; dir < 6; dir++) {
            int dx = dir == 1 ? -1 : dir == 2 ? 1 : dir == 3 ? -1 : dir == 4 ? 1 : 0;
            int dz = dir == 1 ? 0 : dir == 2 ? 0 : dir == 3 ? -1 : dir == 4 ? 1 : -1;
            if (dir == 5) { dx = 0; dz = 1; }

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            pos.set(origin.getX() + dx, 0, origin.getZ() + dz);

            int cx = pos.getX() >> 4;
            int cz = pos.getZ() >> 4;
            if (Math.abs(cx - originCX) > 1 || Math.abs(cz - originCZ) > 1)
                continue;

            int solids = 0;
            for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
                pos.setY(y);
                BlockState state = level.getBlockState(pos);
                if (state.isAir()) continue;
                if (!state.getFluidState().isEmpty()) break;

                if (state.isSolid()) {
                    solids++;
                    BlockPos immutablePos = pos.immutable();

                    if (dir == 5) {
                        // 正面洞口
                        if (solids < 3) {
                            level.setBlock(immutablePos, Blocks.AIR.defaultBlockState(), 2);
                        } else if (solids == 3) {
                            level.setBlock(immutablePos, ModBlocks.OIL_SPILL.get().defaultBlockState(), 2);
                        } else if (solids < 7) {
                            level.setBlock(immutablePos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                        } else {
                            break;
                        }
                    } else {
                        // 侧面：只做裂缝
                        level.setBlock(immutablePos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                        if (solids >= 4) break;
                    }
                }
            }
        }
    }

    // ========================================================
    // Configuration
    // ========================================================

    public record Config(
            BlockState block,         // 气泡填充方块（例如石油矿石）
            BlockState replace,       // 被替换的方块（通常是石头）
            int minSize,              // 最小半径
            int maxSize,              // 最大半径（建议 ≤ 16）
            boolean fuzzy             // 边缘模糊化
    ) implements FeatureConfiguration {

        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("block").forGetter(Config::block),
                BlockState.CODEC.fieldOf("replace").forGetter(Config::replace),
                Codec.INT.fieldOf("min_size").forGetter(Config::minSize),
                Codec.INT.fieldOf("max_size").forGetter(Config::maxSize),
                Codec.BOOL.fieldOf("fuzzy").orElse(false).forGetter(Config::fuzzy)
        ).apply(instance, Config::new));

        /**
         * 创建仅生成地下气泡（无地表痕迹）的简单配置。
         */
        public static Config simple(BlockState block, BlockState replace, int minSize, int maxSize, boolean fuzzy) {
            return new Config(block, replace, minSize, maxSize, fuzzy);
        }
    }
}
