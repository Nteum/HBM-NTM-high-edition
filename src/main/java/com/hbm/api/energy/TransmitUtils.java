package com.hbm.api.energy;

import com.hbm.blockentity.interfaces.IPower;
import com.hbm.registries.HBMCaps;
import com.hbm.registries.ModItems;
import com.hbm.utils.EnumUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

// 处理LONG_ENERGY能量
public class TransmitUtils {
    // 获取方块的能量，如果不是hbm能量则转换成hbm能量。
    public static IEnergyHandler getEnergyCapability(BlockEntity be, Direction side){
        return be == null ? null : be.getCapability(HBMCaps.LONG_ENERGY, side)
                .map(handler -> handler) // 如果存在，直接返回 handler
                .orElseGet(() -> {
                    // 如果不存在，再去尝试 Forge 能量
                    return be.getCapability(ForgeCapabilities.ENERGY, side)
                            .map(fe -> new ProxyEnergyHandler(new FEAdapter(fe)))
                            .orElse(null);
                });
    }
    public static LazyOptional<IEnergyHandler> getEnergyOptional(BlockEntity be, Direction side){
        LazyOptional<IEnergyHandler> result = LazyOptional.empty();
        if (be == null) return result;
        if ((result = be.getCapability(HBMCaps.LONG_ENERGY, side)).isPresent()) return result;
        if ((result = be.getCapability(ForgeCapabilities.ENERGY, side).lazyMap(fe -> new ProxyEnergyHandler(new FEAdapter(fe)))).isPresent()) return result;
        return result;
    }
    //吸取物品槽的电力
    public static void dischargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.isEmpty())return;
        IEnergyHandler energyHandler = pBlockEntity.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            if (itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()){
                // 物品使用forge能量的情况下，就转换成hbm能量
                IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
            }
        }

        long needed = energyHandler.getEnergyContainer().getNeeded();
        if (itemStack.is(ModItems.BATTERY_CREATIVE.get())){
            energyHandler.setEnergy(energyHandler.getCapacity());
            return;
        }
        if (itemEnergy == null) {
            return;
        }
        energyHandler.receive(itemEnergy.extract(needed,false),false);
    }
    //为物品槽中的物品充电
    public static void chargeItem(BlockEntity pBlockEntity, ItemStack itemStack){
        if (itemStack.isEmpty())return;
        IEnergyHandler energyHandler = pBlockEntity.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (energyHandler == null)return;
        IEnergyHandler itemEnergy = itemStack.getCapability(HBMCaps.LONG_ENERGY).orElse(null);
        if (itemEnergy == null){
            if (itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()){
                // 物品使用forge能量的情况下，就转换成hbm能量
                IEnergyStorage FEStorage = itemStack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                itemEnergy = new ProxyEnergyHandler(new FEAdapter(FEStorage));
            }
        }

        if (itemEnergy == null) {
            return;
        }

        if (itemStack.is(ModItems.BATTERY_CREATIVE.get())){
            return;
        }
        energyHandler.extract(itemEnergy.receive(energyHandler.getStored(),false),false);
    }
    // 指某个方块仅向外提供能量，用于调试
    public static void outputOnly(BlockEntity pBlockEntity){
        IEnergyHandler energyHandler = getEnergyCapability(pBlockEntity, null);
        if (energyHandler == null || pBlockEntity.getLevel() == null)return;
        IEnergyHandler neighborHandler = null;
        for (Direction direction : Direction.values()) {
            BlockEntity neighborEntity = pBlockEntity.getLevel().getBlockEntity(pBlockEntity.getBlockPos().relative(direction));
            if (neighborEntity == null)continue;
            energyHandler = getEnergyCapability(pBlockEntity, direction);
            neighborHandler = getEnergyCapability(neighborEntity, direction.getOpposite());
            if (energyHandler == null || neighborHandler == null)continue;
            if (energyHandler.canExtract() && neighborHandler.canReceive()){
                energyHandler.extract(neighborHandler.receive(energyHandler.getStored(),false),false);
            }
        }
    }
    //电池和邻接面能量交换
    public static void batteryTransmit(@Nullable BlockEntity pBlockEntity){
        if (pBlockEntity == null || pBlockEntity.getLevel() == null)return;
        BlockEntity neighborEntity;
        IEnergyHandler[] energyHandler = new IEnergyHandler[6];
        IEnergyHandler[] neighborHandler = new IEnergyHandler[6];
        long[] transNeed = new long[]{0,0,0,0,0,0};
        int[] priority = new int[]{0,0,0,0,0,0};
        int[][] idx = new int[4][6];    //分配的四种情况的下标
        long temp, sum1, sum2, sum3, sum4;
        int temp1, temp2, temp3, temp4, sum11, sum31;
        temp1 = temp2 = temp3 = temp4 = sum11 = sum31 = 0;
        sum1 = sum2 = sum3 = sum4 = 0;
        // 1. 记录六个面需要输入和输出的能量，以及优先级
        for (int i = 0; i < 6; i++) {
            Direction direction = EnumUtils.DIRECTIONS[i];
            if ((neighborEntity = pBlockEntity.getLevel().getBlockEntity(pBlockEntity.getBlockPos().relative(direction))) == null
                    || (energyHandler[i] = getEnergyCapability(pBlockEntity, direction)) == null
                    || (neighborHandler[i] = getEnergyCapability(neighborEntity, direction.getOpposite())) == null
            ) continue;
            if (energyHandler[i].canExtract() && neighborHandler[i].canReceive()){
                if (neighborEntity instanceof IPower) {
                    temp = ((IPower) neighborEntity).getPower();
                    priority[i] = 1;
                }
                else temp = neighborHandler[i].receive(energyHandler[i].getStored(),true);
                transNeed[i] += energyHandler[i].extract(temp,true);
            }
            if (energyHandler[i].canReceive() && neighborHandler[i].canExtract()){
                if (neighborEntity instanceof IPower) {
                    temp = ((IPower) neighborEntity).getPower();
                    priority[i] = 1;
                }
                else temp = neighborHandler[i].extract(energyHandler[i].getCapacity() - energyHandler[i].getStored(),true);
                transNeed[i] -= energyHandler[i].receive(temp,true);
            }
            if (priority[i] > 0 && transNeed[i] > 0) {          //优先输出
                idx[0][temp1] = i;temp1 ++;
            }else if (priority[i] <= 0 && transNeed[i] > 0){    //非优先输出
                idx[1][temp2] = i;temp2 ++;
            }else if (priority[i] > 0 && transNeed[i] < 0){     //优先输入
                idx[2][temp3] = i;temp3 ++;
            }else if (priority[i] <= 0 && transNeed[i] < 0){    //非优先输入
                idx[3][temp4] = i;temp4 ++;
            }
        }
        //2. 分配所有需要分配的能量
        IEnergyHandler energyCapability = getEnergyCapability(pBlockEntity, null);
        long maxExtract = energyCapability.extract(Long.MAX_VALUE, true);
        long maxReceive = energyCapability.receive(Long.MAX_VALUE, true);
        //2.1优先输出项
        for (int i = 0; i < temp1; i++) {
            sum1 += transNeed[idx[0][i]];
            sum11 += priority[idx[0][i]];
        }
        sum1 = (long) Math.min(sum1, maxExtract * 0.7);
        maxExtract -= sum1;
        for (int i = 0; i < temp1; i++) {
            transNeed[idx[0][i]] *= (long) ((double)transNeed[idx[0][i]] / sum1 * (double)priority[idx[0][i]] / sum11);
        }
        //2.2非优先输出项
        for (int i = 0; i < temp2; i++) {
            sum2 += transNeed[idx[1][i]];
        }
        sum2 = Math.min(sum2, maxExtract);
        for (int i = 0; i < temp2; i++) {
            transNeed[idx[1][i]] *= (long) ((double)transNeed[idx[0][i]] / sum2);
        }
        //2.3优先输入项
        for (int i = 0; i < temp3; i++) {
            sum3 -= transNeed[idx[2][i]];
            sum31 += priority[idx[2][i]];
        }
        sum3 = (long) Math.min(sum1, maxReceive * 0.7);
        maxReceive -= sum3;
        for (int i = 0; i < temp3; i++) {
            transNeed[idx[2][i]] *= (long) (-(double)transNeed[idx[2][i]] / sum3 * (double)priority[idx[2][i]] / sum31);
        }
        //2.4非优先输入项
        for (int i = 0; i < temp4; i++) {
            sum4 -= transNeed[idx[1][i]];
        }
        sum4 = Math.min(sum4, maxReceive);
        for (int i = 0; i < temp4; i++) {
            transNeed[idx[3][i]] *= (long) (-(double)transNeed[idx[3][i]] / sum4);
        }

        //3. 处理能量的接受和输出
        for (int i = 0; i < 6; i++) {
            if (energyHandler[i] != null && neighborHandler[i] != null){
                if (transNeed[i] > 0){
                    energyHandler[i].extract(neighborHandler[i].receive(transNeed[i], false), false);
                }else if (transNeed[i] < 0){
                    energyHandler[i].receive(neighborHandler[i].extract(-transNeed[i], false), false);
                }
            }
        }
    }
    public static void batteryTransmit(LevelReader level, SidedEnergyHandler batteryHandler){
        long[] transNeed = new long[]{0,0,0,0,0,0};
        IEnergyHandler energyHandler;
        long sum1, sum2, sumPri1, sumPri2;
        for (int i = 0; i < 6; i++) {
            if (batteryHandler.sideHandlers[i] == null) continue;
            energyHandler = batteryHandler.sideHandlers[i];
            if (batteryHandler.canExtract() && energyHandler.canReceive()){
                transNeed[i] += batteryHandler.extract(energyHandler.receive(batteryHandler.getStored(), true), true);
            }else if (batteryHandler.canReceive() && energyHandler.canExtract()){
                transNeed[i] -= batteryHandler.receive(energyHandler.extract(batteryHandler.getNeeded(), true), true);
            }
        }
        sum1 = sum2 = 0;
        sumPri1 = sumPri2 = 0;
        for (int i = 0; i < 6; i++) {
            if (transNeed[i] > 0){
                sum1 += transNeed[i];
                sumPri1 += batteryHandler.priorities[i] * transNeed[i];
            }else if (transNeed[i] < 0){
                sum2 -= transNeed[i];
                sumPri2 -= batteryHandler.priorities[i] * transNeed[i];
            }
        }
        sum1 = Math.min(Math.min(batteryHandler.getEnergyContainer().getOutputLimit(), batteryHandler.getEnergyContainer().getEnergy()), sum1);
        sum2 = Math.min(Math.min(batteryHandler.getEnergyContainer().getInputLimit(), batteryHandler.getEnergyContainer().getNeeded()), sum2);
        for (int i = 0; i < 6; i++) {
            if (transNeed[i] > 0){
                transNeed[i] = sum1 * transNeed[i] * batteryHandler.priorities[i] / sumPri1;
            }else if (transNeed[i] < 0){
                transNeed[i] = sum2 * transNeed[i] * batteryHandler.priorities[i] / sumPri2;
            }
        }
        //3. 处理能量的接受和输出
        for (int i = 0; i < 6; i++) {
            if ((energyHandler = batteryHandler.sideHandlers[i]) == null) continue;
            if (transNeed[i] > 0){
                batteryHandler.extract(energyHandler.receive(transNeed[i], false), false);
            }else if (transNeed[i] < 0){
                batteryHandler.receive(energyHandler.extract(-transNeed[i], false), false);
            }
        }
    }
    public static void dischargeOnly(ICapabilityProvider provider, long amount){
        provider.getCapability(HBMCaps.LONG_ENERGY).ifPresent(iEnergyHandler -> {
            iEnergyHandler.extract(amount, false);
        });
    }
}
