package com.hbm.space.dim;

import com.hbm.HBM;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * 航空版和噪音相关的内容放到这里
 * <br>
 * 旧HBM相关类说明：<br>
 * - PerlinNoiseSampler 基本复制的高版本 ImprovedNoise ，直接替代<br>
 * - SimplexNoiseSampler 基本复制自高版本 SimplexNoise ，直接替代<br>
 * - OctavePerlinNoiseSampler 基本复制自高版本 PerlinNoise ，直接替代，但pUseNewFactory == false<br>
 * - DoublePerlinNoiseSampler 基本复制自 NormalNoise ，直接替代<br>
 * - NoiseCaveGenerator 是对新版洞穴系统的转化，可以替代
 */
public class SpaceNoise {
    // 1. 声明 DENSITY_FUNCTION_TYPE 的 DeferredRegister
    public static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, HBM.MODID);

    // 2. 注册 VoronoiCraterDensity 的 MapCodec
    public static final RegistryObject<Codec<VoronoiCraterDensity>> VORONOI_CRATER = DENSITY_FUNCTION_TYPES.register("voronoi_crater", VoronoiCraterDensity.SERIALIZER::codec);

    public static DensityFunction createCraterTerrain(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> functions = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noises = context.lookup(Registries.NOISE);

        double heightInBlocks = 40;
        double densityScale = 2.0 / (72.0 - 56.0); // 0.125

        DensityFunctions.HolderHolder holderHolder = new DensityFunctions.HolderHolder(functions.getOrThrow(NoiseRouterData.DEPTH));
//        DensityFunction backgroundNoise = DensityFunctions.mul(holderHolder, DensityFunctions.constant(20 * heightInBlocks * densityScale));
//        // 1. 原版 JAGGED 基础小丘陵 (桦树林感)
//        Holder.Reference<DensityFunction> jaggedKey = functions.getOrThrow(NoiseRouterData.JAGGEDNESS);
//        DensityFunction baseHills = DensityFunctions.mul(new DensityFunctions.HolderHolder(jaggedKey), DensityFunctions.constant(12.0 * densityScale));

        VoronoiCraterDensity bigCraterNoise = new VoronoiCraterDensity(640);
        // 3. 对环形山进行映射，取值过高和过低都会变成0或复数，这样就可以让中间一块形成山脉
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> craterSpline =
                CubicSpline.builder(new DensityFunctions.Spline.Coordinate(Holder.direct(bigCraterNoise)))
                        .addPoint(0.0f, 0.0f)
                        .addPoint(0.1f, -0.4f)
                        .addPoint(0.2f, -0.6f)
                        .addPoint(0.7f, -0.6f)
                        .addPoint(0.8f, -0.4f)
                        .addPoint(0.9f, -0.2f)
                        .addPoint(1.0f,  0f)
                        .addPoint(1.1f,  0.5f)
                        .addPoint(1.2f,  0.7f)
                        .addPoint(1.3f,  1.0f)
                        .addPoint(1.4f,  0.3f)
                        .addPoint(1.5f,  0.0f)
                        .addPoint(2.0f,  0.0f)
                        .build();

        VoronoiCraterDensity smallCraterNoise = new VoronoiCraterDensity(80);
        // 3. 对环形山进行映射，取值过高和过低都会变成0或复数，这样就可以让中间一块形成山脉
        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> smallCraterSpline =
                CubicSpline.builder(new DensityFunctions.Spline.Coordinate(Holder.direct(smallCraterNoise)))
                        .addPoint(0.0f, -0.4f)
                        .addPoint(0.8f, -0f)
                        .addPoint(1.0f,  0.2f)
                        .addPoint(1.1f,  0f)
                        .addPoint(2.0f,  0.0f)
                        .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> smallMaskSpline =
                CubicSpline.builder(new DensityFunctions.Spline.Coordinate(Holder.direct(smallCraterNoise)))
                        .addPoint(0.0f, 0f)  // 0.0 圆心：100% 破坏大坑
                        .addPoint(0.8f, 0f)  // 1.1 山脊顶部：100% 破坏大坑
                        .addPoint(1.1f, 1f)  // 1.3 山壁外侧：平滑过渡归零
                        .addPoint(2.0f, 1f)  // 2.0 平原：完全不破坏大坑
                        .build();

        DensityFunction rawCrater = DensityFunctions.add(
                DensityFunctions.mul(
                        DensityFunctions.add(DensityFunctions.spline(craterSpline), holderHolder),
                        DensityFunctions.spline(smallMaskSpline)
                ),
                DensityFunctions.spline(smallCraterSpline)
        );
//        DensityFunction maskedLarge = DensityFunctions.mul(DensityFunctions.spline(craterSpline), DensityFunctions.spline(smallMaskSpline));

        // 1. 获取侵蚀噪声
        Holder.Reference<DensityFunction> erosionKey = functions.getOrThrow(NoiseRouterData.EROSION);
        DensityFunction erosionNoise = new DensityFunctions.HolderHolder(erosionKey);
        // 2. 将侵蚀噪声映射到 0.3 ~ 1.2 的系数范围 (部分山脊保留 120% 高度，部分山脊被侵蚀剥蚀到只剩 30%)
        DensityFunction erosionFactor = DensityFunctions.add(
                DensityFunctions.mul(erosionNoise, DensityFunctions.constant(0.45)),
                DensityFunctions.constant(0.75)
        );

        DensityFunction erosionCrater = DensityFunctions.mul(
                DensityFunctions.mul(
                        rawCrater,
                        DensityFunctions.constant(heightInBlocks * densityScale)),
                erosionFactor
        ); // 被侵蚀的环形山

        DensityFunction surfaceDensity = DensityFunctions.add(
                DensityFunctions.yClampedGradient(56, 256, 1.0, -23.0), // 基础 Y 轴切面
                erosionCrater // 缩放偏移量到密度标尺
        );

        // 6. 地下洞穴过滤：只在 Y < 55 的深层叠加洞穴，防止洞穴把地表的环形山边缘切碎
        DensityFunction underground = NoiseRouterData.underground(functions, noises, getFunction(functions, NoiseRouterData.NOODLE));

        // 7. 专门控制洞穴在 Y=45 到 Y=60 之间平滑消失（Y=45时为1.0，Y=60时为0.0）
        DensityFunction caveFade = DensityFunctions.yClampedGradient(45, 60, 1.0, 0.0);

        // 用 rangeChoice 限制：Y > 60 时完全使用地表陨石坑，Y < 50 时加入洞穴
        return DensityFunctions.add(surfaceDensity, DensityFunctions.mul(underground, caveFade)); // 地下：陨石坑 + 洞穴
    }


    /**
     * 复制自NoiseRouterData#getFunction
     * <br> 说明：通过类似functions.getOrThrow(NoiseRouterData.TEMPERATURE).get()的方式在数据生成阶段是不行的，必须先用holder包住。
     */
    public static DensityFunction getFunction(HolderGetter<DensityFunction> pDensityFunctions, ResourceKey<DensityFunction> pKey) {
        return new DensityFunctions.HolderHolder(pDensityFunctions.getOrThrow(pKey));
    }

    public record VoronoiCraterDensity(double cellSize) implements DensityFunction.SimpleFunction {
        // Codec 用于 DataGen 序列化
        public static final MapCodec<VoronoiCraterDensity> CODEC = Codec.DOUBLE.fieldOf("cell_size").xmap(VoronoiCraterDensity::new, VoronoiCraterDensity::cellSize);
        public static final KeyDispatchDataCodec<VoronoiCraterDensity> SERIALIZER = KeyDispatchDataCodec.of(CODEC);

        @Override
        public double compute(FunctionContext context) {
            int x = context.blockX();
            int z = context.blockZ();

            int minSize = (int) (cellSize * 0.125);
            int sizeRange = (int) (cellSize * 0.25);

            // 1. 计算当前点所在的网格单元 (Cell)
            long cellX = Math.floorDiv(x, (long) cellSize);
            long cellZ = Math.floorDiv(z, (long) cellSize);

            double minDistanceRatio = 2.0; // 初始化为一个较大值（超出陨石坑范围）

            // 2. 检查当前网格及相邻的 3x3 网格（防止陨石坑跨越网格边界被切断）
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    long cx = cellX + dx;
                    long cz = cellZ + dz;

                    // 使用高散列度的 Mix 算法，结合网格坐标 cx 和 cz
                    long cellHash = mixStaffwood64(cx * 0x9E3779B97F4A7C15L ^ cz * 0xBF58476D1CE4E5B9L);
                    if (cellHash % 2 != 0) continue;    // 跳过特定中心，降低一下陨石坑密度，这里是

                    // 利用带有 index 偏移的 Hash，分别生成 X偏移, Z偏移, 半径
                    double offsetX = hashToDouble(mixStaffwood64(cellHash + 101L));
                    double offsetZ = hashToDouble(mixStaffwood64(cellHash + 102L));
                    double radiusNoise = hashToDouble(mixStaffwood64(cellHash + 103L));
                    // 算出圆心坐标 (在网格 0.1 ~ 0.9 范围内变动)
                    double featureX = (cx + 0.1 + offsetX * 0.8) * cellSize;
                    double featureZ = (cz + 0.1 + offsetZ * 0.8) * cellSize;
                    // 算出真正的随机半径 (例如 20 格到 70 格之间)
                    double radius = minSize + radiusNoise * sizeRange;

                    // 计算当前采样点到圆心的欧氏距离
                    double distX = x - featureX;
                    double distZ = z - featureZ;
                    double dist = Math.sqrt(distX * distX + distZ * distZ);

                    // 计算距离与半径的比值 (0.0 = 圆心, 1.0 = 环形山壁)。加一个乘数，避免
                    double ratio = dist / radius * (0.5 + 0.5 * radiusNoise);
                    if (ratio < minDistanceRatio) {
                        minDistanceRatio = ratio;
                    }
                }
            }

            // 输出 0.0 到 1.5 之间的连续数值，直接传给 Spline 映射剖面
            return minDistanceRatio;
        }

        private static double hashRandom(long hash, int index) {
            long seed = hash + index * 1013904223L;
            seed = (seed ^ (seed >> 13)) * 1274126177L;
            return (double) ((seed ^ (seed >> 16)) & 0x7FFFFFFF) / (double) Integer.MAX_VALUE;
        }

        /**
         * Staffwood 64-bit Finalizer (雪崩混淆函数)
         * 只要输入值改变 1 个 bit，输出值的每一个 bit 都有 50% 概率改变
         */
        private static long mixStaffwood64(long z) {
            z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
            z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
            return z ^ (z >>> 31);
        }

        /**
         * 将 64 位 long Hash 安全、均匀地映射到 [0.0, 1.0) 浮点数区间
         */
        private static double hashToDouble(long hash) {
            return (double) (hash & 0x000FFFFFFFFFFFFFL) / (double) 0x0010000000000000L;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return SERIALIZER;
        }

        @Override
        public double minValue() { return 0.0; }

        @Override
        public double maxValue() { return 2.0; }
    }


    /**
     * 对应 NoiseCaveGenerator 生成的内容：
     *<br> - pillarNoise - Noises.PILLAR
     *<br> - pillarFalloffNoise - Noises.PILLAR_RARENESS
     *<br> - pillarScaleNoise - Noises.PILLAR_THICKNESS
     *<br> - caveNoise - Noises.SPAGHETTI_2D
     *<br> - horizontalCaveNoise - Noises.SPAGHETTI_2D_ELEVATION
     *<br> - caveScaleNoise - Noises.SPAGHETTI_2D_MODULATOR
     *<br> - caveFalloffNoise - Noises.SPAGHETTI_2D_THICKNESS
     *<br> - tunnelNoise1 - Noises.SPAGHETTI_3D_1
     *<br> - tunnelNoise2 - Noises.SPAGHETTI_3D_2
     *<br> - tunnelScaleNoise - Noises.SPAGHETTI_3D_RARITY
     *<br> - tunnelFalloffNoise - Noises.SPAGHETTI_3D_THICKNESS
     *<br> - offsetNoise - Noises.SPAGHETTI_ROUGHNESS
     *<br> - offsetScaleNoise - Noises.SPAGHETTI_ROUGHNESS_MODULATOR
     *<br> - terrainAdditionNoise - Noises.CAVE_LAYER
     *<br> - caveDensityNoise - Noises.CAVE_CHEESE
     */
    public static DensityFunction cave(){
        return null;
    }
}
