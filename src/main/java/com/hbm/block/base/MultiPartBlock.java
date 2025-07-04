package com.hbm.block.base;

import com.hbm.blockentity.base2.DummyableBlockEntity;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 对BedLikeBlock的改写
 * */
public abstract class MultiPartBlock extends BaseMachineBlock{
    protected MultiPartBlock(Properties pProperties) {
        super(pProperties.noOcclusion());
    }

    protected boolean isCore(BlockGetter level, BlockPos pos){
        DummyableBlockEntity blockEntity = WorldUtils.getTileEntity(DummyableBlockEntity.class, level, pos);
        if (blockEntity!=null)return blockEntity.isCore;
        else return false;
    }
    protected BlockPos getCore(BlockGetter level, BlockPos pos){
        DummyableBlockEntity blockEntity = WorldUtils.getTileEntity(DummyableBlockEntity.class, level, pos);
        if (blockEntity!=null)return blockEntity.corePos;
        else return null;
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    /** 被放置后的动作 */
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
//        if (!pLevel.isClientSide){
//
//        }
        DummyableBlockEntity blockEntity = WorldUtils.getTileEntity(DummyableBlockEntity.class, pLevel, pPos);
//        if (blockEntity != null) ((DummyableBlockEntity)blockEntity).onPlaced(pLevel,pPos,pState,pPlacer,pStack);
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())){
            DummyableBlockEntity blockEntity = WorldUtils.getTileEntity(DummyableBlockEntity.class, pLevel, pPos);
            if (blockEntity != null) ((DummyableBlockEntity)blockEntity).onRemove(pState,pLevel,pPos,pNewState,pMovedByPiston);
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return isCore(pLevel,pPos) ? super.getShadeBrightness(pState,pLevel,pPos) : 1.0F;
    }

    /** 获取物品占用的所有位置（相对于放置点的位置） */
    public List<Vec3i> getOffsets(){return List.of(new Vec3i(0,0,0));}
    public int[] getOffset(){return new int[]{1,0,0,1,0,1};}
}
