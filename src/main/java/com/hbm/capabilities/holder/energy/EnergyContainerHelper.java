package com.hbm.capabilities.holder.energy;

import com.hbm.api.RelativeSide;
import com.hbm.api.energy.fe.IHBMEnergyStorage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EnergyContainerHelper {

    private final IEnergyContainerHolder slotHolder;
    private boolean built;

    private EnergyContainerHelper(IEnergyContainerHolder slotHolder) {
        this.slotHolder = slotHolder;
    }

    public static EnergyContainerHelper forSide(Supplier<Direction> facingSupplier) {
        return new EnergyContainerHelper(new EnergyContainerHolder(facingSupplier));
    }

//    public static EnergyContainerHelper forSideWithConfig(Supplier<Direction> facingSupplier, Supplier<TileComponentConfig> configSupplier) {
//        return new EnergyContainerHelper(new ConfigEnergyContainerHolder(facingSupplier, configSupplier));
//    }

    public <CONTAINER extends IHBMEnergyStorage> CONTAINER addContainer(@NotNull CONTAINER container) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof EnergyContainerHolder slotHolder) {
            slotHolder.addContainer(container);
//        } else if (slotHolder instanceof ConfigEnergyContainerHolder slotHolder) {
//            slotHolder.addContainer(container);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add containers");
        }
        return container;
    }

    public <CONTAINER extends IHBMEnergyStorage> CONTAINER addContainer(@NotNull CONTAINER container, RelativeSide... sides) {
        if (built) {
            throw new IllegalStateException("Builder has already built.");
        }
        if (slotHolder instanceof EnergyContainerHolder slotHolder) {
            slotHolder.addContainer(container, sides);
        } else {
            throw new IllegalArgumentException("Holder does not know how to add containers on specific sides");
        }
        return container;
    }

    public IEnergyContainerHolder build() {
        built = true;
        return slotHolder;
    }
}