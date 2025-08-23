package com.hbm.utils.creatures_data;

import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.function.Consumer;

public enum DataEntry {
    RADIATION(Float.class),
    POLLUTION(Float.class),
    ;
    // 数据的类型
    public final Class<?> type;
    // 是否会被序列化
    public final boolean isPersistent;
    // 需要服务端和客户端同步
    public final boolean needSync;
    // 需要玩家复活后仍然保持
    public final boolean keepAfterReborn;
    // 更新的数据
    public Consumer<Object> update;
    DataEntry(Class<?> type){
        this(type, true, false, false);
    }
    DataEntry(Class<?> type, boolean isPersistent, boolean needSync, boolean keepAfterReborn){
        this.type = type;
        this.isPersistent = isPersistent;
        this.needSync = needSync;
        this.keepAfterReborn = keepAfterReborn;
    }
}
