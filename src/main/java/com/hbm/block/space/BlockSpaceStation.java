package com.hbm.block.space;

import com.hbm.block.base.BlockDummyable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

// 航天版作 OrbitalStation
public class BlockSpaceStation extends BlockDummyable {
    public BlockSpaceStation(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return super.mainBlockEntity(pPos, pState);
    }
}
