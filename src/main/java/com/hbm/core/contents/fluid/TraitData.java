package com.hbm.core.contents.fluid;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Map;

/**
 * 带数据的流体 Trait —— 全部用不可变 record 表示。
 * <p>
 * 用法示例：
 * <pre>{@code
 * fluidType.withData(new TraitData.Corrosion(75));
 * fluidType.withData(new TraitData.Flammable(200_000));
 * fluidType.withData(new TraitData.Heatable(...));
 * }</pre>
 * 查询示例：
 * <pre>{@code
 * TraitData.Corrosion c = fluidType.getData(TraitData.Corrosion.class);
 * if (c != null && c.rating() > 50) { ... }
 * }</pre>
 */
public final class TraitData {

    private TraitData() {}

    // ============================================================
    // 腐蚀性
    // ============================================================
    /**
     * @param rating 0-100，>50 为强腐蚀
     */
    public record Corrosion(int rating) implements IFluidTrait {
        public boolean isStrong() { return rating > 50; }

        @Override
        public void addTooltip(List<Component> tooltip) {
            String text = isStrong() ? "Strongly Corrosive" : "Corrosive";
            ChatFormatting color = isStrong() ? ChatFormatting.GOLD : ChatFormatting.YELLOW;
            tooltip.add(Component.literal("[" + text + "]").withStyle(color));
        }
    }

    // ============================================================
    // 可燃（火焰燃烧，提供热值）
    // ============================================================
    /**
     * @param heatPerBucket 每桶（1000mB）产生的热能 (TU)
     */
    public record Flammable(long heatPerBucket) implements IFluidTrait {
        @Override
        public void addTooltip(List<Component> tooltip) {
            tooltip.add(Component.literal("[Flammable]").withStyle(ChatFormatting.YELLOW));
            if (heatPerBucket > 0)
                tooltip.add(Component.literal("Provides " + formatNumber(heatPerBucket) + " TU per bucket")
                        .withStyle(ChatFormatting.RED));
        }
    }

    // ============================================================
    // 可燃烧（燃料，分等级，提供 HE）
    // ============================================================
    public enum FuelGrade {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        AERO("Aviation"),
        GAS("Gaseous");

        private final String displayName;
        FuelGrade(String name) { this.displayName = name; }
        public String getDisplayName() { return displayName; }
    }

    /**
     * @param energyPerBucket 每桶产生的 HE
     * @param grade 燃料等级
     */
    public record Combustible(long energyPerBucket, FuelGrade grade) implements IFluidTrait {
        @Override
        public void addTooltip(List<Component> tooltip) {
            tooltip.add(Component.literal("[Combustible]").withStyle(ChatFormatting.GOLD));
            if (energyPerBucket > 0)
                tooltip.add(Component.literal("Provides " + formatNumber(energyPerBucket) + " HE per bucket")
                        .withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("Fuel grade: " + grade.getDisplayName())
                    .withStyle(ChatFormatting.RED));
        }
    }

    // ============================================================
    // 可加热（锅炉、PWR、换热器等）
    // ============================================================
    public enum HeatingType {
        BOILER,
        HEAT_EXCHANGER,
        PWR,
        FUSION_COOLANT;

        public static record Efficiency(HeatingType type, double factor) {}
    }

    /**
     * @param steps 加热步骤（按温度升序排列）
     * @param efficiencies 各加热器类型的效率
     */
    public record Heatable(List<HeatingStep> steps, List<HeatingType.Efficiency> efficiencies) implements IFluidTrait {
        public record HeatingStep(int heatRequired, int inputMb, String outputFluid, int outputMb) {}

        public HeatingStep getFirstStep() { return steps.get(0); }
        public double getEfficiency(HeatingType type) {
            return efficiencies.stream()
                    .filter(e -> e.type() == type)
                    .map(HeatingType.Efficiency::factor)
                    .findFirst().orElse(0.0);
        }

