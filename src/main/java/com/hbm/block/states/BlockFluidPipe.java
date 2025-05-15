package com.hbm.block.states;

import com.hbm.block.logistic.AbstractPipeBlock;
import com.hbm.blockentity.machine.PipeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class BlockFluidPipe extends AbstractPipeBlock implements EntityBlock {
    public BlockFluidPipe(Properties pProperties) {
        super(pProperties, 0.1875f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PipeEntity(pPos,pState);
    }
    //根据方块实体判断流体管是否需要和机器连接
    @Override
    protected boolean connBlockEntityCond(LevelAccessor pLevel, BlockState state,BlockPos blockPos, BlockPos neighbourPos) {
        if (!state.hasBlockEntity())return false;
        else if (!pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent())return false;
        else {
            IFluidHandler pipeFluid = pLevel.getBlockEntity(blockPos).getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            IFluidHandler neighFluid = pLevel.getBlockEntity(neighbourPos).getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
            FluidStack fluidInPipe = pipeFluid.getFluidInTank(0);
            if (fluidInPipe.isFluidEqual(FluidStack.EMPTY))return true;
            else {
                int num = neighFluid.getTanks();
                for (int i = 0; i < num; i++) {
                    FluidStack fluidInTank = neighFluid.getFluidInTank(i);
                    if (fluidInPipe.isFluidEqual(fluidInTank))return true;
                }
            }
        }
        return false;
    }
}
