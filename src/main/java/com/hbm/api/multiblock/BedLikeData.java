package com.hbm.api.multiblock;

import com.hbm.api.RelativeSide;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.capabilities.SidedCapCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;

//BedLikeBlock的数据
public class BedLikeData {
    public Map<Vec3i,Map<Capability<?>, List<Direction>>> capChart = new HashMap<>();

    public BedLikeData() {
    }
    public BedLikeData putCapability(Capability<?> capability){
        return putCapability(capability,new Vec3i(0,0,0), Direction.values());
    }
    public BedLikeData putCapability(Capability<?> capability, Vec3i pos){
        return putCapability(capability,pos,Direction.values());
    }
    public BedLikeData putCapability(Capability<?> capability, Vec3i pos, Direction ... directions){
        capChart.computeIfAbsent(pos,k -> new HashMap<>()).put(capability, List.of(directions));
        return this;
    }

    public BedLikeData tansform(Direction direction){
        return null;
    }
}
