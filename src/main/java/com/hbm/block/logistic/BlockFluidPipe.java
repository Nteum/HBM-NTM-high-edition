package com.hbm.block.logistic;

import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.logistic.PipeEntityBEPipeBase;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.WorldUtils;
import com.hbm.core.contents.transport_net.FluidBackupSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockFluidPipe extends AbstractPipeBlock implements EntityBlock, ILookOverlay {
    public BlockFluidPipe(Properties pProperties) {
        super(pProperties, 0.1875f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PipeEntityBEPipeBase(pPos,pState);
    }

    @Override
    public boolean connectsTo(BlockPos clickedPos, LevelAccessor pLevel, Direction direction) {
        BlockPos neighbourPos = clickedPos.relative(direction);
        PipeEntityBEPipeBase clickPipe = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, pLevel, clickedPos);
        if (clickPipe != null && !clickPipe.isDirAllow(direction)) {
            return false;
        }
        BlockEntity neighbourEntity = pLevel.getBlockEntity(neighbourPos);
        if (neighbourEntity instanceof PipeEntityBEPipeBase pipe){
            // 既需要检查管道模式，也需要检查流体类型
            if (!pipe.isDirAllow(direction.getOpposite())) {
                return false;
            }
            Fluid selfFluid = clickPipe != null ? clickPipe.getFluid() : Fluids.EMPTY;
            Fluid otherFluid = pipe.getFluid();
            return selfFluid == Fluids.EMPTY || otherFluid == Fluids.EMPTY || selfFluid.isSame(otherFluid);
        }
        return neighbourEntity != null && neighbourEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).isPresent();
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        if (!(level instanceof Level serverLevel) || serverLevel.isClientSide) {
            return;
        }
        PipeEntityBEPipeBase pipe1 = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, level, pos);
        if (pipe1 == null) return;
        for (Direction direction : EnumUtils.DIRECTIONS) {
            BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
            boolean oldValue = oldState.hasProperty(property) && oldState.getValue(property);
            boolean newValue = newState.hasProperty(property) && newState.getValue(property);
            if (pipe1.network != null && pipe1.network.getParent() != null){
                FluidBackupSystem system = pipe1.network.getParent();
                if (!oldValue && newValue){
                    system.link(pos, pos.relative(direction));
                }else if (oldValue && !newValue){
                    system.cut(pos, pos.relative(direction));
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        // 核心防御 1：如果是同一个方块（只是 BlockState 的属性/连接状态变了），绝对不能触发网络 leave！
        if (pState.is(pNewState.getBlock())) {
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
            return;
        }
        if (!pLevel.isClientSide) {
            PipeEntityBEPipeBase pipeEntity = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, pLevel, pPos);
            if (pipeEntity != null && pipeEntity.network != null && pipeEntity.network.getParent() != null){
                pipeEntity.network.getParent().leave(pipeEntity);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        if (!pLevel.isClientSide && pLevel.getBlockState(pNeighborPos).hasBlockEntity()){
            PipeEntityBEPipeBase pipeTile = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, pLevel, pPos);
            BlockEntity neighbourTile = WorldUtils.getTileEntity(pLevel, pNeighborPos);
            if (pipeTile != null && neighbourTile != null && neighbourTile.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent()){
                Direction facing = DirectionUtils.posToDirection(pPos, pNeighborPos);
                if (facing != null) pLevel.setBlock(pPos, pState.updateShape(facing, pLevel.getBlockState(pNeighborPos), pLevel, pPos, pNeighborPos), 3);
                FluidBackupSystem.getOrCreate(pLevel).refresh(pipeTile);
            }
        }
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        PipeEntityBEPipeBase pipeEntity = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, level, pos);
        Fluid fluid = Fluids.EMPTY;
        if (pipeEntity != null) fluid = pipeEntity.getClientFluid();
        return List.of(
                Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.YELLOW),
                Component.translatable(fluid.getFluidType().getDescriptionId()).withStyle(ChatFormatting.WHITE)
        );
    }

    public static int getColor(BlockState state, @javax.annotation.Nullable BlockAndTintGetter level, @javax.annotation.Nullable BlockPos pos, int tintIndex){
        if (tintIndex == 1 && level != null && pos != null) {
            PipeEntityBEPipeBase be = WorldUtils.getTileEntity(PipeEntityBEPipeBase.class, level, pos);
            if (be != null) {
                return be.getFluidColor();
            }
        }
        return -1;
    }

}
