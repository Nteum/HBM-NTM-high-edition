package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CrucibleEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
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

import java.util.List;

public class BlockCrucible extends BedLikeBlock {
    public static VoxelShape SHAPE = Block.box(-16,0,-16,32,24,32);
    public BlockCrucible(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrucibleEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntityType.CRUCIBLE_ENTITY.get() ? DifurnaceEntity::tick : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    @Override
    public int[] getOffset() {
        return new int[] {1, 0, 1, 1, 1, 1};
    }

    @Override
    public List<Vec3i> getOffsets() {
        return square(getOffset());
    }
}
