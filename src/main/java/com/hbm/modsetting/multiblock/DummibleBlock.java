package com.hbm.modsetting.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
/**
 * 空白方块，用于填充多方块结构其余的内容。
 * */
public class DummibleBlock extends Block implements EntityBlock {
    public DummibleBlock(Properties pProperties) {
        super(pProperties.noOcclusion());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        DummibleBlockEntity blockEntity = (DummibleBlockEntity)pLevel.getBlockEntity(pPos);
        return pLevel.getBlockState(blockEntity.corePos).getBlock().use(pState,pLevel,pPos,pPlayer,pHand,pHit);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        DummibleBlockEntity blockEntity = (DummibleBlockEntity)pLevel.getBlockEntity(pPos);
        pLevel.destroyBlock(blockEntity.corePos,true);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
//    }

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
}
