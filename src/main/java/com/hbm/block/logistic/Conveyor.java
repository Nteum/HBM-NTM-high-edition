package com.hbm.block.logistic;

import com.hbm.block.HBMBlockProperties;
import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import com.hbm.blockentity.base.UpdateableBlockEntity;
import com.hbm.blockentity.logistic.TileConveyor;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.InventoryUtils;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class Conveyor extends Block implements EntityBlock, IToolable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    // 0 - 直行 1 - 左弯 2 - 右弯 3 - 向上（常态） 4 - 向上（接底） 5 - 向上（顶部） 6 - 向下 7 - 向下（底部）
    public static final IntegerProperty VARIANT = HBMBlockProperties.VARIANT8;
    public static final
    VoxelShape SHAPE = Block.box(0,0,0,16,5,16);
    VoxelShape PILLAR = Shapes.or(Block.box(0,0,0,4,16,4), Block.box(12,0,0,16,16,4), Block.box(0,0,12,4,16,16), Block.box(12,0,12,16,16,16));
    VoxelShape PILLAR_WITH_BOARD = Shapes.or(Block.box(0,0,0,16,16,4), Block.box(0,0,12,4,16,16), Block.box(12,0,12,16,16,16));
    VoxelShape TOP = Shapes.or(Block.box(0,0,0,4,12,16), Block.box(12,0,0,16,12,16), Block.box(4, 0, 0, 12, 4, 4));
    public Conveyor(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VARIANT, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, VARIANT);
    }
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        for (Direction direction : Direction.values()) {
            Direction opposite = direction.getOpposite();
            BlockState neighbourState = level.getBlockState(pos.relative(direction));
            if (neighbourState.getBlock() instanceof Conveyor){
                Direction facing = neighbourState.getValue(FACING);
                int variant = neighbourState.getValue(VARIANT);
                Direction outputDir = getOutputDirSimple(neighbourState);
                if (direction.getAxis().isHorizontal()){
                    if (variant == 0 || variant == 1 || variant == 2){
                        if (facing.equals(direction)) return this.defaultBlockState().setValue(FACING, direction);
                        else if (outputDir.equals(opposite)) return this.defaultBlockState().setValue(FACING, opposite);
                    }else if (variant == 4){
                        if (!facing.equals(opposite)) return this.defaultBlockState().setValue(FACING, direction);
                    }else if (variant == 5 || variant == 7){
                        if (facing.equals(opposite)) return this.defaultBlockState().setValue(FACING, opposite);
                    }
                }else {
                    BlockEntity be;
                    boolean upWithConveyor = level.getBlockState(pos.relative(Direction.UP)).getBlock() instanceof Conveyor;
                    boolean downWithConveyor = level.getBlockState(pos.relative(Direction.DOWN)).getBlock() instanceof Conveyor;
                    if (!upWithConveyor && (be = level.getBlockEntity(pos.relative(Direction.UP))) != null){
                        LazyOptional<IItemHandler> lazyOptional = be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);
                        if (lazyOptional.isPresent() && lazyOptional.resolve().isPresent()){
                            upWithConveyor = InventoryUtils.insertNoCheckSlots(new ItemStack(Items.REDSTONE), lazyOptional.resolve().get(), true) > 0;
                        }
                    }
                    if (!downWithConveyor && (be = level.getBlockEntity(pos.relative(Direction.DOWN))) != null){
                        LazyOptional<IItemHandler> lazyOptional = be.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
                        if (lazyOptional.isPresent() && lazyOptional.resolve().isPresent()){
                            downWithConveyor = InventoryUtils.insertNoCheckSlots(new ItemStack(Items.REDSTONE), lazyOptional.resolve().get(), true) > 0;
                        }
                    }
                    if (variant == 3 || variant == 4 || variant == 5){
                        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, downWithConveyor ? (upWithConveyor ? 3 : 5) : 4);
                    }else if (variant == 6 || variant == 7){
                        return this.defaultBlockState().setValue(FACING, facing).setValue(VARIANT, downWithConveyor ? 6 : 7);
                    }
                }
            }
        }
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        int variant = pState.getValue(VARIANT);
        if (pNeighborState.getBlock() instanceof Conveyor){
            if (pDirection == Direction.UP && variant == 5){
                return pState.setValue(VARIANT, 3);
            }else if (pDirection == Direction.DOWN && variant == 7){
                return pState.setValue(VARIANT, 6);
            }else if (pDirection == Direction.DOWN && variant == 4){
                return pState.setValue(VARIANT, 3);
            }
        }else {
            if (pDirection == Direction.DOWN && variant == 3){
                return pState.setValue(VARIANT, 4);
            }else if (pDirection == Direction.DOWN && variant == 6){
                return pState.setValue(VARIANT, 7);
            }
        }
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    public static Direction getInputDir(BlockState state){
        Direction facing = state.getValue(FACING);
        Integer variant = state.getValue(VARIANT);
        return switch (variant){
            case 0,1,2 -> facing.getOpposite();
            case 3,4,5 -> facing;
            case 6,7 -> Direction.UP;
            default -> facing;
        };
    }
    // 为了区分放置物品与物流的区别，这个函数需要两个不同的版本。
    public static Direction getOutputDirSimple(BlockState state){
        Direction facing = state.getValue(FACING);
        Integer variant = state.getValue(VARIANT);
        // 注意放在底部的向下槽，和放在顶部的向上槽，出口不是向上和向下的。
        return switch (variant){
            case 1 -> facing.getCounterClockWise();
            case 2 -> facing.getClockWise();
            case 3,4,5 -> Direction.UP;
            case 6,7 -> Direction.DOWN;
            default -> facing;
        };
    }
    public static Direction getOutputDir(BlockState state){
        Direction facing = state.getValue(FACING);
        Integer variant = state.getValue(VARIANT);
        // 注意放在底部的向下槽，和放在顶部的向上槽，出口不是向上和向下的。
        return switch (variant){
            case 0 -> facing;
            case 1 -> facing.getCounterClockWise();
            case 2 -> facing.getClockWise();
            case 3,4 -> Direction.UP;
            case 5 -> facing;
            case 6 -> Direction.DOWN;
            case 7 -> facing;
            default -> facing;
        };
    }

    public static boolean isOrthogonal(BlockState state, Direction in){
        return in != null && (state.getValue(VARIANT) < 3 && in.getAxis().isVertical() || state.getValue(VARIANT) >= 3 && in.getAxis().isHorizontal());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        int variant = pState.getValue(VARIANT);
        // 测试中shape方向总是反的，所以改成反的
        return switch (variant){
            case 0,1,2 -> SHAPE;
            case 3 -> DirectionUtils.voxelShapeRot(PILLAR_WITH_BOARD, facing.getOpposite());
            case 4 -> DirectionUtils.voxelShapeRot(Shapes.or(PILLAR_WITH_BOARD, SHAPE), facing.getOpposite());
            case 5 -> DirectionUtils.voxelShapeRot(TOP, facing.getOpposite());
            case 6 -> PILLAR;
            case 7 -> Shapes.or(PILLAR, SHAPE);
            default -> SHAPE;
        };
    }

    @Override
    public boolean onScrew(UseOnContext context, ToolType tool) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (tool != ToolType.SCREWDRIVER) return false;
        BlockState blockState = level.getBlockState(clickedPos);
        BlockState newState;
        if (player.hasPose(Pose.CROUCHING)){
            int variant = blockState.getValue(VARIANT);
            switch (variant){
                case 2 ->{
                    if (!(level.getBlockState(clickedPos.relative(Direction.UP)).getBlock() instanceof Conveyor)) variant = 5;
                    else if (!(level.getBlockState(clickedPos.relative(Direction.DOWN)).getBlock() instanceof Conveyor)) variant = 4;
                    else variant = 3;
                }
                case 3,4,5 ->{
                    if (!(level.getBlockState(clickedPos.relative(Direction.DOWN)).getBlock() instanceof Conveyor)) variant = 7;
                    else variant = 6;
                }
                case 6,7 -> variant = 0;
                default -> variant = (variant + 1) % 8;
            }
            newState = blockState.setValue(VARIANT, variant);
        }else {
            newState = blockState.setValue(FACING, blockState.getValue(FACING).getCounterClockWise());
        }
        level.setBlock(clickedPos, newState, 3);
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TileConveyor(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? UpdateableBlockEntity::clientTicker : UpdateableBlockEntity::serverTicker;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof TileConveyor conveyor) {
                Containers.dropContents(pLevel, pPos, new RecipeWrapper(conveyor.getItems()));
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }
    // 传送带被右键后会掉出其中的物品
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND && !pPlayer.hasPose(Pose.CROUCHING)){
            TileConveyor tileConveyor = WorldUtils.getTileEntity(TileConveyor.class, pLevel, pPos);
            if (tileConveyor != null){
                tileConveyor.onLeftClick(pLevel, pPos, pState);
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
}
