package com.hbm.block.machine;

import com.hbm.blockentity.machine.BaseMachineBlockEntity;
import com.hbm.blockentity.machine.DifurnaceEntity;
//import com.hbm.handler.MoltiblockHandler;
import com.hbm.modsetting.multiblock.BedLikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
/**
 * 大部分机器的父类：
 * 1. 有四个方向
 * 2. 放置时会根据玩家位置调整方向
 *
 * 参考：
 * （HBM）BlockDummable
 * */
public abstract class BaseMachineBlock extends BedLikeBlock implements EntityBlock {
    protected BaseMachineBlock(Properties pProperties) {
        super(pProperties);
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
//    @Override
//    public RenderShape getRenderShape(BlockState pState) {
//        return RenderShape.MODEL;
//    }


    //工具函数，返回一个立方体的偏移
    public static Vec3i square(int n){
        return new Vec3i(n,n,n);
    }
    //工具函数，返回一个底面为柱体的偏移
    public static Vec3i pillar(int width,int height){
        return new Vec3i(width,height,width);
    }
    //机器在Y轴上的偏移量
    public int getHeightOffset() {
        return 0;
    }
}
