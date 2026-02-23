package com.hbm.blockentity.interfaces;

import com.hbm.utils.transport_net.EnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;
import java.util.Set;

public interface IConnector {
    EnergyNetwork getNetwork();
    void setNetwork(EnergyNetwork network);
    // 非直接连接，比如采用连接线连接
    Set<BlockPos> getConnected();
    // 面贴面连接，需要考虑对应面的功能
    List<Direction> getAttached();
}
