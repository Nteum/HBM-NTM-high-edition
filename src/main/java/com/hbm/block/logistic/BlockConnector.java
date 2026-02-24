package com.hbm.block.logistic;

import com.hbm.HBMKey;
import com.hbm.block.interfaces.IFaceAttach;
import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.TileConnector;
import com.hbm.registries.ModItems;
import com.hbm.utils.NBTUtils;
import com.hbm.utils.transport_net.EnergyNetworkSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlockConnector extends BaseEntityBlock implements IFaceAttach {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    protected static final VoxelShape UP_AABB = Block.box(5, 0, 5, 11, 9, 11);
    protected static final VoxelShape DOWN_AABB = Block.box(5, 7, 5, 11, 16, 11);
    protected static final VoxelShape SOUTH_AABB = Block.box(5, 5, 0, 11, 11, 9);
    protected static final VoxelShape NORTH_AABB = Block.box(5, 5, 7, 11, 11, 16);
    protected static final VoxelShape EAST_AABB = Block.box(0, 5, 5, 9, 11, 11);
    protected static final VoxelShape WEST_AABB = Block.box(7, 5, 5, 16, 11, 11);
    public BlockConnector(Properties pProperties) {
        super(pProperties.noOcclusion());
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction attachedDirection = getAttachedDirection(pContext);
        if (attachedDirection != null) return this.defaultBlockState().setValue(FACING, attachedDirection.getOpposite());
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)){
            case UP -> UP_AABB;
            case DOWN -> DOWN_AABB;
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            default -> super.getShape(pState, pLevel, pPos, pContext);
        };
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return (pState.getValue(FACING).getOpposite() == pDirection && !canAttach(pLevel, pPos, pDirection)) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileConnector(pPos, pState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            ItemStack itemInHand = pPlayer.getItemInHand(pHand);
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (itemInHand.is(ModItems.WIRING_RED_COPPER.get()) && !pPlayer.hasPose(Pose.CROUCHING) && blockEntity instanceof TileConnector connector){
                Optional<CompoundTag> component = NBTUtils.getSafeComponent(itemInHand.getTag(), HBMKey.POSITION);
                if (component.isPresent()){
                    BlockPos pos = NbtUtils.readBlockPos(component.get());
                    itemInHand.removeTagKey(HBMKey.POSITION);
                    connector.addConnected(pos);
                }else {
                    itemInHand.addTagElement(HBMKey.POSITION, NbtUtils.writeBlockPos(pPos));
                }
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        if (pNeighborPos.relative(pState.getValue(BlockStateProperties.FACING)).equals(pPos))
            pLevel.getBlockEntity(pPos, ModBlockEntityType.TILE_CONNECTOR.get()).ifPresent(TileConnector::neighbourChanged);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        pLevel.getBlockEntity(pPos, ModBlockEntityType.TILE_CONNECTOR.get()).ifPresent(TileConnector::onRemoveCallback);
    }

    public static Vec3 getLinkPos(final BlockPos pos, final Direction facing){
        Vec3 center = pos.getCenter();
        return switch (facing){
            case DOWN -> center.add(0, -0.0625, 0);
            case UP -> center.add(0, 0.0625, 0);
            case NORTH -> center.add(0, 0, -0.0625);
            case SOUTH -> center.add(0, 0, 0.0625);
            case WEST -> center.add(-0.0625, 0, 0);
            case EAST -> center.add(0.0625, 0, 0);
        };
    }
}

/*
  "display": {
    "gui": {
      "rotation": [ 30, 45, 180 ],
      "translation": [ -5, -3, 0 ],
      "scale": [ 1, 1, 1 ]
    },
    "ground": {
      "rotation": [ 0, 0, 0 ],
      "translation": [ 0, 3, 0 ],
      "scale": [ 0.7, 0.7, 0.7 ]
    },
    "fixed": {
      "rotation": [ 0, 0, 0 ],
      "translation": [ 0, 0, 0 ],
      "scale": [ 0.5, 0.5, 0.5 ]
    },
    "thirdperson_righthand": {
      "rotation": [ 180, 180, 90 ],
      "translation": [ 0, 2.5, 0 ],
      "scale": [ 1, 1, 1 ]
    },
    "firstperson_righthand": {
      "rotation": [ 135, 180, 0 ],
      "translation": [ 2, -0.5, -0.2 ],
      "scale": [ 1, 1, 1 ]
    },
    "head": {
      "rotation": [ 0, 180, 0 ],
      "translation": [ 0, 13, 7 ],
      "scale": [ 1, 1, 1 ]
    }
  }
* * */
