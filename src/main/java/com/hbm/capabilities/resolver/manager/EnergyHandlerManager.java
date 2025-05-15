//package com.hbm.capabilities.resolver.manager;
//
//import com.hbm.api.annotations.NothingNullByDefault;
//import com.hbm.api.energy.fe.IHBMEnergyStorage;
//import com.hbm.capabilities.holder.energy.IEnergyContainerHolder;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.energy.IEnergyStorage;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//@NothingNullByDefault
//public class EnergyHandlerManager extends CapabilityHandlerManager<IEnergyContainerHolder,IHBMEnergyStorage, IEnergyStorage, ISidedEnergyStorage> {
//    public EnergyHandlerManager(@Nullable IEnergyContainerHolder holder, @NotNull ISidedEnergyStorage baseHandler) {
//        super(holder, baseHandler, ForgeCapabilities.ENERGY, ::new, IEnergyContainerHolder::getEnergyContainers);
//    }
//
//}