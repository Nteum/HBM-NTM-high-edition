package com.hbm.utils;

import com.hbm.HBM;
import com.hbm.HBMKey;
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
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

@ParametersAreNotNullByDefault
public class NBTUtils {

    private NBTUtils() {
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

    public static Optional<CompoundTag> getSafeComponent(@Nullable CompoundTag tag, String key){
        if (tag != null && tag.contains(key, Tag.TAG_COMPOUND)){
            return Optional.of(tag.getCompound(key));
        }
        return Optional.empty();
    }

    public static void savePositions(CompoundTag tag, @Nullable Collection<BlockPos> positions){
        if (positions == null || positions.isEmpty()) return;
        int[] rawArray = new int[positions.size() * 3];
        int i = 0;
        for (BlockPos pos : positions) {
            rawArray[i++] = pos.getX();
            rawArray[i++] = pos.getY();
            rawArray[i++] = pos.getZ();
        }
        tag.put(HBMKey.POSITIONS, new IntArrayTag(rawArray));
    }
    public static List<BlockPos> loadPositions(@Nullable CompoundTag tag){
        if (tag == null || !tag.contains(HBMKey.POSITIONS, Tag.TAG_INT_ARRAY)) return new ArrayList<>();
        int[] array = tag.getIntArray(HBMKey.POSITIONS);
        List<BlockPos> result = new ArrayList<>(array.length / 3);
        for (int i = 0; i < array.length; i += 3) {
            result.add(new BlockPos(array[i], array[i + 1], array[i + 2]));
        }
        return result;
    }

    public static void setByteIfPresent(CompoundTag nbt, String key, ByteConsumer setter) {
        if (nbt.contains(key, Tag.TAG_BYTE)) {
            setter.accept(nbt.getByte(key));
        }
    }

    public static void setBooleanIfPresent(CompoundTag nbt, String key, BooleanConsumer setter) {
        if (nbt.contains(key, Tag.TAG_BYTE)) {
            setter.accept(nbt.getBoolean(key));
        }
    }

    public static void setBooleanIfPresentElse(CompoundTag nbt, String key, boolean fallback, BooleanConsumer setter) {
        if (nbt.contains(key, Tag.TAG_BYTE)) {
            setter.accept(nbt.getBoolean(key));
        } else {
            setter.accept(fallback);
        }
    }

    public static void setShortIfPresent(CompoundTag nbt, String key, ShortConsumer setter) {
        if (nbt.contains(key, Tag.TAG_SHORT)) {
            setter.accept(nbt.getShort(key));
        }
    }

    public static void setIntIfPresent(CompoundTag nbt, String key, IntConsumer setter) {
        if (nbt.contains(key, Tag.TAG_INT)) {
            setter.accept(nbt.getInt(key));
        }
    }

    public static void setLongIfPresent(CompoundTag nbt, String key, LongConsumer setter) {
        if (nbt.contains(key, Tag.TAG_LONG)) {
            setter.accept(nbt.getLong(key));
        }
    }

    public static void setFloatIfPresent(CompoundTag nbt, String key, FloatConsumer setter) {
        if (nbt.contains(key, Tag.TAG_FLOAT)) {
            setter.accept(nbt.getFloat(key));
        }
    }

    public static void setDoubleIfPresent(CompoundTag nbt, String key, DoubleConsumer setter) {
        if (nbt.contains(key, Tag.TAG_DOUBLE)) {
            setter.accept(nbt.getDouble(key));
        }
    }

    public static void setByteArrayIfPresent(CompoundTag nbt, String key, Consumer<byte[]> setter) {
        if (nbt.contains(key, Tag.TAG_BYTE_ARRAY)) {
            setter.accept(nbt.getByteArray(key));
        }
    }

    public static void setStringIfPresent(CompoundTag nbt, String key, Consumer<String> setter) {
        if (nbt.contains(key, Tag.TAG_STRING)) {
            setter.accept(nbt.getString(key));
        }
    }

    public static void setListIfPresent(CompoundTag nbt, String key, int type, Consumer<ListTag> setter) {
        if (nbt.contains(key, Tag.TAG_LIST)) {
            setter.accept(nbt.getList(key, type));
        }
    }

    public static void setCompoundIfPresent(CompoundTag nbt, String key, Consumer<CompoundTag> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(nbt.getCompound(key));
        }
    }

    public static void setIntArrayIfPresent(CompoundTag nbt, String key, Consumer<int[]> setter) {
        if (nbt.contains(key, Tag.TAG_INT_ARRAY)) {
            setter.accept(nbt.getIntArray(key));
        }
    }

    public static void setLongArrayIfPresent(CompoundTag nbt, String key, Consumer<long[]> setter) {
        if (nbt.contains(key, Tag.TAG_LONG_ARRAY)) {
            setter.accept(nbt.getLongArray(key));
        }
    }

    public static void setUUIDIfPresent(CompoundTag nbt, String key, Consumer<UUID> setter) {
        if (nbt.hasUUID(key)) {
            setter.accept(nbt.getUUID(key));
        }
    }

    public static void setUUIDIfPresentElse(CompoundTag nbt, String key, Consumer<UUID> setter, Runnable notPresent) {
        if (nbt.hasUUID(key)) {
            setter.accept(nbt.getUUID(key));
        } else {
            notPresent.run();
        }
    }

