package com.hbm.addational_data.entity;

import com.hbm.addational_data.AdditionalDataManager;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityAdditionalDataProvider implements ICapabilityProvider {
    private EntityAdditionalDataImpl entityData = null;
    private LazyOptional<IEntityAdditionalData> optional = LazyOptional.of(this::getEntityData);
    private EntityAdditionalDataImpl getEntityData(){
        if (entityData == null){
            entityData = new EntityAdditionalDataImpl();
        }
        return entityData;
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == AdditionalDataManager.ENTITY_DATA){
            return optional.cast();
        }
        return LazyOptional.empty();
    }
}
