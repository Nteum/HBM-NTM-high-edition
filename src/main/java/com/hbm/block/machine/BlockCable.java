package com.hbm.block.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CableEntity;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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

import java.util.List;
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
        return Objects.requireNonNull(super.getStateForPlacement(pContext))
                .setValue(EAST,this.connectsTo(clickedPos,level,Direction.EAST))
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
        return pLevel.getBlockState(clickedPos).getBlock() instanceof BlockCable ?
                ((CableEntity) Objects.requireNonNull(pLevel.getBlockEntity(clickedPos))).dirType[direction.get3DDataValue()]== CableEntity.ConnType.ALLOW
                && (state.getBlock() instanceof BlockCable && (((CableEntity) Objects.requireNonNull(pLevel.getBlockEntity(neighbourPos))).dirType[direction.getOpposite().get3DDataValue()]== CableEntity.ConnType.ALLOW)
                || state.hasBlockEntity() && pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.ENERGY).isPresent())
                : state.hasBlockEntity() && pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.ENERGY).isPresent();
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
                    BlockEntity neighbourEntity = pLevel.getBlockEntity(pPos.relative(hitDir));
                    //更新本方块状态
                    boolean flag2 = false;
                    if (cableEntity.dirType[hitDir.get3DDataValue()]== CableEntity.ConnType.ALLOW){
                        cableEntity.dirType[hitDir.get3DDataValue()]= CableEntity.ConnType.FORBID;
                        if (neighbourEntity!=null && neighbourEntity.getCapability(ForgeCapabilities.ENERGY,hitDir.getOpposite()).isPresent()) {
                            pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),false);
                        }
                        flag2 = true;
                    }else {
                        cableEntity.dirType[hitDir.get3DDataValue()]= CableEntity.ConnType.ALLOW;
                        if (neighbourEntity!=null && neighbourEntity.getCapability(ForgeCapabilities.ENERGY,hitDir.getOpposite()).isPresent()) {
                            pState = pState.setValue(PROPERTY_BY_DIRECTION.get(hitDir),true);
                        }
                    }
                    pLevel.setBlock(pPos, pState, 10);
                    CableEntity.updateConnCaps(cableEntity);
                    //如果临近方块是线缆，则同时更新线缆状态
                    boolean flag1 = neighbourState.is(ModBlocks.RED_CABLE.get());
                    if (flag1 && neighbourEntity instanceof CableEntity neighbourCableEntity){
                        if (flag2){
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(hitDir.getOpposite()),false);
                            neighbourCableEntity.dirType[hitDir.getOpposite().get3DDataValue()]= CableEntity.ConnType.FORBID;
                        }else {
                            neighbourState = neighbourState.setValue(PROPERTY_BY_DIRECTION.get(hitDir.getOpposite()),true);
                            neighbourCableEntity.dirType[hitDir.getOpposite().get3DDataValue()]= CableEntity.ConnType.ALLOW;
                        }
                        pLevel.setBlock(pPos.relative(hitDir),neighbourState,10);
                        CableEntity.updateConnCaps(neighbourCableEntity);
                    }
                }
            }else if (pHand.equals(InteractionHand.MAIN_HAND)&&!pPlayer.getItemInHand(pHand).is(ModBlocks.RED_CABLE.get().asItem())){
                //右键显示连接
                List<BlockPos> connection = ((CableEntity) pLevel.getBlockEntity(pPos)).getConnection();
                pPlayer.sendSystemMessage(Component.literal(connection.toString()));
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
