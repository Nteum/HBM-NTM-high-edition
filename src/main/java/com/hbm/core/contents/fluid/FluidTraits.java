package com.hbm.core.contents.fluid;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 无数据的简单流体 Trait —— 纯标签。
 * <p>
 * 用法：{@code fluidType.withTrait(FluidTraits.GASEOUS);}
 * 查询：{@code fluidType.hasTrait(FluidTraits.GASEOUS);}
 */
public enum FluidTraits implements IFluidTrait {

    /** 常温气态流体（氢、氧、天然气等） */
    GASEOUS(ChatFormatting.BLUE, "Gaseous"),

    /** 低温气态（仅在室温为气体，如液氢蒸发出的氢气） */
    GASEOUS_AT_ROOM_TEMP(ChatFormatting.AQUA, "Gaseous at Room Temperature"),

    /** 液态 */
    LIQUID(ChatFormatting.BLUE, "Liquid"),

    /** 粘稠流体，不可雾化喷涂 */
    VISCOUS(ChatFormatting.GRAY, "Viscous — cannot be sprayed"),

    /** 等离子体 */
    PLASMA(ChatFormatting.LIGHT_PURPLE, "Plasma"),

    /** 反物质 */
    ANTIMATTER(ChatFormatting.DARK_RED, "Antimatter"),

    /** 需要含铅容器才能储存 */
    REQUIRES_LEAD_CONTAINER(ChatFormatting.DARK_RED, "Requires hazardous material tank"),

    /** 不可装桶 */
    NO_CONTAINER(ChatFormatting.GRAY, "Cannot be stored in standard containers"),

    /** 美味（彩蛋） */
    DELICIOUS(ChatFormatting.DARK_GREEN, "Delicious"),

    /** 不可被虹吸管提取 */
    UNSIPHONABLE(ChatFormatting.BLUE, "Ignored by siphons"),

    /** 爆炸性 */
    EXPLOSIVE(ChatFormatting.RED, "Explosive"),

    /** 含铅燃料 */
    LEADED_FUEL(ChatFormatting.BLUE, "Leaded Fuel"),
    ;

    private final ChatFormatting color;
    private final String description;

    FluidTraits(ChatFormatting color, String description) {
        this.color = color;
        this.description = description;
    }

    @Override
    public void addTooltipHidden(List<Component> tooltip) {
        tooltip.add(Component.literal("[" + description + "]").withStyle(color));
    }
}
