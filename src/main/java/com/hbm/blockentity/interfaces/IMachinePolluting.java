package com.hbm.blockentity.interfaces;

import com.hbm.addational_data.Pollution;
import com.hbm.registries.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

// 原版TileEntityMachinePolluting逻辑，将污染视为一种流体，可以通过流体管道运输走。
public interface IMachinePolluting {
    FluidTank getPollutionTank(Pollution.Type type);
    default void pollute(Level level, BlockPos pos, Pollution.Type type, float amount){
        FluidTank tank = getPollutionTank(type);
        if (tank == null) return;
        int fluidAmount = (int) Math.ceil(amount * 100);
        int filled = tank.fill(new FluidStack(Pollution.getPollutingFluid(type), fluidAmount), IFluidHandler.FluidAction.EXECUTE);
        if (filled < fluidAmount){
            int overflow = fluidAmount - filled;
            Pollution.increPollution(level, pos, type, overflow / 100f);
            if (level.random.nextInt(3) == 0) level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.RECORDS, 0.1f, 1.5f);
        }
    }
}
