package com.hbm.capabilities;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.registries.HBMCaps;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
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
    private final Map<Capability<?>, Map<Direction, LazyOptional<?>>> sideSpecialMap = new HashMap<>();
    // 添加能力
    // 默认全部方向都可以
    public <T>void addCapability(Capability<T> capability, T handler){
        if (!lazyOptionalMap.containsKey(capability)){
            handlerMap.put(capability, handler);
            lazyOptionalMap.put(capability, LazyOptional.of(() -> handler));
            sideMap.put(capability, List.of(Direction.values()));
        }
    }
    // 我希望添加capability可以覆盖原有的，但这获取会影响到他人的代码，所以单独实现一个功能。
    public <T>void forceAddCapability(Capability<T> capability, T handler, Direction ... directions){
        handlerMap.put(capability, handler);
        LazyOptional<T> lazyOptional = LazyOptional.of(() -> handler);
        lazyOptionalMap.put(capability, lazyOptional );
        sideMap.computeIfAbsent(capability, capability1 -> new ArrayList<>()).addAll(List.of(directions.length == 0 ? Direction.values() : directions));
        for (Direction direction : directions) {
            sideSpecialMap.computeIfAbsent(capability, capability1 -> new HashMap<>()).put(direction, lazyOptional);
        }
    }
    // 添加能力，但可以指明方向，除了用于添加能力，也用于添加方向
    public <T>void addCapability(Capability<T> capability, T handler, Direction ... sides){
        if (!lazyOptionalMap.containsKey(capability)){
            handlerMap.put(capability, handler);
            lazyOptionalMap.put(capability, LazyOptional.of(() -> handler));
            sideMap.put(capability, Arrays.stream(sides).toList());
        }
    }
    /** 不固定泛型添加能力，用于给TileProxy添加能力 */
    public void addCapability(Capability<?> capability, Object handler, Set<Direction> directions){
        Direction[] directionsArray = directions.toArray(new Direction[0]);
        if (directions.contains(null)) directionsArray = new Direction[]{null};
        if (capability == HBMCaps.LONG_ENERGY && handler instanceof IEnergyHandler
                || capability == ForgeCapabilities.FLUID_HANDLER && handler instanceof IFluidHandler
                || capability == ForgeCapabilities.ITEM_HANDLER && handler instanceof IItemHandler){
            plusCapability(capability, handler, directionsArray);
        }
    }
    /** 沟槽的java泛型，我加个无泛型的addCapability竟然显示冲突，因此只能把命名稍作调整。 */
    private void plusCapability(Capability<?> capability, Object handler, Direction[] directionsArray) {
        if (!lazyOptionalMap.containsKey(capability)){
            handlerMap.put(capability, handler);
            lazyOptionalMap.put(capability, LazyOptional.of(() -> handler));
            sideMap.put(capability, Arrays.stream(directionsArray).toList());
        }
    }

    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side){
        if (lazyOptionalMap.containsKey(capability) && (side == null || sideMap.get(capability).contains(side))){
            if (sideSpecialMap.containsKey(capability) && sideSpecialMap.get(capability).containsKey(side)){
                return sideSpecialMap.get(capability).get(side).cast();
            }
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
}
