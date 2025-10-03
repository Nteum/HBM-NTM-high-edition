package com.hbm.addational_data.chunk;

import com.hbm.addational_data.AdditionalDataManager;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChunkAdditionalDataProvider implements ICapabilityProvider {
    private ChunkAdditionalDataImpl chunkData = null;
    private LazyOptional<IChunkAdditionalData> optional = LazyOptional.of(this::getEntityData);
    private IChunkAdditionalData getEntityData(){
        if (chunkData == null){
            chunkData = new ChunkAdditionalDataImpl();
        }
        return chunkData;
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == AdditionalDataManager.CHUNK_DATA){
            return optional.cast();
        }
        return LazyOptional.empty();
    }
}
