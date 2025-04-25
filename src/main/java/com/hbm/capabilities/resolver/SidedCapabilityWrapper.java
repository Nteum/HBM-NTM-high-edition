package com.hbm.capabilities.resolver;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public abstract class SidedCapabilityWrapper<T> implements ICapabilityResolver{
    protected EnumMap<Direction,Boolean> directionConn = new EnumMap<>(Direction.class);
    protected LazyOptional<T> optional = LazyOptional.empty();
    protected T content = null;
    //不指定方向则所有边都有效
    public SidedCapabilityWrapper(T content){
        this.content = content;
        for (Direction value : Direction.values()) {
            directionConn.put(value,true);
        }
    }
    public SidedCapabilityWrapper(T content, Direction...directions){
        for (Direction direction : directions) {
            this.directionConn.put(direction,true);
        }
        this.content = content;
    }

    @Override
    public <R> LazyOptional<R> resolve(Capability<R> capability, @Nullable Direction side) {
        if (side == null || this.directionConn.get(side)){
            if (!this.optional.isPresent())this.optional = LazyOptional.of(()->this.content).cast();
            return this.optional.cast();
        }
        else return LazyOptional.empty();
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        if (side==null)invalidateAll();
        else directionConn.put(side,Boolean.FALSE);
    }

    @Override
    public void invalidateAll() {
        this.optional.invalidate();
    }
}
