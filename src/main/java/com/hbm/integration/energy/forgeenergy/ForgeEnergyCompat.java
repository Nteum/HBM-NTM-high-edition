package com.hbm.integration.energy.forgeenergy;

import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.energy.IStrictEnergyHandler;
import com.hbm.integration.energy.IEnergyCompat;
import com.hbm.utils.CapabilityUtils;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
//ref:mek
@NothingNullByDefault
public class ForgeEnergyCompat implements IEnergyCompat {

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

//    @Override
//    public Collection<CachedValue<?>> getBackingConfigs() {
//        return Set.of(MekanismConfig.general.blacklistForge);
//    }

    @Override
    public LazyOptional<IEnergyStorage> getHandlerAs(IStrictEnergyHandler handler) {
        return LazyOptional.of(() -> new ForgeEnergyIntegration(handler));
    }

    @Override
    public LazyOptional<IStrictEnergyHandler> getLazyStrictEnergyHandler(ICapabilityProvider provider, @Nullable Direction side) {
        return CapabilityUtils.getCapability(provider, getCapability(), side).lazyMap(ForgeStrictEnergyHandler::new);
    }
}