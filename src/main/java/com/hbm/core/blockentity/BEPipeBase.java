package com.hbm.core.blockentity;

import com.hbm.HBMKey;
import com.hbm.blockentity.interfaces.IConnector;
import com.hbm.utils.math.BitUtil;
import com.hbm.core.contents.transport_net.EnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基础的管道类，仅需存储管道的连接和模式
 * 暂时不需要考虑能力
 * */
public abstract class BEPipeBase extends BEUpdateable implements IConnector {
    // 仅代表是否限制连接，禁止连接的面不会自动连接，但允许连接的面未必实际上连接了。
    // 0代表允许连接，1代表禁止连接
    private byte connLimit = 0;
    public BEPipeBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public boolean isDirAllow(Direction direction){
        return BitUtil.get(connLimit, direction.get3DDataValue(), 1) == 0;
    }

    public void setDirAllow(Direction direction, boolean value){
        this.connLimit = (byte) BitUtil.set(connLimit, direction.get3DDataValue(), 1, value ? 0 : 1);
    }

    //=== data load unload
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putByte(HBMKey.CONN_LIMIT, connLimit);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.connLimit = pTag.getByte(HBMKey.CONN_LIMIT);
    }
    // 所有连接的方向
    @Override
    public Set<BlockPos> getConnected(){
        return getAttached().stream().map(this.worldPosition::relative).collect(Collectors.toSet());
    }

    @Override
    public List<Direction> getAttached() {
        List<Direction> result = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (this.getBlockState().getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(dir))) result.add(dir);
        }
        return result;
    }

    @Override
    public EnergyNetwork getNetwork() {
        return null;
    }

    @Override
    public void setNetwork(EnergyNetwork network) {

    }
}
