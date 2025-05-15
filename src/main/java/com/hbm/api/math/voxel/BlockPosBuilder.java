//package com.hbm.api.math.voxel;
//
//import com.hbm.api.multiblock.mek.Structure;
//import net.minecraft.core.BlockPos;
//
//public class BlockPosBuilder {
//
//    private final int[] pos = new int[3];
//    private final boolean[] set = new boolean[3];
//
//    public BlockPos build() {
//        return new BlockPos(pos[0], pos[1], pos[2]);
//    }
//
//    public boolean isSet(Structure.Axis axis) {
//        return set[axis.ordinal()];
//    }
//
//    public void set(Structure.Axis axis, int value) {
//        pos[axis.ordinal()] = value;
//        set[axis.ordinal()] = true;
//    }
//
//    public int get(Structure.Axis axis) {
//        return pos[axis.ordinal()];
//    }
//}
