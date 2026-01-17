package com.hbm.reactor.pile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Base neutron cache entry. Stores the position and owning block entity so
 * streams can query additional data without repeated lookups.
 */
public abstract class NeutronNode {

    protected final BlockPos pos;
    protected final BlockEntity blockEntity;

    protected NeutronNode(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();
    }

    public BlockPos pos() {
        return pos;
    }

    public BlockEntity blockEntity() {
        return blockEntity;
    }
}
