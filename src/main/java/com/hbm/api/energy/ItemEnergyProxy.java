package com.hbm.api.energy;

import net.minecraft.world.item.ItemStack;

public class ItemEnergyProxy {
    //用耐久度表示能量，本来打算直接和能量划等号，然而耐久度是int，而能量是long，因此这里采用比例转换
    public static final int DEFAULT_DAMAGE = 1000;
    public long capacity;
    public long maxExtract;
    public long maxReceive;
    public ItemEnergyProxy(){
        this(0,0,0);
    }
    public ItemEnergyProxy(long capacity, long maxExtract, long maxReceive){
        this.capacity = capacity;
        this.maxExtract = maxExtract;
        this.maxReceive = maxReceive;
    }
    public static void setEnergy(ItemStack stack, IEnergyContainer cap){
        if (stack.getItem() instanceof IItemBattery itemBattery){
            long energy = cap.getEnergy();
            stack.getOrCreateTag().putLong("energy",energy);
            stack.setDamageValue(transToDamage(itemBattery.getEnergyProxy(),energy));
        }
    }
    //向电池充电
    public static void charge(ItemStack stack, IEnergyContainer cap){
        if (stack.getItem() instanceof IItemBattery itemBattery
                && itemBattery.getEnergyProxy().capacity > 0
                && stack.getOrCreateTag().getLong("energy") < itemBattery.getEnergyProxy().capacity){
            ItemEnergyProxy energyProxy = itemBattery.getEnergyProxy();
            long input = cap.extract(energyProxy.maxReceive);
            long capacity = energyProxy.capacity;
            if (energyProxy.maxReceive == 0)return;
            if (!stack.hasTag() || !stack.getOrCreateTag().contains("energy")){
                stack.getTag().putLong("energy", capacity);
            }else {
                long energy = stack.getTag().getLong("energy");
                long receive = Math.min(input,capacity - energy);
                if (energy < capacity){
                    long l = energy + receive;
                    stack.getTag().putLong("energy",l);
                    stack.setDamageValue(transToDamage(energyProxy,l));
                }
            }
        }
    }
    //电池向外输电
    public static long disCharge(ItemStack stack){
        if (stack.getItem() instanceof IItemBattery battery){
            ItemEnergyProxy energyProxy = battery.getEnergyProxy();
            long capacity = energyProxy.capacity;
            long maxExtract = energyProxy.maxExtract;
            //用-1容量表示创造电池，它直接输出
            if (capacity == -1)return maxExtract;
            if (!stack.hasTag() || !stack.getOrCreateTag().contains("energy")){
                stack.getTag().putLong("energy", capacity - maxExtract);
                stack.setDamageValue(transToDamage(energyProxy,capacity - maxExtract));
                return maxExtract;
            }else {
                long energy = stack.getTag().getLong("energy");
                long extract = Math.min(maxExtract,energy);
                if (extract > 0){
                    long l = energy - extract;
                    stack.getTag().putLong("energy",l);
                    stack.setDamageValue(transToDamage(energyProxy,l));
                }
                return extract;
            }
        }
        return 0L;
    }
    private static int transToDamage(ItemEnergyProxy energyProxy, long energy){return (int) ((1.0D - (double)energy / energyProxy.capacity)*DEFAULT_DAMAGE);}
}
