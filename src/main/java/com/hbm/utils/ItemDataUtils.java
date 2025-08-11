package com.hbm.utils;

import com.hbm.HBMKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDataUtils {
    @NotNull
    public static CompoundTag getDataMap(CompoundTag tag) {
        if (tag.contains(HBMKey.DATA, Tag.TAG_COMPOUND)) {
            return tag.getCompound(HBMKey.DATA);
        }
        CompoundTag dataMap = new CompoundTag();
        tag.put(HBMKey.DATA, dataMap);
        return dataMap;
    }
    @NotNull
    public static CompoundTag getDataMap(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return getDataMap(tag);
    }
    @Nullable
    public static CompoundTag getDataMapIfPresent(CompoundTag tag) {
        if (tag != null && tag.contains(HBMKey.DATA, Tag.TAG_COMPOUND)) {
            return tag.getCompound(HBMKey.DATA);
        }
        return null;
    }
    @Nullable
    public static CompoundTag getDataMapIfPresent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return getDataMapIfPresent(tag);
    }
    public static boolean hasData(ItemStack stack, String key, int type) {
        CompoundTag dataMap = getDataMapIfPresent(stack);
        return dataMap != null && dataMap.contains(key, type);
    }
    public static <T extends INBTSerializable<CompoundTag>> void readContainers(ItemStack stack, String containerKey, T container) {
        if (!stack.isEmpty() && hasData(stack,containerKey,Tag.TAG_COMPOUND)) {
            container.deserializeNBT((CompoundTag) getDataMap(stack).get(containerKey));
        }
    }
    public static <T extends INBTSerializable<CompoundTag>> void writeContainers(ItemStack stack, String containerKey, T container) {
        if (!stack.isEmpty()) {
            getDataMap(stack).put(containerKey,container.serializeNBT());
        }
    }
}