        @Override
        public void addTooltipHidden(List<Component> tooltip) {
            var first = getFirstStep();
            tooltip.add(Component.literal("Thermal capacity: " + first.heatRequired + " TU per " + first.inputMb + "mB")
                    .withStyle(ChatFormatting.RED));
            for (var eff : efficiencies) {
                if (eff.factor() > 0)
                    tooltip.add(Component.literal("[" + eff.type().name() + "] Efficiency: " + (int)(eff.factor() * 100) + "%")
                            .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    // ============================================================
    // 可冷却（涡轮蒸汽、换热器）
    // ============================================================
    public record Coolable(String outputFluid, int inputMb, int outputMb, int heatPerCycle,
                           List<HeatingType.Efficiency> efficiencies) implements IFluidTrait {

        public double getEfficiency(HeatingType type) {
            return efficiencies.stream()
                    .filter(e -> e.type() == type)
                    .map(HeatingType.Efficiency::factor)
                    .findFirst().orElse(0.0);
        }

        @Override
        public void addTooltipHidden(List<Component> tooltip) {
            tooltip.add(Component.literal("Thermal capacity: " + heatPerCycle + " TU per " + inputMb + "mB")
                    .withStyle(ChatFormatting.RED));
            for (var eff : efficiencies) {
                if (eff.factor() > 0)
                    tooltip.add(Component.literal("[" + eff.type().name() + "] Efficiency: " + (int)(eff.factor() * 100) + "%")
                            .withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    // ============================================================
    // 污染
    // ============================================================
    public enum PollutionType { SOOT, HEAVY_METAL, RADIOACTIVE_DUST, ACID_RAIN }

    public record Polluting(Map<PollutionType, Float> onSpill,
                            Map<PollutionType, Float> onBurn) implements IFluidTrait {
        @Override
        public void addTooltipHidden(List<Component> tooltip) {
            tooltip.add(Component.literal("[Polluting]").withStyle(ChatFormatting.GOLD));
            for (var entry : onSpill.entrySet())
                tooltip.add(Component.literal("  When spilled: " + entry.getValue() + " " + entry.getKey() + "/mB")
                        .withStyle(ChatFormatting.GREEN));
            for (var entry : onBurn.entrySet())
                tooltip.add(Component.literal("  When burned: " + entry.getValue() + " " + entry.getKey() + "/mB")
                        .withStyle(ChatFormatting.RED));
        }
    }

    // ============================================================
    // 毒素
    // ============================================================
    public sealed interface ToxinEffect permits ToxinEffect.Damage, ToxinEffect.MobEffects {
        void apply(LivingEntity entity, double intensity);
        record Damage(DamageSource source, float amount, int tickDelay,
                      boolean needsHazmat, String maskType) implements ToxinEffect {
            public void apply(LivingEntity entity, double intensity) {
                if (tickDelay > 0 && entity.level().getGameTime() % tickDelay != 0) return;
                entity.hurt(source, (float) (amount * intensity));
            }
        }

        record MobEffects(List<MobEffectInstance> effects, boolean needsHazmat,
                          String maskType) implements ToxinEffect {
            public void apply(LivingEntity entity, double intensity) {
                for (var eff : effects) {
                    entity.addEffect(new MobEffectInstance(
                            eff.getEffect(), (int) (eff.getDuration() * intensity),
                            eff.getAmplifier(), eff.isAmbient(), eff.isVisible()));
                }
            }
        }
    }

    public record Toxin(List<ToxinEffect> effects) implements IFluidTrait {
        public void affect(LivingEntity entity, double intensity) {
            for (var effect : effects) {
                effect.apply(entity, intensity);
            }
        }

        @Override
        public void addTooltipHidden(List<Component> tooltip) {
            tooltip.add(Component.literal("[Toxin]").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    // ============================================================
    // 其它
    // ============================================================

    /** 辐射排放 */
    public record VentRadiation(float radPerMb) implements IFluidTrait {
        @Override
        public void addTooltip(List<Component> tooltip) {
            tooltip.add(Component.literal("[Radioactive Vent] " + radPerMb + " RAD/mB")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }

    /** 火箭燃料 */
    public record RocketFuel(float thrust, float isp) implements IFluidTrait {
        @Override
        public void addTooltip(List<Component> tooltip) {
            tooltip.add(Component.literal("[Rocket Fuel]")
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    /** PWR 中子慢化剂 */
    public record PWRModerator(float moderationFactor) implements IFluidTrait {}

    /** 信息素（蜜蜂相关） */
    public record Pheromone(float strength) implements IFluidTrait {}

    /** 环境改造 */
    public record Terraformer() implements IFluidTrait {}

    // ============================================================
    // 工具
    // ============================================================
    private static String formatNumber(long n) {
        if (n >= 1_000_000_000) return (n / 1_000_000_000f) + "G";
        if (n >= 1_000_000) return (n / 1_000_000f) + "M";
        if (n >= 1_000) return (n / 1_000f) + "K";
        return String.valueOf(n);
    }
}
