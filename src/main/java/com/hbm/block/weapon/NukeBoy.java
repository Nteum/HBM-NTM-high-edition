package com.hbm.block.weapon;

import com.hbm.blockentity.weapon.NukeBombBoyEntity;
import com.hbm.blockentity.weapon.NukeBombFatEntity;
import com.hbm.utils.MultipartUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NukeBoy extends NukeBomb{
    public static final VoxelShape SHAPE = Block.box(-30,0,0,24,16,16);
    public NukeBoy(Properties pProperties, int range) {
        super(pProperties, range);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new NukeBombBoyEntity(pPos,pState);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return isCore(pLevel,pPos) ? SHAPE : super.getShape(pState,pLevel,pPos,pContext);
    }

    @Override
    public int[] getOffset() {
        return new int[]{0, 0, 0 ,0 ,1 ,1};
    }

    @Override
    public List<Vec3i> getOffsets() {
        return MultipartUtils.square(getOffset());
    }
//    @Override
//    public RenderShape getRenderShape(BlockState pState) {
//        return RenderShape.INVISIBLE;
//    }
}
