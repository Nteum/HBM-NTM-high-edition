package com.hbm.block.logistic;

import com.hbm.block.HBMBlockProperties;
import com.hbm.block.interfaces.*;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.blockentity.logistic.TileConveyorMachine;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class ConveyorMachineBase extends Block implements EntityBlock, IToolable, IConveyorAccess {
    // 放置时面向玩家的一面是主端口
    public static final DirectionProperty MAIN_PORT_SIDE = BlockStateProperties.FACING;
    public static final IntegerProperty SECONDARY_PORT_SIDE = HBMBlockProperties.RELATIVE_DIRECTION;
    public ConveyorMachineBase(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(MAIN_PORT_SIDE, Direction.SOUTH).setValue(SECONDARY_PORT_SIDE, 0));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? UpdateableBlockEntity::clientTicker : UpdateableBlockEntity::serverTicker;
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
        int relativeDir = blockState.getValue(SECONDARY_PORT_SIDE);
        Direction secondaryPortSide = DirectionUtils.relativeDir2Dir(mainPortSide, relativeDir);
        BlockState newState;
        if (clickedSide == mainPortSide) {
            newState = blockState.setValue(SECONDARY_PORT_SIDE, (relativeDir + 1) % 5);
        }else if (clickedSide == secondaryPortSide){
            newState = blockState.setValue(MAIN_PORT_SIDE, secondaryPortSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(secondaryPortSide, mainPortSide));
        }
        else newState = blockState.setValue(MAIN_PORT_SIDE, clickedSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(clickedSide, secondaryPortSide));
//        if (clickedSide == mainPortSide || clickedSide == secondaryPortSide){   // 点击主副端口可以让主副端口互换
//            newState = blockState.setValue(MAIN_PORT_SIDE, secondaryPortSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(secondaryPortSide, mainPortSide));
//        }else {                                                                 // 点击空白面可以设置主端口
//            newState = blockState.setValue(MAIN_PORT_SIDE, clickedSide).setValue(SECONDARY_PORT_SIDE, DirectionUtils.dir2RelativeDir(clickedSide, secondaryPortSide));
//        }
        context.getLevel().setBlock(context.getClickedPos(), newState, 3);
        TileConveyorMachine tile = WorldUtils.getTileEntity(TileConveyorMachine.class, context.getLevel(), context.getClickedPos());
        if (tile != null){
            tile.renewItemCaps(newState);
        }
        return true;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof TileConveyorMachine be) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(be.getItems()));
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && !pPlayer.getPose().equals(Pose.CROUCHING)){
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MenuProvider){   //如果有界面则打开界面
                NetworkHooks.openScreen((ServerPlayer) pPlayer, (MenuProvider) blockEntity, buf -> buf.writeBlockPos(pPos));
            }
            return InteractionResult.CONSUME;
        }else {
            return InteractionResult.SUCCESS;
        }
    }
}