    public static void setBlockPosIfPresent(CompoundTag nbt, String key, Consumer<BlockPos> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(NbtUtils.readBlockPos(nbt.getCompound(key)));
        }
    }

    public static void setCoord4DIfPresent(CompoundTag nbt, String key, Consumer<Coord4D> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(Coord4D.read(nbt.getCompound(key)));
        }
    }

    public static void setFluidStackIfPresent(CompoundTag nbt, String key, Consumer<FluidStack> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(FluidStack.loadFluidStackFromNBT(nbt.getCompound(key)));
        }
    }

//    public static void setBoxedChemicalIfPresent(CompoundTag nbt, String key, Consumer<BoxedChemical> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(BoxedChemical.read(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setGasIfPresent(CompoundTag nbt, String key, Consumer<Gas> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(Gas.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setGasStackIfPresent(CompoundTag nbt, String key, Consumer<GasStack> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(GasStack.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setInfuseTypeIfPresent(CompoundTag nbt, String key, Consumer<InfuseType> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(InfuseType.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setInfusionStackIfPresent(CompoundTag nbt, String key, Consumer<InfusionStack> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(InfusionStack.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setPigmentIfPresent(CompoundTag nbt, String key, Consumer<Pigment> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(Pigment.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setPigmentStackIfPresent(CompoundTag nbt, String key, Consumer<PigmentStack> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(PigmentStack.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setSlurryIfPresent(CompoundTag nbt, String key, Consumer<Slurry> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(Slurry.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setSlurryStackIfPresent(CompoundTag nbt, String key, Consumer<SlurryStack> setter) {
//        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
//            setter.accept(SlurryStack.readFromNBT(nbt.getCompound(key)));
//        }
//    }
//
//    public static void setFloatingLongIfPresent(CompoundTag nbt, String key, FloatingLongConsumer setter) {
//        if (nbt.contains(key, Tag.TAG_STRING)) {
//            try {
//                setter.accept(FloatingLong.parseFloatingLong(nbt.getString(key)));
//            } catch (NumberFormatException e) {
//                setter.accept(FloatingLong.ZERO);
//            }
//        }
//    }

    public static void setItemStackIfPresent(CompoundTag nbt, String key, Consumer<ItemStack> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(ItemStack.of(nbt.getCompound(key)));
        }
    }

    public static void setItemStackOrEmpty(CompoundTag nbt, String key, Consumer<ItemStack> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(ItemStack.of(nbt.getCompound(key)));
        } else {
            setter.accept(ItemStack.EMPTY);
        }
    }

    public static void setResourceLocationIfPresent(CompoundTag nbt, String key, Consumer<ResourceLocation> setter) {
        if (nbt.contains(key, Tag.TAG_STRING)) {
            ResourceLocation value = ResourceLocation.tryParse(nbt.getString(key));
            if (value != null) {
                setter.accept(value);
            }
        }
    }

    public static void setResourceLocationIfPresentElse(CompoundTag nbt, String key, Consumer<ResourceLocation> setter, Runnable notPresent) {
        if (nbt.contains(key, Tag.TAG_STRING)) {
            ResourceLocation value = ResourceLocation.tryParse(nbt.getString(key));
            if (value == null) {
                notPresent.run();
            } else {
                setter.accept(value);
            }
        }
    }

    public static <REG> void setRegistryEntryIfPresentElse(CompoundTag nbt, String key, IForgeRegistry<REG> registry, Consumer<REG> setter, Runnable notPresent) {
        setResourceLocationIfPresentElse(nbt, key, rl -> {
            REG reg = registry.getValue(rl);
            if (reg == null) {
                notPresent.run();
            } else {
                setter.accept(reg);
            }
        }, notPresent);
    }

    public static <REG> void setResourceKeyIfPresentElse(CompoundTag nbt, String key, ResourceKey<? extends Registry<REG>> registryName, Consumer<ResourceKey<REG>> setter,
          Runnable notPresent) {
        setResourceLocationIfPresentElse(nbt, key, rl -> setter.accept(ResourceKey.create(registryName, rl)), notPresent);
    }

    public static <ENUM extends Enum<ENUM>> void setEnumIfPresent(CompoundTag nbt, String key, Int2ObjectFunction<ENUM> indexLookup, Consumer<ENUM> setter) {
        if (nbt.contains(key, Tag.TAG_INT)) {
            setter.accept(indexLookup.apply(nbt.getInt(key)));
        }
    }

    public static void writeEnum(CompoundTag nbt, String key, Enum<?> e) {
        nbt.putInt(key, e.ordinal());
    }

    public static <V> V readRegistryEntry(CompoundTag nbt, String key, IForgeRegistry<V> registry, V fallback) {
        if (nbt.contains(key, Tag.TAG_STRING)) {
            ResourceLocation rl = ResourceLocation.tryParse(nbt.getString(key));
            if (rl != null) {
                V result = registry.getValue(rl);
                if (result != null) {
                    return result;
                }
            }
        }
        return fallback;
    }

    public static <V> void writeRegistryEntry(CompoundTag nbt, String key, IForgeRegistry<V> registry, V entry) {
        ResourceLocation registryName = registry.getKey(entry);
        if (registryName != null) {//Should not be null but validate it
            nbt.putString(key, registryName.toString());
        }
    }

    public static void writeResourceKey(CompoundTag nbt, String key, ResourceKey<?> entry) {
        nbt.putString(key, entry.location().toString());
    }
}