package com.hbm.api.energy;

import com.hbm.blockentity.interfaces.IPower;
import com.hbm.utils.EnumUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 可以记录不同方向的energyhandler
 * */
public class SidedEnergyHandler extends ProxyEnergyHandler{
    protected IEnergyHandler[] sideHandlers = new IEnergyHandler[6];
    protected int[] priorities = new int[]{0,0,0,0,0,0};
    public SidedEnergyHandler(IEnergyContainer energyContainer) {
        super(energyContainer);
    }

    public void rebuildSideData(LevelReader level, BlockPos pos){
        Direction direction;
        BlockEntity blockEntity;
        for (int i = 0; i < 6; i++) {
            direction = EnumUtils.DIRECTIONS[i];
            if ((blockEntity = level.getBlockEntity(pos.relative(direction))) == null || blockEntity.isRemoved() || (sideHandlers[i] = TransmitUtils.getEnergyCapability(blockEntity, direction.getOpposite())) == null) {
                priorities[i] = 0;
                continue;
            }
            priorities[i] = blockEntity instanceof IPower ? 2 : 1;
        }
    }

    public boolean checkNeighbourIfLoad(LevelReader level, BlockPos pos){
        return pos.getX() % 16 == 0 && sideHandlers[Direction.WEST.ordinal()] != null && level.hasChunkAt(pos.relative(Direction.WEST))
                || pos.getX() % 16 == 15 && sideHandlers[Direction.EAST.ordinal()] != null && level.hasChunkAt(pos.relative(Direction.EAST))
                || pos.getZ() % 16 == 0 && sideHandlers[Direction.NORTH.ordinal()] != null && level.hasChunkAt(pos.relative(Direction.NORTH))
                || pos.getZ() % 16 == 15 && sideHandlers[Direction.SOUTH.ordinal()] != null && level.hasChunkAt(pos.relative(Direction.SOUTH));
    }
}
