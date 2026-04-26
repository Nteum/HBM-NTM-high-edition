package com.hbm.blockentity.logistic;

import com.hbm.block.HBMBlockProperties;
import com.hbm.blockentity.base.BaseMenuTile;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public abstract class TileConveyorMachine extends BaseMenuTile {
    public TileConveyorMachine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void renewItemCaps(BlockState state){
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction secondaryFacing = DirectionUtils.relativeDir2Dir(facing, state.getValue(HBMBlockProperties.RELATIVE_DIRECTION));
        this.capabilitiesContent.addCapability(ForgeCapabilities.ITEM_HANDLER, items, facing, secondaryFacing);
    }

    public Direction getOutputSide(){
        return DirectionUtils.relativeDir2Dir(this.getBlockState().getValue(BlockStateProperties.FACING), this.getBlockState().getValue(HBMBlockProperties.RELATIVE_DIRECTION));
    }
}
