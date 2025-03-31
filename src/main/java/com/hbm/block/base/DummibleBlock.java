package com.hbm.block.base;

import com.hbm.blockentity.base.DummibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 空白方块，用于填充多方块结构其余的内容。
 * */
public class DummibleBlock extends Block implements EntityBlock {
    public DummibleBlock(Properties pProperties) {
        super(pProperties.noOcclusion().isViewBlocking(DummibleBlock::never));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            DummibleBlockEntity blockEntity = (DummibleBlockEntity)pLevel.getBlockEntity(pPos);
            BlockPos corePos = blockEntity.corePos;
            return pLevel.getBlockState(corePos).getBlock().use(pState,pLevel,corePos,pPlayer,pHand,pHit);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        DummibleBlockEntity blockEntity = (DummibleBlockEntity)pLevel.getBlockEntity(pPos);
        //联动移除核心方块
        if (pLevel.getBlockState(blockEntity.corePos).getBlock() instanceof BedLikeBlock coreBlock){
            pLevel.removeBlock(blockEntity.corePos,false);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (!level.isClientSide()){
            // 邻接红石信号变化则传导到中心节点
            BlockPos corePos = ((DummibleBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).corePos;
            BlockState coreBlock = level.getBlockState(corePos);
            coreBlock.onNeighborChange(level,corePos,pos);
        }
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new DummibleBlockEntity(pPos,pState);
    }

    private static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }
}
