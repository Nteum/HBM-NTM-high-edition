package com.hbm.block.machine;

import com.hbm.blockentity.machine.TileEntitySolarMirror;
import com.hbm.core.block.BlockDummyable;
import com.hbm.core.block.BlockMachineBase;
import com.hbm.core.blockentity.BEDummyable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SolarMirror extends BaseEntityBlock {
    public SolarMirror(Properties pProperties) {
        super(pProperties.noOcclusion().isViewBlocking(BlockDummyable::never).dynamicShape());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileEntitySolarMirror(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }
}
