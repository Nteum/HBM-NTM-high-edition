package com.hbm.capabilities.resolver;

import com.hbm.api.annotations.NothingNullByDefault;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;
//ref:mek
@NothingNullByDefault
public interface ICapabilityResolver {
    default List<Capability<?>> getSupportedCapabilities(){return List.of();}
    <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable Direction side);
    void invalidate(Capability<?> capability, @Nullable Direction side);
    default void validate(Capability<?> capability, @Nullable Direction side){};
    void invalidateAll();
}