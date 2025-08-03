package com.hbm.block.base;

import com.hbm.blockentity.base2.BaseMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

//单方块机器
//对应BaseMachineBlockEntity
public abstract class BlockMachineBase extends BlockContainerBase{
    public BlockMachineBase(Properties pProperties) {
        super(pProperties.randomTicks());
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? BaseMachineBlockEntity::clientTicker : BaseMachineBlockEntity::serverTicker;
    }
    /** 右键 */
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()&& pPlayer.getPose().equals(Pose.CROUCHING)){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MenuProvider){   //如果有界面则打开界面
                pPlayer.openMenu((MenuProvider) blockEntity);
            }
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.SUCCESS;
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

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
