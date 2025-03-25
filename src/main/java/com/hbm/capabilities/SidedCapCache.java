package com.hbm.capabilities;

import com.hbm.api.multiblock.BedLikeData;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class SidedCapCache {
    private final Map<Capability<?>,LazyOptional<?>> dirlessCaps = new IdentityHashMap<>();
    private final Map<Capability<?>,Map<Direction, LazyOptional<?>>> storedCaps = new IdentityHashMap<>();
    public void init(BedLikeData bedLikeData){

    }
}
