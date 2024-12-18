package com.hbm.modsetting.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
/**
 * 空白方块，用于填充多方块结构其余的内容。
 * */
public class DummibleBlock extends Block implements EntityBlock {
    public DummibleBlock(Properties pProperties) {
        super(pProperties.noOcclusion().isViewBlocking(DummibleBlock::never));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        DummibleBlockEntity blockEntity = (DummibleBlockEntity)pLevel.getBlockEntity(pPos);
        return pLevel.getBlockState(blockEntity.corePos).getBlock().use(pState,pLevel,pPos,pPlayer,pHand,pHit);
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
