package com.hbm.block.machine;

import com.hbm.block.base.BlockDummyable;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockCrucible extends BlockDummyable {
    public BlockCrucible(Properties pProperties) {
        super(pProperties);
        shape = Block.box(-16.0,0,-16.0,16.0,24.0,16.0);
    }

    @Override
    protected BlockEntity mainBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrucibleEntity(pPos,pState);
    }
}
