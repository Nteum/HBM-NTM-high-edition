package com.hbm.utils;

import com.hbm.HBM;
import com.hbm.api.Coord4D;
import com.hbm.api.annotations.ParametersAreNotNullByDefault;
import com.hbm.addational_data.DataEntry;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

// 原版NBTUitls的基础上补充的一些内容
@ParametersAreNotNullByDefault
public class NBTHelper {

    private NBTHelper() {
    }

    public static void serializeDataEntry(CompoundTag nbt, DataEntry entry, Object value){
        if (value == null) return;
        String entryNum = String.valueOf(entry.ordinal());
        if (entry.type == null){
            nbt.putString(entryNum, "");
        } else if (entry.type.equals(Boolean.class)) {
            nbt.putBoolean(entryNum, (Boolean) value);
        } else if (entry.type.equals(Byte.class)) {
            nbt.putByte(entryNum, (Byte) value);
        } else if (entry.type.equals(Integer.class)) {
            nbt.putInt(entryNum, (Integer) value);
        } else if (entry.type.equals(Long.class)) {
            nbt.putLong(entryNum, (Long) value);
        } else if (entry.type.equals(Float.class)) {
            nbt.putFloat(entryNum, (Float) value);
        } else if (entry.type.equals(Double.class)) {
            nbt.putDouble(entryNum, (Double) value);
        } else if (entry.type.equals(String.class)) {
            nbt.putString(entryNum, (String) value);
        } else if (INBTSerializable.class.isAssignableFrom(entry.type)) {
            nbt.put(entryNum, ((INBTSerializable<?>) value).serializeNBT());
        } else {
            HBM.LOGGER.warn("Data entry:{} value can't be serialized.", entry);
        }
    }

    public static Object deserializeDataEntry(CompoundTag nbt, DataEntry entry) {
        String entryNum = String.valueOf(entry.ordinal());
        if (entry.type == null){
            return null;
        } else if (entry.type.equals(Boolean.class)) {
            return nbt.getBoolean(entryNum);
        } else if (entry.type.equals(Byte.class)) {
            return nbt.getByte(entryNum);
        } else if (entry.type.equals(Integer.class)) {
            return nbt.getInt(entryNum);
        } else if (entry.type.equals(Long.class)) {
            return nbt.getLong(entryNum);
        } else if (entry.type.equals(Float.class)) {
            return nbt.getFloat(entryNum);
        } else if (entry.type.equals(Double.class)) {
            return nbt.getDouble(entryNum);
        } else if (entry.type.equals(String.class)) {
            return nbt.getString(entryNum);
        } else if (INBTSerializable.class.isAssignableFrom(entry.type)) {
            try {
                // 对于存储nbt数据的功能，暂时仅返回nbt数据，加载可以延迟。
                return nbt.get(entryNum);
//                return entry.type.getMethod("deserializeNBT", CompoundTag.class).invoke(entry.type.newInstance(), nbt);
            }catch (Exception e){
                HBM.LOGGER.warn("Data entry:{} try invoke deserializeNBT method fail.", entry);
            }
        } else {
            HBM.LOGGER.warn("Data entry:{} value can't be deserialized.", entry);
        }
        return null;
    }

    public static void setCompoundIfPreset(CompoundTag tag, String key, Consumer<CompoundTag> consumer){
        if (tag.contains(key, Tag.TAG_COMPOUND)){
            consumer.accept(tag.getCompound(key));
        }
    }

    public static <ENUM extends Enum<ENUM>> void setEnumIfPresent(CompoundTag nbt, String key, Int2ObjectFunction<ENUM> indexLookup, Consumer<ENUM> setter) {
        if (nbt.contains(key, Tag.TAG_INT)) {
            setter.accept(indexLookup.apply(nbt.getInt(key)));
        }
    }

    public static void writeEnum(CompoundTag nbt, String key, Enum<?> e) {
        nbt.putInt(key, e.ordinal());
    }
}