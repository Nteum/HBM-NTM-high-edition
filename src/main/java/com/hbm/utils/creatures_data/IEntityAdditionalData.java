package com.hbm.utils.creatures_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IEntityAdditionalData extends INBTSerializable<CompoundTag> {
    static String nbtKey = "hbmdata";
    /**
     * 获取相应的词条。
     * 如果词条不需要取值，则直接
     * */
    Map<DataEntry, Object> getEntries();
    <T>Optional<T> getData(DataEntry entry, Class<T> clazz);
    /**
     * 标记状态变化，序列化时仅写入变化的信息
     * */
    void markChange(DataEntry entry);
    boolean shouldSync();
    CompoundTag syncToClient();
    /**
     * 生物死后重生获得的功能，目前只用于玩家。
     * */
    void copyAfterDeath(IEntityAdditionalData data);
}
