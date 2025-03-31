package com.hbm.block.machine;

import com.google.common.collect.ImmutableMap;
import com.hbm.block.base.BaseMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 目前的渲染问题：
 * 1. 和砧相接的地面还是渲染不出来
 * 2. 砧模型出现的位置在不在方块中央，而在交界处
 * 3. 砧上有不自然的阴影
 * */
public class BlockAnvil extends BaseMachineBlock {
    public static final VoxelShape SHAPE_ZAXIS = Block.box(4.0D,0.0D,0.0D,12.0D,13.0D,16.0D);
    public static final VoxelShape SHAPE_XAXIS = Block.box(0.0D,0.0D,4.0D,16.0D,13.0D,12.0D);
    public BlockAnvil(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING).getAxis()== Direction.Axis.Z?SHAPE_ZAXIS:SHAPE_XAXIS;
    }

    //    @Override
//    protected ImmutableMap<BlockState, VoxelShape> getShapeForEachState(Function<BlockState, VoxelShape> pShapeGetter) {
//        return super.getShapeForEachState(pShapeGetter);
//    }


}
