package com.hbm.core.contents.fluid;

import com.hbm.space.dim.CelestialBody;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;

public class FluidHelper {
    // Venting gases into the atmosphere
    public static void release(Level world, Fluid type, double mB) {
        if(world.isClientSide) return;

        boolean isGas = false;
        if (type.getFluidType() instanceof HbmFluidType hbmFluidType){
            isGas = hbmFluidType.hasTrait(FluidTraits.GASEOUS) || hbmFluidType.hasTrait(FluidTraits.GASEOUS_AT_ROOM_TEMP);
        }

        if(!isGas) return;

        CelestialBody.emitGas(world, type, mB);
    }

    // Extracting gases from the atmosphere
    public static void capture(Level world, Fluid type, double mB) {
        if(world.isClientSide) return;


        boolean isGas = false;
        if (type.getFluidType() instanceof HbmFluidType hbmFluidType){
            isGas = hbmFluidType.hasTrait(FluidTraits.GASEOUS) || hbmFluidType.hasTrait(FluidTraits.GASEOUS_AT_ROOM_TEMP);
        }
        if(!isGas) return;

        CelestialBody.consumeGas(world, type, mB);
    }
}
