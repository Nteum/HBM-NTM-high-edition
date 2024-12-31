package com.hbm.block.machine;

import com.hbm.blockentity.ModBlockEntityType;
import com.hbm.blockentity.machine.CableEntity;
import com.hbm.registries.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockCable extends PipeBlock implements EntityBlock{

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
                .setValue(EAST,this.connectsTo(east,level)).setValue(WEST,this.connectsTo(west,level)).setValue(NORTH,this.connectsTo(north,level))
                .setValue(SOUTH,this.connectsTo(south,level)).setValue(UP,this.connectsTo(up,level)).setValue(DOWN,this.connectsTo(down,level));
    }
    /** 针对特定方向更新状态 */
    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), Boolean.valueOf(connectsTo(pNeighborPos,pLevel)));
    }

    /** 判断相邻的线缆是否可连通 */
    public boolean connectsTo(BlockPos neighbourPos, LevelAccessor pLevel) {
        BlockState state = pLevel.getBlockState(neighbourPos);
        return state.getBlock() instanceof BlockCable || state.hasBlockEntity() && pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.ENERGY).isPresent();
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
}
