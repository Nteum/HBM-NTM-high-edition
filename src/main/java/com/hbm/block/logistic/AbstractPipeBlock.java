package com.hbm.block.logistic;

import com.hbm.api.Mode;
import com.hbm.capabilities.network.ConnType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPipeBlock extends PipeBlock implements EntityBlock {
    // 管道半径
    public float apothem;
    public AbstractPipeBlock(Properties pProperties, float apothem) {
        super(apothem,pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(EAST,false)
                .setValue(WEST,false)
                .setValue(NORTH,false)
                .setValue(SOUTH,false)
                .setValue(UP,false)
                .setValue(DOWN,false)
        );
        this.apothem = apothem;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(EAST,WEST,NORTH,SOUTH,UP,DOWN);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();
        return Objects.requireNonNull(super.getStateForPlacement(pContext))
                .setValue(EAST,this.connectsTo(clickedPos,level, Direction.EAST))
                .setValue(WEST,this.connectsTo(clickedPos,level,Direction.WEST))
                .setValue(NORTH,this.connectsTo(clickedPos,level,Direction.NORTH))
                .setValue(SOUTH,this.connectsTo(clickedPos,level,Direction.SOUTH))
                .setValue(UP,this.connectsTo(clickedPos,level,Direction.UP))
                .setValue(DOWN,this.connectsTo(clickedPos,level,Direction.DOWN));
    }
    /** 针对特定方向更新状态 */
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), Boolean.valueOf(connectsTo(pPos,pLevel,pDirection)));
    }

    /** 判断相邻的线缆是否可连通 */
    public boolean connectsTo(BlockPos clickedPos, LevelAccessor pLevel,Direction direction) {
        BlockPos neighbourPos = clickedPos.relative(direction);
        BlockState state = pLevel.getBlockState(neighbourPos);
        return pLevel.getBlockState(clickedPos).getBlock() instanceof AbstractPipeBlock ?
                ((BasePipeBlockEntity) Objects.requireNonNull(pLevel.getBlockEntity(clickedPos))).connLimit[direction.ordinal()]== Mode.BOTH
                        && (state.getBlock() instanceof AbstractPipeBlock && (((BasePipeBlockEntity) Objects.requireNonNull(pLevel.getBlockEntity(neighbourPos))).connLimit[direction.ordinal()]== Mode.BOTH)
                        || connBlockEntityCond(pLevel,state,clickedPos,neighbourPos))
                : connBlockEntityCond(pLevel,state,clickedPos,neighbourPos);
    }
    /** 子类自定义的管道连接限制 */
    protected boolean connBlockEntityCond(LevelAccessor pLevel, BlockState state, BlockPos blockPos, BlockPos neighbourPos){return true;}

//    public List<Direction> getConnection(){
//
//    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? BasePipeBlockEntity::clientTicker : BasePipeBlockEntity::serverTicker;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            // 螺丝刀点击对应方向使导线可连接/不连接
            if (pPlayer.getItemInHand(pHand).is(ModItems.SCREWDRIVER.get())){
                Direction hitDir = cableHitDirection(pHit.getBlockPos().getCenter(),pHit.getLocation());
                hitDir = hitDir==null?pHit.getDirection():hitDir;
                if (pLevel.getBlockEntity(pPos) instanceof BasePipeBlockEntity pipeEntity){
                    BlockState neighbourState = pLevel.getBlockState(pPos.relative(hitDir));
                    BlockEntity neighbourEntity = pLevel.getBlockEntity(pPos.relative(hitDir));
                    //更新本方块状态
                    boolean flag2 = false;
                    if (pipeEntity.connLimit[hitDir.ordinal()] == Mode.BOTH){
                        pipeEntity.connLimit[hitDir.ordinal()] = Mode.NONE;
                        pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),false);
                        flag2 = true;
                    }else {
                        pipeEntity.connLimit[hitDir.ordinal()] = Mode.BOTH;
                        pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),true);
                    }
                    pLevel.setBlock(pPos, pState, 10);
//                    BasePipeBlockEntity.updateConnCaps(pipeEntity);
                    //如果临近方块是线缆，则同时更新线缆状态
                    boolean flag1 = neighbourState.getBlock() instanceof AbstractPipeBlock;
                    if (flag1 && neighbourEntity instanceof BasePipeBlockEntity neighbourPipeEntity){
                        if (flag2){
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(hitDir.getOpposite()),false);
                            neighbourPipeEntity.connLimit[hitDir.ordinal()] = Mode.NONE;
                        }else {
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(hitDir.getOpposite()),true);
                            neighbourPipeEntity.connLimit[hitDir.ordinal()] = Mode.BOTH;
                        }
                        pLevel.setBlock(pPos.relative(hitDir),neighbourState,10);
                    }
                }
            }
//            else if (pHand.equals(InteractionHand.MAIN_HAND)&&!pPlayer.getItemInHand(pHand).is(ModBlocks.RED_CABLE.get().asItem())){
//                //右键显示连接
//                List<BlockPos> connection = ((BasePipeBlockEntity) pLevel.getBlockEntity(pPos)).getConnection();
//                pPlayer.sendSystemMessage(Component.literal(connection.toString()));
//            }
            //只有返回pass才能正常放置物品
            return InteractionResult.PASS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
    private Direction cableHitDirection(Vec3 center, Vec3 hitPos){
        Vec3 diff = hitPos.subtract(center);
        if (diff.x > apothem)return Direction.EAST;
        else if (diff.x < -apothem)return Direction.WEST;
        else if (diff.y > apothem)return Direction.UP;
        else if (diff.y < -apothem)return Direction.DOWN;
        else if (diff.z > apothem)return Direction.SOUTH;
        else if (diff.z < -apothem)return Direction.NORTH;
        else return null;
    }
}
