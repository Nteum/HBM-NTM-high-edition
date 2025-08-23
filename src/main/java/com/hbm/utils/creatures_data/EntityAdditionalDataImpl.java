package com.hbm.utils.creatures_data;

import com.hbm.HBM;
import com.hbm.utils.NBTUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class EntityAdditionalDataImpl implements IEntityAdditionalData{
    Map<DataEntry, Object> dataEntries;
    // 用于暂存尚不需要实例化的nbt
    Map<DataEntry, CompoundTag> tagData;
    // 数据变化有待同步的数据
    Set<DataEntry> changed;
    public EntityAdditionalDataImpl(){
        this.dataEntries = new HashMap<>();
        this.tagData = new HashMap<>();
        this.changed = new HashSet<>();
    }

    @Override
    public Map<DataEntry, Object> getEntries() {
        return dataEntries;
    }
    /**
     * 用于判断没有内容的纯标签数据是否存在
     * */
    public boolean hasEntry(DataEntry entry){
        return this.dataEntries.containsKey(entry);
    }
    /**
     * 获取特定dataentry的数据
     * 只用于获取有数据的dataentry，本来就无数据的纯标签entry请用hasEntry
     * */
    @Override
    public <T> Optional<T> getData(DataEntry entry, Class<T> type) {
        if (type == null) return Optional.empty();
        Object object = dataEntries.get(entry);
        if (tagData.containsKey(entry)){
            loadPersistentData(entry);
            object = dataEntries.get(entry);
        }
        if (type.isInstance(object)){
            return Optional.of(type.cast(object));
        }
        return Optional.empty();
    }
    public <T> Optional<?> getData(DataEntry entry){
        return getData(entry, entry.type);
    }
    public void setData(DataEntry entry, Object value){
        if ((entry.type == null && value == null) || (entry.type != null && entry.type.isInstance(value))) {
            dataEntries.put(entry, value);
            if (entry.needSync) markChange(entry);
        }
    }
    /**
     * 设置不需要值的数据项，它的值直接输入null
     * */
    public void setData(DataEntry entry){
        setData(entry, null);
    }

    public void removeData(DataEntry dataEntry){
        this.dataEntries.remove(dataEntry);
        this.tagData.remove(dataEntry);
    }

    @Override
    public void markChange(DataEntry entry) {
        changed.add(entry);
    }

    @Override
    public boolean shouldSync() {
        return !changed.isEmpty();
    }

    @Override
    public CompoundTag syncToClient(){
        CompoundTag tag = new CompoundTag();
        for (DataEntry entry : changed) {
            NBTUtils.serializeDataEntry(tag, entry, dataEntries.get(entry));
        }
        changed.clear();
        return tag;
    }

    @Override
    public void copyAfterDeath(IEntityAdditionalData old) {
        old.getEntries().forEach((key, val) -> {
            if (key.keepAfterReborn) {
                setData(key, val);
            }
        });
    }

    @Override
    public CompoundTag serializeNBT() {
        // 序列化需要持久化的数据
        CompoundTag tag = new CompoundTag();
        dataEntries.forEach((key, val) -> {
            if (key.isPersistent) NBTUtils.serializeDataEntry(tag, key, val);
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            DataEntry entry = DataEntry.values()[Integer.parseInt(key)];
            if (INBTSerializable.class.isAssignableFrom(entry.type)){
                this.dataEntries.put(entry, null);
                this.tagData.put(entry, (CompoundTag) nbt.get(entry.ordinal()+""));
            }else{
                this.dataEntries.put(entry, NBTUtils.deserializeDataEntry(nbt, entry));
            }
        }
    }
    public <T extends INBTSerializable<CompoundTag>> void loadPersistentData(DataEntry entry){
        if (this.tagData.containsKey(entry)){
            try {
                T dataHandler = (T) entry.type.newInstance();
                dataHandler.deserializeNBT(tagData.get(entry));
                tagData.remove(entry);
            }catch (Exception e){
                HBM.LOGGER.warn("Data entry:{} try invoke deserializeNBT method fail.", entry);
            }
        }
    }
}
