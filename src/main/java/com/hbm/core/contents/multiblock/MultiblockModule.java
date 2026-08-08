package com.hbm.core.contents.multiblock;

import net.minecraft.core.Vec3i;

import java.util.List;

public class MultiblockModule extends MultiblockData{
    public boolean isFormed = false;
    // 不需要序列化，每次重载都需要重新分配
    public boolean distributed = false;

    public MultiblockModule(List<Vec3i> offsets, int[] dirOffsets){
        super(offsets, dirOffsets);
    }
    public MultiblockModule(int... dirOffsets){
        super(dirOffsets);
    }

    public MultiblockModule(MultiblockData data){
        super(data);
    }
}
