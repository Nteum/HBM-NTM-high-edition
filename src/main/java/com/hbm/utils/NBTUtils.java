package com.hbm.utils;

import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

//ref:mek
public class NBTUtils {
    private NBTUtils(){}
    public static void setLongIfPresent(CompoundTag nbt, String key, LongConsumer setter) {
        if (nbt.contains(key, Tag.TAG_STRING)) {
            setter.accept(nbt.getLong(key));
        }
    }
}
