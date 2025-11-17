package com.hbm.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * RBMK 相关配置。用于驱动新的 RBMKSettings，让反应堆高度、冷却系数等可以配置。
 */
public final class ConfigRBMK {

    public static int columnHeight = 4;
    public static double passiveCooling = 1.0D;
    public static double columnHeatFlow = 0.2D;
    public static double reactivityModifier = 1.0D;

    private ConfigRBMK() {
    }

    public static void addConfig(final ForgeConfigSpec.Builder builder) {
        builder.push(CommonConfig.CATEGORY_RBMK);

        CommonConfig.addInt(builder, "columnHeight", columnHeight, 2, 16,
                "RBMK 主体的默认高度（不含盖板）。");
        builder.comment("控制被动冷却倍率，用于尚未接入外部冷却回路时的稳定度。")
                .defineInRange("passiveCooling", passiveCooling, 0.0D, 10.0D);
        builder.comment("柱体之间的热量传递效率，数值越高，热平衡越快。")
                .defineInRange("columnHeatFlow", columnHeatFlow, 0.0D, 1.0D);
        builder.comment("反应度修正系数，可用于整体增/减输出。")
                .defineInRange("reactivityModifier", reactivityModifier, 0.0D, 10.0D);

        builder.pop();
    }

    public static void loadConfig(final ModConfigEvent event) {
        final CommentedConfig root = event.getConfig().getConfigData();
        if (!root.contains(CommonConfig.CATEGORY_RBMK)) {
            return;
        }
        final Object categoryRaw = root.get(CommonConfig.CATEGORY_RBMK);
        if (!(categoryRaw instanceof CommentedConfig category)) {
            return;
        }
        columnHeight = Math.max(2, ((Number) category.get("columnHeight")).intValue());
        passiveCooling = Math.max(0.0D, ((Number) category.get("passiveCooling")).doubleValue());
        columnHeatFlow = clamp01(((Number) category.get("columnHeatFlow")).doubleValue());
        reactivityModifier = Math.max(0.0D, ((Number) category.get("reactivityModifier")).doubleValue());
    }

    private static double clamp01(final double value) {
        if (value < 0.0D) {
            return 0.0D;
        }
        if (value > 1.0D) {
            return 1.0D;
        }
        return value;
    }
}
