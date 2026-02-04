package com.hbm.block.space;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.machine.TileSpaceStaion;
import com.hbm.registries.ModBlocks;
import com.hbm.utils.multiblock.MultiblockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

// 航天版作 OrbitalStation
public class BlockSpaceStation extends BlockDummyable {
    public BlockSpaceStation(Properties pProperties) {
        super(pProperties);
        SHAPE = Shapes.box(-32, 0, -32, 32, 16, 32);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileSpaceStaion(pPos, pState);
    }
}
