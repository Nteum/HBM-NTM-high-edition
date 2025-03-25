//package com.hbm.integration.energy;
//
//import com.hbm.api.energy.IEnergyConductor;
//import com.hbm.api.energy.IEnergyContainer;
//import com.hbm.capabilities.Capabilities;
//import com.hbm.api.annotations.NothingNullByDefault;
//import com.hbm.api.energy.IStrictEnergyHandler;
//import com.hbm.utils.CapabilityUtils;
//import net.minecraft.core.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.common.util.LazyOptional;
//import org.jetbrains.annotations.Nullable;
////ref:mek
//@NothingNullByDefault
//public class StrictEnergyCompat implements IEnergyCompat {
//
//    @Override
//    public boolean isUsable() {
//        return true;
//    }
//
//    @Override
//    public Capability<IEnergyContainer> getCapability() {
//        return Capabilities.ENERGY;
//    }
//
//    @Override
//    public LazyOptional<IStrictEnergyHandler> getHandlerAs(IStrictEnergyHandler handler) {
//        return LazyOptional.of(() -> handler);
//    }
//
//    @Override
//    public LazyOptional<IStrictEnergyHandler> getLazyStrictEnergyHandler(ICapabilityProvider provider, @Nullable Direction side) {
//        return CapabilityUtils.getCapability(provider, getCapability(), side);
//    }
//}