package com.hbm.core.contents.fluid;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * 流体渲染外观 —— 纯数据 record，双端安全。
 * <p>
 * 在 {@link HbmFluidType#initializeClient} 中消费，
 * 通过 {@link IClientFluidTypeExtensions} 注入渲染管线。
 *
 * @param still      静止纹理
 * @param flowing    流动纹理
 * @param tint       着色颜色 (0xRRGGBB)，{@code -1} 表示不着色
 * @param overlay    叠加纹理（可选，如石油光泽层）
 */
public record FluidAppearance(
        ResourceLocation still,
        ResourceLocation flowing,
        int tint,
        Optional<ResourceLocation> overlay,
        ResourceLocation guiTexture
) {
    /** 不着色、无叠加的默认外观 */
    public FluidAppearance(ResourceLocation still, ResourceLocation flowing, int tint) {
        this(still, flowing, tint, Optional.empty(), null);
    }

    /** 不着色、无叠加 */
    public FluidAppearance(ResourceLocation still, ResourceLocation flowing) {
        this(still, flowing, -1);
    }
}
