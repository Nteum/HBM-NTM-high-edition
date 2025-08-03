package com.hbm.capabilities.resolver;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public abstract class SidedCapabilityWrapper<T> implements ICapabilityResolver, Cloneable{
    public EnumMap<Direction,Boolean> directionConn = new EnumMap<>(Direction.class);
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
            this.directionConn.put(direction, true);
        }
        this.content = content;
    }
    public void setAllowDirection(Direction ... directions){
        for (Map.Entry<Direction, Boolean> entry : this.directionConn.entrySet()) {
            entry.setValue(false);
        }
        for (Direction direction : directions) {
            this.directionConn.put(direction,true);
        }
    }

    @Override
    public <R> LazyOptional<R> resolve(Capability<R> capability, @Nullable Direction side) {
        if (side == null || this.directionConn.getOrDefault(side,false)){
            if (!this.optional.isPresent())this.optional = LazyOptional.of(()->this.content).cast();
            return this.optional.cast();
        }
        else return LazyOptional.empty();
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable Direction side) {
        if (side==null)invalidateAll();
        else directionConn.put(side,false);
    }

    @Override
    public void validate(Capability<?> capability, @Nullable Direction side) {
        if (side == null){
            this.optional = LazyOptional.of(()->this.content).cast();
        }else directionConn.put(side,true);
    }

    @Override
    public void invalidateAll() {
        this.optional.invalidate();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Object clone = super.clone();
        ((SidedCapabilityWrapper<?>)clone).directionConn = this.directionConn.clone();
        return clone;
    }
}
