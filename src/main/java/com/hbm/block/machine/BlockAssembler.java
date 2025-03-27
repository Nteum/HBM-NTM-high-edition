package com.hbm.block.machine;

import com.hbm.block.base.BaseMachineBlock;
import com.hbm.block.base.BedLikeBlock;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.AssemblerEntity;
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

import static com.hbm.block.base.BedLikeBlock.square;

public class BlockAssembler extends BedLikeBlock {
    public static final VoxelShape SHAPE = Block.box(-32.0,0.0D,-32.0D,32.0D,32.0D,32.0D);
    public BlockAssembler(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AssemblerEntity(pPos,pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntityType.ASSEMBLER_ENTITY.get()?AssemblerEntity::tick : null;
    }

    @Override
    protected List<Vec3i> getOffsets() {
        //                      U  D  N  S  W  E
        return square(new int[]{1, 0, 2 ,1 ,2 ,1});
    }
}
