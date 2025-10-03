package com.hbm.addational_data;

import com.hbm.addational_data.entity.IEntityAdditionalData;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;

public interface IAdditionalData extends INBTSerializable<CompoundTag> {
    String nbtKey = "hbmdata";

    /**
     * 所有词条
     * */
    Map<DataEntry, Object> getEntries();
    /**
     * 不取值的词条直接判断是否包含
     * */
    boolean contains(DataEntry entry);
    /**
     * 获取有取值的词条
     * */
    <T> Optional<T> getData(DataEntry entry, Class<T> clazz);
    default <T>Optional<?> getData(DataEntry entry){
        return getData(entry, entry.type);
    }
    /**
     * 给词条设置值
     * */
    void setData(DataEntry entry, Object value);
    /**
     * 标记状态变化，序列化时仅写入变化的信息
     * */
    void markChange(DataEntry entry);
    /** 是否需要同步 */
    boolean shouldSync();
    /**
     * 同步客户端数据
     * */
    CompoundTag syncToClient();
}
