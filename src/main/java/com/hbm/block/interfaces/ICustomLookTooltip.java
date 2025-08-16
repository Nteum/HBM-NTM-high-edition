package com.hbm.block.interfaces;

import net.minecraft.network.chat.Component;

/** 当玩家指着方块时，方块显示的内容 */
public interface ICustomLookTooltip {
    Component getLookTooltip();
}
