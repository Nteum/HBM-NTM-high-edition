package com.hbm.block.logistic;

import com.hbm.api.Mode;
import com.hbm.block.interfaces.IToolable;
import com.hbm.block.interfaces.ToolType;
import com.hbm.blockentity.machine.PipeEntity;
import com.hbm.capabilities.network.ConnType;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModBlocks;
import com.hbm.registries.ModItems;;
import com.hbm.utils.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class AbstractPipeBlock extends PipeBlock implements EntityBlock, IToolable {
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
        BlockState neighbourState = pLevel.getBlockState(neighbourPos);
        return pLevel.getBlockState(clickedPos).getBlock() instanceof AbstractPipeBlock ?
                ((BasePipeBlockEntity) Objects.requireNonNull(pLevel.getBlockEntity(clickedPos))).isDirAllow(direction)
                        && ((neighbourState.getBlock() instanceof AbstractPipeBlock && ((BasePipeBlockEntity) Objects.requireNonNull(pLevel.getBlockEntity(neighbourPos))).isDirAllow(direction.getOpposite())) || connBlockEntityCond(pLevel,neighbourState,clickedPos,neighbourPos))
                : connBlockEntityCond(pLevel,neighbourState,clickedPos,neighbourPos);
    }
    /** 子类自定义的管道连接限制 */
    protected boolean connBlockEntityCond(LevelAccessor pLevel, BlockState state, BlockPos blockPos, BlockPos neighbourPos){return true;}

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? BasePipeBlockEntity::clientTicker : BasePipeBlockEntity::serverTicker;
    }

    @Override
    public boolean onScrew(UseOnContext context, ToolType tool) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (context.getHand() == InteractionHand.MAIN_HAND && level.getBlockEntity(pos) instanceof BasePipeBlockEntity pipeEntity){
            Direction hitDir = cableHitDirection(pos.getCenter(),context.getClickLocation());
            hitDir = hitDir==null ? context.getClickedFace() : hitDir;
            //更新本方块状态
            boolean newDirAllowState = !pipeEntity.isDirAllow(hitDir);
            pipeEntity.setDirAllow(hitDir, newDirAllowState);
            pipeEntity.setChanged();
            BlockState blockState = level.getBlockState(pos);
            // 更新邻居状态
            BlockPos neighbourPos = pos.relative(hitDir);
            BlockState neighbourState = level.getBlockState(neighbourPos);
            BlockEntity neighbourEntity = level.getBlockEntity(neighbourPos);
            BlockState oldState;
            if (neighbourEntity instanceof BasePipeBlockEntity neighbourPipeEntity){
                neighbourPipeEntity.setDirAllow(hitDir.getOpposite(), newDirAllowState);
                neighbourPipeEntity.setChanged();
                oldState = level.getBlockState(neighbourPos);
                if (!newDirAllowState) neighbourState = neighbourState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(hitDir.getOpposite()), false);
                else neighbourState = neighbourState.updateShape(hitDir.getOpposite(), blockState, level, neighbourPos, pos);
                level.setBlock(neighbourPos, neighbourState, 3);
                level.sendBlockUpdated(neighbourPos, oldState, neighbourState, 3);
            }
            oldState = level.getBlockState(pos);
            if (!newDirAllowState) blockState = blockState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(hitDir), false);
            else blockState = blockState.updateShape(hitDir, neighbourState, level, pos, neighbourPos);
            level.setBlock(pos, blockState, 3);
            level.sendBlockUpdated(pos, oldState, blockState, 3);
            return true;
        }
        return false;
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
