package com.hbm.block.base;

import com.hbm.blockentity.base.BaseMachineBlockEntity;
//import com.hbm.handler.MoltiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 大部分机器的父类，完成一些机器共有的工作。
 * */
public abstract class BaseMachineBlock extends BaseEntityBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected BaseMachineBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    /** 右键 */
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MenuProvider){   //如果有界面则打开界面
                pPlayer.openMenu((MenuProvider) blockEntity);
            }
            return InteractionResult.CONSUME;
        }else {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
    }

    /** 当方块被移除调用 */
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BaseMachineBlockEntity){
                if (pLevel instanceof ServerLevel){
                    /** 掉落方块中的物品 */
                    Containers.dropContents(pLevel,pPos,(BaseMachineBlockEntity) blockEntity);
                }
                pLevel.updateNeighbourForOutputSignal(pPos,this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    //必须规定为MODEL，否则渲染不出来
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
