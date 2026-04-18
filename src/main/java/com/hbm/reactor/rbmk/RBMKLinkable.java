package com.hbm.reactor.rbmk;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Common contract for RBMK devices that can be linked to a reactor column by the
 * handheld RBMK tool.
 */
public interface RBMKLinkable {

    boolean linkToColumn(BlockPos target);

    @Nullable
    BlockPos getLinkedColumn();

    Component getLinkDisplayName();
}
