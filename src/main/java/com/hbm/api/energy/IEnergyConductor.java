package com.hbm.api.energy;

import net.minecraft.core.BlockPos;

import java.util.List;

public interface IEnergyConductor {
    public List<BlockPos> getConnection();
}
