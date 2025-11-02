package com.hbm.addational_data;

import com.hbm.addational_data.entity.living.ContaminationEffectLists;
import com.hbm.addational_data.entity.player.RightClickCount;

import java.util.function.Consumer;

public enum DataEntry {
    RADIATION(Float.class),
    RADIATION_ENV(Float.class),
    RADIATION_BUF(Float.class),
    RADIATION_IMMUNE,
    CONTAMINATION_EFFECTS(ContaminationEffectLists.class),
    DIGMMA(Float.class),
    POLLUTION(Pollution.class),
//    POLLUTION(Float.class),
    JETPACK_ENABLE(null),

    RIGHT_CLICK_COUNT(RightClickCount.class)
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
    DataEntry(){
        this(null, true, false, false);
    }
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
