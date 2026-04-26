package com.hbm.block.logistic;

import com.hbm.api.Mode;
import com.hbm.block.interfaces.ILookOverlay;
import com.hbm.blockentity.machine.PipeEntity;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.WorldUtils;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockFluidPipe extends AbstractPipeBlock implements EntityBlock, ILookOverlay {
    public BlockFluidPipe(Properties pProperties) {
        super(pProperties, 0.1875f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PipeEntity(pPos,pState);
    }

    @Override
    public boolean connectsTo(BlockPos clickedPos, LevelAccessor pLevel, Direction direction) {
        BlockPos neighbourPos = clickedPos.relative(direction);
        PipeEntity clickPipe = WorldUtils.getTileEntity(PipeEntity.class, pLevel, clickedPos);
        if (clickPipe != null && !clickPipe.isDirAllow(direction)) {
            return false;
        }
        BlockEntity neighbourEntity = pLevel.getBlockEntity(neighbourPos);
        if (neighbourEntity instanceof PipeEntity pipe){
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
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        if (pLevel.isClientSide) {
            return;
        }
        FluidNetworkSystem.getOrCreate(pLevel).rebuildNetwork(pPos);
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        if (!(level instanceof Level serverLevel) || serverLevel.isClientSide) {
            return;
        }
        PipeEntity pipe1 = WorldUtils.getTileEntity(PipeEntity.class, level, pos);
        if (pipe1 == null) return;
        FluidNetworkSystem fluidNetworkSystem = FluidNetworkSystem.getOrCreate(serverLevel);
        for (Direction direction : EnumUtils.DIRECTIONS) {
            BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
            boolean oldValue = oldState.hasProperty(property) && oldState.getValue(property);
            boolean newValue = newState.hasProperty(property) && newState.getValue(property);
            if (!oldValue && newValue){
                fluidNetworkSystem.rebuildNetwork(pos);
            }else if (oldValue && !newValue){
                if (pipe1.network != null) {
                    fluidNetworkSystem.split(pipe1.network);
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (!pLevel.isClientSide) {
            FluidNetworkSystem fluidNetworkSystem = FluidNetworkSystem.getOrCreate(pLevel);
            fluidNetworkSystem.leave(pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public List<Component> getDesc(Level level, BlockPos pos) {
        PipeEntity pipeEntity = WorldUtils.getTileEntity(PipeEntity.class, level, pos);
        Fluid fluid = Fluids.EMPTY;
        if (pipeEntity != null) fluid = pipeEntity.getFluid();
        return List.of(
                Component.translatable(this.getDescriptionId()).withStyle(ChatFormatting.YELLOW),
                Component.translatable(fluid.getFluidType().getDescriptionId()).withStyle(ChatFormatting.WHITE)
        );
    }
}
