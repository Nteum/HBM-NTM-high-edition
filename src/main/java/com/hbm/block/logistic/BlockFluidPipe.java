package com.hbm.block.logistic;

import com.hbm.api.Mode;
import com.hbm.block.logistic.AbstractPipeBlock;
import com.hbm.blockentity.base.BasePipeBlockEntity;
import com.hbm.blockentity.machine.PipeEntity;
import com.hbm.utils.DirectionUtils;
import com.hbm.utils.EnumUtils;
import com.hbm.utils.WorldUtils;
import com.hbm.utils.transport_net.FluidNetwork;
import com.hbm.utils.transport_net.FluidNetworkSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.GrindstoneEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockFluidPipe extends AbstractPipeBlock implements EntityBlock {
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
        PipeEntity clickPipe = (PipeEntity) pLevel.getBlockEntity(clickedPos);
        BlockEntity neighbourEntity;
        assert clickPipe != null;
        if (clickPipe.connLimit[direction.ordinal()] != Mode.BOTH) return false;
        if ((neighbourEntity = pLevel.getBlockEntity(neighbourPos)) instanceof PipeEntity pipe){
            // 既需要检查管道模式，也需要检查流体类型
            return pipe.connLimit[direction.getOpposite().ordinal()] == Mode.BOTH && (clickPipe.getFluid() == Fluids.EMPTY || clickPipe.getFluid().isSame(pipe.getFluid()));
        }else {
            return neighbourEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
        }
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
        Set<FluidNetwork> networks = new HashSet<>();
        PipeEntity pipe1 = (PipeEntity) pLevel.getBlockEntity(pPos);
        if (pipe1 == null) return;
        for (Direction direction : EnumUtils.DIRECTIONS) {
            if (pState.getValue(PROPERTY_BY_DIRECTION.get(direction)) && pLevel.getBlockEntity(pPos.relative(direction)) instanceof PipeEntity pipe && pipe.network != null){
                networks.add(pipe.network);
            }
        }
        FluidNetworkSystem fluidNetworkSystem = FluidNetworkSystem.getOrCreate(pLevel);
        if (networks.isEmpty()) fluidNetworkSystem.create(pPos);
        else {
            FluidNetwork[] networksArray = networks.toArray(FluidNetwork[]::new);
            if (pipe1.getFluid().isSame(Fluids.EMPTY))fluidNetworkSystem.join(pPos, networksArray[0]);
            if (networks.size() > 1) fluidNetworkSystem.connect(Arrays.stream(networksArray).filter(network -> network.fluid == networksArray[0].fluid).toArray(FluidNetwork[]::new));
        }
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        PipeEntity pipe1 = WorldUtils.getTileEntity(PipeEntity.class, level, pos);
        if (pipe1 == null) return;
        FluidNetworkSystem fluidNetworkSystem = FluidNetworkSystem.getOrCreate((Level) level);
        Set<FluidNetwork> addNets = new HashSet<>();
        for (Direction direction : EnumUtils.DIRECTIONS) {
            Boolean oldValue = oldState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            Boolean newValue = newState.getValue(PROPERTY_BY_DIRECTION.get(direction));
            if (!oldValue && newValue){
                PipeEntity pipe2 = WorldUtils.getTileEntity(PipeEntity.class, level, pos.relative(direction));
                if (pipe2 != null && pipe2.network != null) addNets.addAll(List.of(pipe1.network, pipe2.network));
            }else if (oldValue && !newValue){
                // 只要有地方断了就可能导致网络断裂，因此这里直接上报。
                fluidNetworkSystem.split(pipe1.network);
            }
        }
        // 如果有网络需要加入，则
        if (addNets.size() > 1) fluidNetworkSystem.connect(addNets.toArray(FluidNetwork[]::new));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        PipeEntity pipeEntity = WorldUtils.getTileEntity(PipeEntity.class, pLevel, pPos);
        if (pipeEntity != null) {
            FluidNetworkSystem fluidNetworkSystem = FluidNetworkSystem.getOrCreate(pLevel);
            fluidNetworkSystem.leave(pPos, pipeEntity.network);
            fluidNetworkSystem.split(pipeEntity.network);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }
}
