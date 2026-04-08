package com.hbm.block.machine.rbmk;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

final class RBMKMiniPanelGeometry {

    static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    static final VoxelShape WEST = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    static final VoxelShape EAST = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    private RBMKMiniPanelGeometry() {
    }

    static VoxelShape shape(Direction facing) {
        return switch (facing) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case SOUTH -> SOUTH;
            default -> SOUTH;
        };
    }
}
