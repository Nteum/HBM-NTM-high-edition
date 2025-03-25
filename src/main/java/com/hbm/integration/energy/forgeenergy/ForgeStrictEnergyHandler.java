package com.hbm.integration.energy.forgeenergy;

import com.hbm.api.Action;
import com.hbm.api.annotations.NothingNullByDefault;
import com.hbm.api.energy.IStrictEnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
//ref:mek
//Note: When wrapping joules to a whole number based energy type we don't need to add any extra simulation steps
// for insert or extract when executing as we will always round down the number and just act upon a lower max requested amount
@NothingNullByDefault
public class ForgeStrictEnergyHandler implements IStrictEnergyHandler {

    private final IEnergyStorage storage;

    public ForgeStrictEnergyHandler(IEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public long getEnergy(int container) {
        return container == 0 ? storage.getEnergyStored() : 0L;
    }

    @Override
    public void setEnergy(int container, long energy) {
        //Not implemented or directly needed
    }

    @Override
    public long getMaxEnergy(int container) {
        return container == 0 ? storage.getMaxEnergyStored() : 0L;
    }

    @Override
    public long getNeededEnergy(int container) {
        return container == 0 ? Math.max(0, storage.getMaxEnergyStored() - storage.getEnergyStored()) : 0L;
    }

    @Override
    public long insertEnergy(int container, long amount, @NotNull Action action) {
        if (container == 0 && storage.canReceive()) {
            int toInsert = Integer.parseInt(Long.toString(amount));
            if (toInsert > 0) {
                int inserted = storage.receiveEnergy(toInsert, false);
                if (inserted > 0) {
                    //Only bother converting back if any was inserted
                    return amount - toInsert;
                }
            }
        }
        return amount;
    }

    @Override
    public long extractEnergy(int container, long amount, @NotNull Action action) {
        if (container == 0 && storage.canExtract()) {
            int toExtract = Integer.parseInt(Long.toString(amount));
            if (toExtract > 0) {
                int extracted = storage.extractEnergy(toExtract, action.simulate());
                return extracted;
            }
        }
        return 0L;
    }
}