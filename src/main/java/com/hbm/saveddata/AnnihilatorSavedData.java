package com.hbm.saveddata;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.hbm.HBMKey;
import com.hbm.core.contents.fluid.HbmFluidType;
import com.hbm.core.contents.fluid.HBMFluids;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * Persists annihilation pool data — each pool tracks how much of each
 * item/fluid/ore-dict key has been fed into an annihilator machine,
 * and what payout is due.
 *
 * Ported from 1.7.10 WorldSavedData → 1.20.1 SavedData.
 */
public class AnnihilatorSavedData extends SavedData {

    public static final String KEY = "annihilator";

    public HashMap<String, AnnihilatorPool> pools = new HashMap<>();

    public AnnihilatorSavedData() {
        // default constructor for Factory
    }

    public static AnnihilatorSavedData load(CompoundTag nbt) {
        AnnihilatorSavedData data = new AnnihilatorSavedData();
        ListTag poolsTag = nbt.getList("pools", Tag.TAG_COMPOUND);

        for (int i = 0; i < poolsTag.size(); i++) {
            CompoundTag poolCompound = poolsTag.getCompound(i);

            String poolName = poolCompound.getString("poolname");
            AnnihilatorPool pool = new AnnihilatorPool();
            pool.deserialize(poolCompound.getList("pool", Tag.TAG_COMPOUND));

            data.pools.put(poolName, pool);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag poolsTag = new ListTag();

        for (Map.Entry<String, AnnihilatorPool> entry : this.pools.entrySet()) {
            CompoundTag poolTag = new CompoundTag();
            ListTag poolList = new ListTag();

            entry.getValue().serialize(poolList);
            poolTag.putString("poolname", entry.getKey());
            poolTag.put("pool", poolList);
            poolsTag.add(poolTag);
        }

        nbt.put("pools", poolsTag);
        return nbt;
    }

    // === Accessors ===

    public static AnnihilatorSavedData getData(Level world) {
        if (world instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage()
                .computeIfAbsent(AnnihilatorSavedData::load, AnnihilatorSavedData::new, KEY);
        }
        throw new IllegalStateException("Cannot get AnnihilatorSavedData from client level");
    }

    public AnnihilatorPool grabPool(String pool) {
        AnnihilatorPool poolInstance = pools.get(pool);
        if (poolInstance == null) {
            poolInstance = new AnnihilatorPool();
            pools.put(pool, poolInstance);
        }
        return poolInstance;
    }

    // === Push Methods ===

    /** For fluids */
    public ItemStack pushToPool(String pool, HbmFluidType type, long amount, boolean alwaysPayOut) {
        AnnihilatorPool poolInstance = grabPool(pool);
        ItemStack payout = poolInstance.increment(type, amount, alwaysPayOut);
        this.setDirty();
        return payout;
    }

    /** For items (type + meta as well as only type), also handles ore dict */
    public ItemStack pushToPool(String pool, ItemStack stack, boolean alwaysPayOut) {
        AnnihilatorPool poolInstance = grabPool(pool);

        ItemStack itemPayout = poolInstance.increment(stack.getItem(), stack.getCount(), alwaysPayOut);
        ItemStack compPayout = poolInstance.increment(stack.copyWithCount(1), stack.getCount(), alwaysPayOut);
        ItemStack dictPayout = null;

        // TODO(port): ItemStackUtil.getOreDictNames — ore dict removed in 1.20; migrate to tags
        // List<String> oreDict = ItemStackUtil.getOreDictNames(stack);
        // for(String name : oreDict) if(name != null && !name.isEmpty()) {
        //     ItemStack payout = poolInstance.increment(name, stack.getCount(), alwaysPayOut);
        //     if(payout != null) dictPayout = payout;
        // }

        this.setDirty();

        return dictPayout != null ? dictPayout : compPayout != null ? compPayout : itemPayout;
    }

    // === AnnihilatorPool ===

    public static class AnnihilatorPool {

        /**
         * Valid keys include:
         * <ul>
         * <li>Items, for wildcard</li>
         * <li>ComparableStacks, for type + meta (1.20: type only — no metadata)</li>
         * <li>FluidTypes</li>
         * <li>Strings, for ore dict keys (1.20: tag keys)</li>
         * </ul>
         */
        public HashMap<Object, BigInteger> items = new HashMap<>();

        public ItemStack increment(Object type, long amount, boolean alwaysPayOut) {
            ItemStack payout = null;
            BigInteger counter = items.get(type);
            if (counter == null) {
                counter = BigInteger.valueOf(amount);
                // TODO(port): AnnihilatorRecipes not yet ported
                // payout = AnnihilatorRecipes.getHighestPayoutFromKey(type, BigInteger.ZERO, counter);
            } else {
                BigInteger prev = counter;
                counter = counter.add(BigInteger.valueOf(amount));
                // TODO(port): AnnihilatorRecipes not yet ported
                // payout = AnnihilatorRecipes.getHighestPayoutFromKey(type, alwaysPayOut ? null : prev, counter);
            }
            items.put(type, counter);
            return payout;
        }

        public void serialize(ListTag nbt) {
            for (Map.Entry<Object, BigInteger> entry : items.entrySet()) {
                CompoundTag compound = new CompoundTag();
                serializeKey(compound, entry.getKey());
                compound.putByteArray("amount", entry.getValue().toByteArray());
                nbt.add(compound);
            }
        }

        public void deserialize(ListTag nbt) {
            try {
                for (int i = 0; i < nbt.size(); i++) {
                    CompoundTag compound = nbt.getCompound(i);
                    Object key = deserializeKey(compound);
                    if (key != null)
                        this.items.put(key, new BigInteger(compound.getByteArray("amount")));
                }
            } catch (Throwable ex) {
                // world data can be corrupted — silently skip bad entries
            }
        }

        /** Serializes a pool key into NBT based on its runtime type */
        public void serializeKey(CompoundTag nbt, Object key) {
            if (key instanceof Item) { // 0
                Item item = (Item) key;
                nbt.putByte("key", (byte) 0);
                nbt.putString("item", BuiltInRegistries.ITEM.getKey(item).toString());
            }

            if (key instanceof ItemStack stack) { // 1
                nbt.putByte("key", (byte) 1);
                nbt.put(HBMKey.ITEM, stack.serializeNBT());
            }

            if (key instanceof HbmFluidType) { // 2
                HbmFluidType type = (HbmFluidType) key;
                nbt.putByte("key", (byte) 2);
                nbt.putString("fluid", type.getName());
            }

            if (key instanceof String) { // 3
                nbt.putByte("key", (byte) 3);
                nbt.putString("dict", (String) key);
            }
        }

        /** Deserializes a pool key from NBT */
        public Object deserializeKey(CompoundTag nbt) {
            try {
                byte key = nbt.getByte("key");

                if (key == 0) { // item
                    String name = nbt.getString("item");
                    return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(name));
                }
                if (key == 1) { // comparablestack
                    String name = nbt.getString("item");
                    return ItemStack.of(nbt.getCompound(HBMKey.ITEM));
                }
                if (key == 2) { // fluidtype
                    return HBMFluids.byName(nbt.getString("fluid"));
                }
                if (key == 3) { // string (ore dict / tag key)
                    return nbt.getString("dict");
                }

            } catch (Throwable ex) {
                // silently skip corrupt entries
            }
            return null;
        }
    }
}
