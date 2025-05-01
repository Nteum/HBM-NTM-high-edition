package com.hbm.block.machine;

import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.machine.ChemplantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockChemplant extends BedLikeBlock {
    public static final VoxelShape SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,48.0D,32.0D);
    public BlockChemplant(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChemplantEntity(pPos,pState);
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
        return new int[]{2, 0, 2 ,1 ,2 ,1};
    }

    @Override
    public List<Vec3i> getOffsets() {
        //                      U  D  N  S  W  E
        return square(new int[]{2, 0, 2 ,1 ,2 ,1});
    }
}
