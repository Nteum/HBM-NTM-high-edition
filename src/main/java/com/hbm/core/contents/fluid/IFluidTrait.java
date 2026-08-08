package com.hbm.core.contents.fluid;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 流体 Trait 的基础接口。
 * <p>
 * 所有 trait（无论是简单标签还是有数据的）都实现此接口。
 * 简单的单例标签用 {@link FluidTraits} 枚举；
 * 带数据的用 {@link TraitData} 下的 record 实现。
 */
public interface IFluidTrait {

    /**
     * 向 tooltip 追加描述信息（始终显示，在基础信息之后）。
     */
    default void addTooltip(List<Component> tooltip) {}

    /**
     * 向 tooltip 追加隐藏描述（按 Shift 时显示）。
     */
    default void addTooltipHidden(List<Component> tooltip) {}
}
