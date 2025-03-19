package com.hbm.block.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CableEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockCable extends PipeBlock implements EntityBlock{
    public static float apothem;
    public BlockCable(Properties pProperties) {
        super(0.18F,pProperties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(EAST,false)
                .setValue(WEST,false)
                .setValue(NORTH,false)
                .setValue(SOUTH,false)
                .setValue(UP,false)
                .setValue(DOWN,false)
        );
        apothem = 0.18F;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(EAST,WEST,NORTH,SOUTH,UP,DOWN);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos east = clickedPos.east();
        BlockPos west = clickedPos.west();
        BlockPos north = clickedPos.north();
        BlockPos south = clickedPos.south();
        BlockPos up = clickedPos.above();
        BlockPos down = clickedPos.below();
//        BlockState eastState = level.getBlockState(east);
//        BlockState westState = level.getBlockState(west);
//        BlockState northState = level.getBlockState(north);
//        BlockState southState = level.getBlockState(south);
//        BlockState upState = level.getBlockState(up);
//        BlockState downState = level.getBlockState(down);
        return Objects.requireNonNull(super.getStateForPlacement(pContext))
                .setValue(EAST,this.connectsTo(clickedPos,level,Direction.WEST))
                .setValue(WEST,this.connectsTo(west,level,Direction.EAST))
                .setValue(NORTH,this.connectsTo(north,level,Direction.SOUTH))
                .setValue(SOUTH,this.connectsTo(south,level,Direction.NORTH))
                .setValue(UP,this.connectsTo(up,level,Direction.DOWN))
                .setValue(DOWN,this.connectsTo(down,level,Direction.UP));
    }
    /** 针对特定方向更新状态 */
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), Boolean.valueOf(connectsTo(pPos,pLevel,pDirection)));
    }
    private Direction facingDir(Direction dir){
        return switch (dir){
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }

    /** 判断相邻的线缆是否可连通 */
    public boolean connectsTo(BlockPos clickedPos, LevelAccessor pLevel,Direction direction) {
        BlockPos neighbourPos = clickedPos.relative(direction);
        BlockState state = pLevel.getBlockState(neighbourPos);
        return state.getBlock() instanceof BlockCable && pLevel.getBlockState(clickedPos).getBlock() instanceof BlockCable
                && !((CableEntity) Objects.requireNonNull(pLevel.getBlockEntity(neighbourPos))).forbidDir.contains(facingDir(direction))
                && !((CableEntity) Objects.requireNonNull(pLevel.getBlockEntity(clickedPos))).forbidDir.contains(direction)
                || state.hasBlockEntity() && pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.ENERGY).isPresent();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CableEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pBlockEntityType == ModBlockEntityType.CABLE_ENTITY.get() ? CableEntity::tick : null;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            // 螺丝刀点击对应方向使导线可连接/不连接
            if (pPlayer.getItemInHand(pHand).is(ModItems.SCREWDRIVER.get())){
                Direction hitDir = cableHitDirection(pHit.getBlockPos().getCenter(),pHit.getLocation());
                hitDir = hitDir==null?pHit.getDirection():hitDir;
                if (pLevel.getBlockEntity(pPos) instanceof CableEntity cableEntity){
                    BlockState neighbourState = pLevel.getBlockState(pPos.relative(hitDir));
                    boolean flag1 = neighbourState.is(ModBlocks.RED_CABLE.get());
                    boolean flag2 = false;
                    if (cableEntity.forbidDir.contains(hitDir)){
                        cableEntity.forbidDir.remove(hitDir);
                        flag2 = true;
                    }else {
                        cableEntity.forbidDir.add(hitDir);
                    }
                    if (flag1){
                        CableEntity neiEntity = (CableEntity)pLevel.getBlockEntity(pPos.relative(hitDir));
                        if (flag2){
                            pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),true);
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(facingDir(hitDir)),true);
                            assert neiEntity != null;
                            neiEntity.forbidDir.remove(facingDir(hitDir));
                        }else {
                            pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),false);
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(facingDir(hitDir)),false);
                        }
                        pLevel.setBlock(pPos, pState, 10);
                        pLevel.setBlock(pPos.relative(hitDir),neighbourState,10);
                    }
                }
            }
            //只有返回pass才能正常放置物品
            return InteractionResult.PASS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }
    private Direction cableHitDirection(Vec3 center,Vec3 hitPos){
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
