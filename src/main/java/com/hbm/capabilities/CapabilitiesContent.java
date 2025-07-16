package com.hbm.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// 修改版的CapabilitiesCache
// 尽管我现在多少清楚了能力运作的规律，然而mek的系统还是过于复杂使我不知道该做什么
// 因此我从零开始只添加我需要的功能
/**
 * 1. 存储所有能力的LazyOptional
 * 2. 对能力进行加载和存储（似乎对于直接继承能力接口的方块实体来说，能力数据的存储和加载是方块实体负责的，而非这个类负责的）
 * 3. 能力的invalidate
 * */
public class CapabilitiesContent {
    // 存储所有能力需要的handler
    private final Map<Capability<?>, Object> handlerMap = new IdentityHashMap<>();
    private final Map<Capability<?>, LazyOptional<?>> lazyOptionalMap = new IdentityHashMap<>();
    // 能力通过方块面访问的情况，注意：这里的方向仅仅是能否访问能力，而不代表哪个面可以访问能力内部的特殊容器
    // 方向信息需要序列化
    private final Map<Capability<?>, List<Direction>> sideMap = new IdentityHashMap<>();
    // 添加能力
    // 方向要么所有面都可以，要么不可从外界访问
    public <T>void addCapability(Capability<T> capability, T handler){
        if (!lazyOptionalMap.containsKey(capability)){
            handlerMap.put(capability, handler);
            lazyOptionalMap.put(capability, LazyOptional.of(() -> handler));
            sideMap.put(capability, List.of(Direction.values()));
//            if (external)
//                sideMap.put(capability, List.of(Direction.values()));
//            else
//                sideMap.put(capability, List.of());
        }
    }
    // 添加能力，但可以指明方向
    public <T>void addCapability(Capability<T> capability, T handler, Direction ... sides){
        if (!lazyOptionalMap.containsKey(capability)){
            handlerMap.put(capability, handler);
            lazyOptionalMap.put(capability, LazyOptional.of(() -> handler));
            sideMap.put(capability, List.of(sides));
        }
    }
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side){
        if (lazyOptionalMap.containsKey(capability) && (side == null || sideMap.get(capability).contains(side))){
            Object object;
            LazyOptional<?> lazyOptional = lazyOptionalMap.get(capability);
            if (lazyOptional.isPresent())
                // 如果lazyoptional存在或未失效则直接返回
                return lazyOptional.cast();
            else if ((object = handlerMap.get(capability)) != null){
                // 如果已失效则重新创建
                LazyOptional<Object> optional2 = LazyOptional.of(() -> object);
                lazyOptionalMap.put(capability, optional2);
                return optional2.cast();
            }
        }
        return LazyOptional.empty();
    }
    public <T>void invalidate(Capability<T> capability){
        if (lazyOptionalMap.containsKey(capability)){
            lazyOptionalMap.get(capability).invalidate();
        }
    }
    public void invalidateAll(){
        lazyOptionalMap.forEach((capability,optional) -> optional.invalidate());
    }
//    @Override
//    public CompoundTag serializeNBT() {
//        return null;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt) {
//
//    }
}
