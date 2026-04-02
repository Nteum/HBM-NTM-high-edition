package com.hbm.block.logistic;

import com.hbm.block.HBMBlockProperties;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import com.hbm.utils.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ConveyorMachineBase extends Block implements EntityBlock, IToolable {
    // 放置时面向玩家的一面是主端口
    public static final DirectionProperty MAIN_PORT_SIDE = BlockStateProperties.FACING;
    public static final IntegerProperty SECONDARY_PORT_SIDE = HBMBlockProperties.RELATIVE_DIRECTION;
    public ConveyorMachineBase(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(MAIN_PORT_SIDE, Direction.SOUTH).setValue(SECONDARY_PORT_SIDE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(MAIN_PORT_SIDE, SECONDARY_PORT_SIDE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction userFacing = pContext.getHorizontalDirection();
        return this.defaultBlockState().setValue(MAIN_PORT_SIDE, userFacing.getOpposite()).setValue(SECONDARY_PORT_SIDE, 0);
    }

    @Override
    public boolean onScrew(UseOnContext context, ToolType tool) {
        Player player = context.getPlayer();
        if (tool != ToolType.SCREWDRIVER || player.hasPose(Pose.CROUCHING)) return false;
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        Direction clickedSide = context.getClickedFace();
        Direction mainPortSide = blockState.getValue(MAIN_PORT_SIDE);
        Direction secondaryPortSide = DirectionUtils.relativeDir2Dir(mainPortSide, blockState.getValue(SECONDARY_PORT_SIDE));
        BlockState newState;
        if (clickedSide == mainPortSide || clickedSide == secondaryPortSide){   // 点击主副端口可以让主副端口互换
            newState = blockState.setValue(MAIN_PORT_SIDE, secondaryPortSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(secondaryPortSide, mainPortSide));
        }else {                                                                 // 点击空白面可以设置主端口
            newState = blockState.setValue(MAIN_PORT_SIDE, clickedSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(clickedSide, secondaryPortSide));
        }
        context.getLevel().setBlock(context.getClickedPos(), newState, 3);
        return true;
    }
}
