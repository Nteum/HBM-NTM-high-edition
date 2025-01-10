package com.hbm.item;

import com.hbm.modsetting.capability.HBMEnergyStorage;
import com.hbm.registries.ModItems;
import com.hbm.registries.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.wrapper.InvWrapper;

public class BatteryItem extends Item {
    //用耐久度表示能量，本来打算直接和能量划等号，然而耐久度是int，而能量是long，因此这里采用比例转换
    public static final int DEFAULT_DAMAGE = 1000;
    //内部的能量存储
    public final BatteryItemData batteryItemData;
    public BatteryItem(long capacity, long maxExtract, long maxReceive, Properties pProperties) {
        super(pProperties.defaultDurability(DEFAULT_DAMAGE));
        batteryItemData = new BatteryItemData(capacity, maxExtract, maxReceive);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return !pStack.is(ModItems.BATTERY_CREATIVE.get());
    }
    public static boolean canCharge(ItemStack stack){
        return stack.getItem() instanceof BatteryItem && ((BatteryItem) stack.getItem()).batteryItemData.capacity > 0 && stack.getOrCreateTag().getLong("energy") < ((BatteryItem) stack.getItem()).batteryItemData.capacity;
    }
    //向电池充电
    public static void charge(ItemStack stack, long input){
        if (stack.getItem() instanceof BatteryItem battery){
            long capacity = battery.batteryItemData.capacity;
            if (battery.batteryItemData.maxReceive == 0)return;
            if (!stack.hasTag() || !stack.getOrCreateTag().contains("energy")){
                stack.getTag().putLong("energy", capacity);
            }else {
                long energy = stack.getTag().getLong("energy");
                long receive = Math.min(input,capacity - energy);
                if (energy < capacity){
                    long l = energy + receive;
                    stack.getTag().putLong("energy",l);
                    stack.setDamageValue(transToDamage(battery,l));
                }
            }
        }
    }
    //电池向外输电
    public static long disCharge(ItemStack stack){
        if (stack.getItem() instanceof BatteryItem battery){
            long capacity = battery.batteryItemData.capacity;
            long maxExtract = battery.batteryItemData.maxExtract;
            //用-1容量表示创造电池，它直接输出
            if (capacity == -1)return maxExtract;
            if (!stack.hasTag() || !stack.getOrCreateTag().contains("energy")){
                stack.getTag().putLong("energy", capacity - maxExtract);
                stack.setDamageValue(transToDamage(battery,capacity - maxExtract));
                return maxExtract;
            }else {
                long energy = stack.getTag().getLong("energy");
                long extract = Math.min(maxExtract,energy);
                if (extract > 0){
                    long l = energy - extract;
                    stack.getTag().putLong("energy",l);
                    stack.setDamageValue(transToDamage(battery,l));
                }
                return extract;
            }
        }
        return 0L;
    }
    private static int transToDamage(BatteryItem battery, long energy){return (int) ((1.0D - (double)energy / battery.batteryItemData.capacity)*DEFAULT_DAMAGE);}
    public static record BatteryItemData(long capacity, long maxExtract, long maxReceive){}
}
